package biz.aQute.authority.dummy.provider;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;

import osgi.enroute.authorization.api.Authority;
import osgi.enroute.authorization.api.AuthorityAdmin;

@Component(property = Constants.SERVICE_RANKING + ":Integer=" + Integer.MIN_VALUE)
public class AuthorityImpl implements Authority, AuthorityAdmin {

	@Override
	public <T> T call(String userId, Callable<T> protectedTask) throws Exception {
		return protectedTask.call();
	}

	@Override
	public String getUserId() throws Exception {
		return "user.anonymous";
	}

	@Override
	public List<String> getPermissions() throws Exception {
		return Collections.emptyList();
	}

	@Override
	public boolean hasPermission(String permission, String... arguments) throws Exception {
		return true;
	}

	@Override
	public void checkPermission(String permission, String... arguments) throws Exception {
	}

}
