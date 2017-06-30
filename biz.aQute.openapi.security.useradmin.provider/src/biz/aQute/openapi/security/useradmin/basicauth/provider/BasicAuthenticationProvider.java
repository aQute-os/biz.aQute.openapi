package biz.aQute.openapi.security.useradmin.basicauth.provider;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Collections;
import java.util.Set;

import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.useradmin.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPISecurityProvider;
import biz.aQute.useradmin.util.UserAdminFacade;

/**
 * Provides an Open API basic authenticator that stores a hashed password in
 * User Admin
 */
@Designate(ocd = BasicAuthConfiguration.class, factory = true)
@Component(service = { BasicAuthenticationProvider.class,
		OpenAPISecurityProvider.class }, configurationPid = BasicAuthenticationProvider.PID, property = "type=basic")
public class BasicAuthenticationProvider implements OpenAPISecurityProvider {
	static final String			PID		= "biz.aQute.openapi.ua.basic";
	final static Logger			logger	= LoggerFactory.getLogger(PID);
	final static SecureRandom	random	= new SecureRandom();

	public enum Hash {
		PLAIN, SHA, SHA_512, SHA_256;
	}

	private BasicAuthConfiguration	config;
	private String					idkey;
	private String					pwkey;
	private String					saltkey;

	@Reference
	UserAdminFacade					userAdmin;

	@Activate
	void activate(BasicAuthConfiguration config) {
		this.config = config;
		this.idkey = config.idkey();
		this.pwkey = config.pwkey();
		this.saltkey = pwkey + ".salt";
	}

	@Override
	public Authentication authenticate(OpenAPIContext context, OpenAPISecurityDefinition dto) {
		String auth = context.header("Authorization");
		String u = null;
		String p = null;

		if (auth != null && auth.toLowerCase().startsWith("basic")) {

			String base64Credentials = auth.substring("Basic".length()).trim();
			String credentials = new String(Base64.getDecoder().decode(base64Credentials),
					StandardCharsets.UTF_8);

			final String[] values = credentials.split(":", 2);
			if (values.length == 2) {
				u = values[0];
				p = values[1];
			}
		}

		String userId = u;
		String password = p;

		return new Authentication() {
			String user;

			@Override
			public String getUser() {
				return user;
			}

			@Override
			public boolean needsCredentials() throws Exception {
				return userId == null || !isAuthenticated();
			}

			@Override
			public void requestCredentials() throws Exception {
				logger.info("RequestCredentials: path={} user={}", context.path(), context.getOriginalIP());
				throw new OpenAPIBase.UnauthorizedResponse("Please login", OpenAPIBase.AuthenticationScheme.Basic,
						config.realm(), Collections.emptyMap());
			}

			@Override
			public boolean isAuthenticated() throws Exception {

				if (userId == null || password == null)
					return false;

				User user = BasicAuthenticationProvider.this.getUser(userId);
				if (user == null) {
					logger.debug("not authenticated: {}", userId);
					return false;
				}

				if (config.requireEncrypted() && !context.isEncrypted()) {
					logger.warn("Attempt to authenticate with basic auth over a unencrypted line. "
							+ "If this is necessary, configure PID '" + PID + ".requireEncrypted'");
					return false;
				}

				byte[] salt = (byte[]) user.getCredentials().get(saltkey);
				byte[] digest = hash(password, salt);
				byte[] expected = (byte[]) user.getCredentials().get(pwkey);

				if (digest.length != expected.length) {
					logger.warn("User={}. Stored hash has different length");
				}

				boolean authenticated = slowEquals(digest, expected);
				logger.info("Authenticated: user={} authenticated={}", userId, authenticated);
				if ( authenticated) {
					this.user = user.getName();
				}
				return authenticated;
			}

			@Override
			public boolean ignore() {
				return false;
			}

		};
	}

	/**
	 * Compare without leaking match length through timing
	 *
	 * @param a
	 * @param b
	 * @return true if equal, false if not
	 */
	private boolean slowEquals(byte[] a, byte[] b) {
		int diff = a.length ^ b.length;
		for (int i = 0; i < a.length && i < b.length; i++)
			diff |= a[i] ^ b[i];
		return diff == 0;
	}

	User getUser(String userId) {
		return idkey.isEmpty() ? (User) userAdmin.getRole(userId) : userAdmin.getUser(idkey, userId.toLowerCase());
	}

	private byte[] hash(String password, byte[] salt) throws NoSuchAlgorithmException {
		MessageDigest md;
		switch (config.hash()) {
		case PLAIN:
			return password.getBytes(StandardCharsets.UTF_8);

		case SHA:
			md = MessageDigest.getInstance("SHA-1");
			break;
		default:
		case SHA_256:
			md = MessageDigest.getInstance("SHA-256");
			break;
		case SHA_512:
			md = MessageDigest.getInstance("SHA-512");
			break;
		}

		if (salt != null) {
			md.update(salt);
		}
		md.update(password.getBytes(StandardCharsets.UTF_8));
		return md.digest();
	}

	@SuppressWarnings("unchecked")
	public String setPassword(String userAdminId, String id, String password) throws NoSuchAlgorithmException {
		User user = getUser(id);
		if (user == null) {
			if (!userAdminId.isEmpty()) {
				user = userAdmin.getUser(userAdminId);
				if (user == null) {
					user = userAdmin.createUser(userAdminId);
				}
				user.getProperties().put(idkey, id.toLowerCase());
			} else {
				return "No such user for " + idkey + "=" + id;
			}
		}

		byte[] salt = null;
		if (config.salt() > 0) {
			salt = new byte[config.salt()];
			random.nextBytes(salt);
			user.getCredentials().put(saltkey, salt);
		}
		user.getCredentials().put(pwkey, hash(password, salt));
		return id;
	}

	public String unsetPassword(String id) {
		User user = getUser(id);
		if (user == null)
			return "No such user for " + idkey + "=" + id;

		user.getCredentials().remove(saltkey);
		user.getCredentials().remove(pwkey);
		return id;
	}

	@Override
	public String toString() {
		return "BasicAuth.UA[" + config.name() + "]";
	}

	public Set<User> list() throws InvalidSyntaxException {
		if (idkey.isEmpty()) {
			return userAdmin.getUsers();
		} else {
			return userAdmin.toUsers(userAdmin.getRoles("(" + idkey + "=*)"));
		}
	}

}
