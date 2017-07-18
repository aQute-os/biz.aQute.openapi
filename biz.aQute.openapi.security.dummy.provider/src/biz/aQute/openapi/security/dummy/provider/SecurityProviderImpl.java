package biz.aQute.openapi.security.dummy.provider;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import aQute.openapi.user.api.OpenAPISecurity;

@Designate(ocd = SecurityProviderImpl.Config.class, factory = true)
@Component(name = "biz.aQute.openapi.security.dummy")
public class SecurityProviderImpl implements OpenAPISecurity {
	static class User {
		final Map<String, String>	properties	= new HashMap<>();
		final Map<String, byte[]>	credentials	= new HashMap<>();
		final String				name;

		User(String name) {
			this.name = name;
		}
	}

	Map<String, User> users = new ConcurrentHashMap<String, User>();

	@ObjectClassDefinition
	@interface Config {
	}

	@Activate
	void activate(Config config) {
	}

	@Override
	public Optional<String> getProperty(String user, String key) {
		User u = getUser0(user);
		return u == null ? Optional.empty() : Optional.of(u.name);
	}

	private User getUser0(String user) {
		User u = users.computeIfAbsent(user, k -> new User(k));
		return u;
	}

	@Override
	public void setProperty(String user, String key, String value) {
		User u = getUser0(user);

		u.properties.put(key, value);
	}

	@Override
	public void setCredential(String user, String key, byte[] value) {
		User u = getUser0(user);

		u.credentials.put(key, value);
	}

	@Override
	public Optional<byte[]> getCredential(String user, String key) {
		User u = getUser0(user);

		return Optional.ofNullable(u.credentials.get(key));
	}

	@Override
	public Optional<String> getUser(String key, String required) {
		for ( User user : users.values()) {
			String actual = user.properties.get(key);
			if ( required.equals(actual))
				return Optional.of(user.name);
		}
		return Optional.empty();
	}

	final ThreadLocal<String> user = new ThreadLocal<>();

	@Override
	public <T> T dispatch(String authenticatedUser, Callable<T> request) throws Exception {
		String old = user.get();
		user.set( authenticatedUser);
		try {
			return request.call();
		} finally {
			user.set(old);
		}
	}

	@Override
	public Optional<String> getUser(String userId) {
		return Optional.of(user.get());
	}

}
