package biz.aQute.useradmin.authority.provider;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

import biz.aQute.useradmin.util.UserAdminFacade;
import osgi.enroute.authorization.api.Authority;
import osgi.enroute.authorization.api.AuthorityAdmin;

@Component(service = { Authority.class, AuthorityAdmin.class }, configurationPid = "biz.aQute.useradmin.authority")
public class UserAdminAuthority implements Authority, AuthorityAdmin {

	static class Context {
		User user;
	}

	@Reference
	UserAdminFacade			userAdmin;

	final ThreadLocal<User>	user	= new ThreadLocal<>();
	Role					anyone;

	@Activate
	void activate() {
		anyone = userAdmin.getRole(Role.USER_ANYONE);
	}

	@Override
	public <T> T call(String userId, Callable<T> protectedTask) throws Exception {
		User old = user.get();
		try {

			if (userId != null) {
				User user = userAdmin.getUser(userId);
				if (user == null)
					throw new IllegalStateException("Invalid user name");
				this.user.set(user);
			}

			return protectedTask.call();
		} finally {
			this.user.set(old);
		}
	}

	@Override
	public String getUserId() throws Exception {
		return getUser().getName();
	}

	protected Role getUser() {
		User user = this.user.get();
		if (user != null)
			return user;

		return anyone;
	}

	@Override
	public List<String> getPermissions() throws Exception {
		Authorization authorization = userAdmin.getAuthorization(getUser().getName());

		String[] roles = authorization.getRoles();
		if (roles == null || roles.length == 0)
			return Collections.emptyList();

		return Arrays.asList(roles);
	}

	@Override
	public boolean hasPermission(String permission, String... arguments) throws Exception {
		return userAdmin.implies(getUser(), permission, arguments);
	}

	@Override
	public void checkPermission(String permission, String... arguments) throws Exception {
		if (!hasPermission(permission, arguments))
			throw new SecurityException(
					getUserId() + " does not have permission for " + userAdmin.effective(permission, arguments));
	}
}
