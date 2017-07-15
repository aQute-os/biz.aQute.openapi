package biz.aQute.useradmin.authority.provider;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.Role;
import org.osgi.service.useradmin.User;

import aQute.openapi.user.api.OpenAPISecurity;
import biz.aQute.useradmin.util.UserAdminFacade;
import osgi.enroute.authorization.api.Authority;
import osgi.enroute.authorization.api.AuthorityAdmin;

@Component(service = { Authority.class, AuthorityAdmin.class, OpenAPISecurity.class }, configurationPid = UserAdminAuthority.PID)
public class UserAdminAuthority implements Authority, AuthorityAdmin, OpenAPISecurity {

	final static String PID = "biz.aQute.useradmin.authority";

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

	@Override
	public Optional<String> getProperty(String user, String key) {
		User u = userAdmin.getUser(user);
		if (u == null)
			return Optional.empty();

		Object object = u.getProperties().get(key);
		if (object == null)
			return Optional.empty();

		if (object instanceof byte[]) {
			object = Base64.getEncoder().encodeToString((byte[]) object);
		}

		return Optional.of((String) object);
	}

	@Override
	public Optional<byte[]> getCredential(String user, String key) {
		User u = userAdmin.getUser(user);
		if (u == null)
			return Optional.empty();

		Object object = u.getCredentials().get(key);
		if (object == null)
			return Optional.empty();

		if (object instanceof String) {
			object = ((String) object).getBytes(StandardCharsets.UTF_8);
		}

		return Optional.of((byte[]) object);
	}

	@Override
	public Optional<String> getUser(String key, String value) {
		User user = userAdmin.getUser(key, value);
		if (user == null)
			return Optional.empty();
		return Optional.of(user.getName());
	}

	@Override
	public <T> T dispatch(String authenticatedUser, Callable<T> request) throws Exception {
		return call(authenticatedUser, request);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setProperty(String user, String key, String value) {
		User u = userAdmin.getUser(user);
		if (u == null)
			throw new IllegalArgumentException("No such user " + u);

		u.getProperties().put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void setCredential(String user, String key, byte[] value) {
		User u = userAdmin.getUser(user);
		if (u == null)
			throw new IllegalArgumentException("No such user " + u);

		u.getCredentials().put(key, value);
	}

	@Override
	public Optional<String> getUser(String userId) {

		Role role = userAdmin.getRole(userId);
		if (role != null && role instanceof User)
			return Optional.of(role.getName());
		return Optional.empty();
	}
}
