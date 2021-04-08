package biz.aQute.openapi.basicauth.provider;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.security.api.OpenAPISecurityProviderInfo;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;
import aQute.openapi.util.WWWUtils;

/**
 * Provides an Open API basic authenticator that stores a hashed password in the
 * OpenAPI Security
 */
@Designate(ocd = BasicAuthConfiguration.class, factory = true)
@Component(service = { BasicAuthenticationProvider.class,
		OpenAPIAuthenticator.class }, configurationPid = BasicAuthenticationProvider.PID, property = OpenAPIAuthenticator.TYPE+"=basic", configurationPolicy = ConfigurationPolicy.REQUIRE)
public class BasicAuthenticationProvider implements OpenAPIAuthenticator {

	static final String			PID		= "biz.aQute.openapi.basicauth";
	final static Logger			logger	= LoggerFactory.getLogger(PID);
	final static SecureRandom	random	= new SecureRandom();

	public enum Hash {
		PLAIN, SHA, SHA_512, SHA_256;
	}

	private BasicAuthConfiguration	config;
	private String					idkey;
	private String					pwkey;
	private String					saltkey;
	private String					name;
	private String					sessionKey;

	@Reference
	OpenAPISecurityEnvironment					security;

	@Activate
	void activate(BasicAuthConfiguration config) {
		this.config = config;
		this.idkey = config.idkey();
		this.pwkey = config.pwkey();
		this.saltkey = config.saltkey();
		this.name = config.openapi_name();
		if (config.hash() == Hash.PLAIN) {
			logger.warn("Using plain passwords is not recommended");
		}
		this.sessionKey = PID + "." + name;
	}

	@Override
	public Authentication authenticate(HttpServletRequest request, HttpServletResponse response,
			OpenAPISecurityDefinition dto) {

		Optional<String[]> pair = WWWUtils.parseAuthorizaton(request.getHeader("Authorization"));
		String userId = pair.map(p -> p[0]).orElse(null);
		String password = pair.map(p -> p[1]).orElse(null);

		return new Authentication() {
			String user;

			@Override
			public String getUser() {
				return user;
			}

			@Override
			public boolean needsCredentials() throws Exception {
				if (config.requireEncrypted() && !WWWUtils.isEncrypted(request)) {
					return false;
				}
				return userId == null || !isAuthenticated();
			}

			@Override
			public void requestCredentials() throws Exception {
				logger.info("RequestCredentials: path={} user={}", request.getPathInfo(), request.getRemoteAddr());
				response.setHeader("WWW-Authenticate", "Basic realm=\"" + config.realm() + "\"");
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login");
			}

			@Override
			public boolean isAuthenticated() throws Exception {

				if (userId == null || password == null)
					return false;

				String user = BasicAuthenticationProvider.this.getUser(userId);
				if (user == null) {
					logger.debug("not authenticated: {}", userId);
					return false;
				}

				if (config.requireEncrypted() && !WWWUtils.isEncrypted(request)) {
					logger.warn("Attempt to authenticate with basic auth over a unencrypted line. "
							+ "If this is necessary, configure PID '" + PID + ".requireEncrypted'");
					return false;
				}

				byte[] salt = security.getCredential(user, saltkey).orElse(new byte[0]);
				byte[] digest = hash(password, salt);
				byte[] expected = security.getCredential(user, pwkey).orElse(new byte[0]);

				if (digest.length != expected.length) {
					logger.warn("User={}. Stored hash has different length");
				}

				boolean authenticated = WWWUtils.slowEquals(digest, expected);
				if (authenticated) {
					this.user = user;
				} else {
					logger.debug("Authenticated: user={} not authenticated", userId);
				}
				return authenticated;
			}

			@Override
			public boolean ignore() {
				return false;
			}

		};
	}

	String getUser(String userId) {

		Optional<String> user = idkey.isEmpty() ? security.getUser(userId)
				: security.getUser(idkey, userId.toLowerCase());

		return user.orElse(null);
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

	public String setPassword(String primary, String id, String password) throws NoSuchAlgorithmException {
		String user = getUser(id);
		if (user == null) {
			if (primary != null && !primary.isEmpty()) {
				user = security.getUser(primary).orElse(null);
				if (user == null) {
					return "Cannot find primary user " + primary;
				}
				security.setProperty(user, idkey, id.toLowerCase());
			} else {
				return "No such user for " + idkey + "=" + id;
			}
		}

		byte[] salt = null;
		if (config.salt() > 0) {
			salt = new byte[config.salt()];
			random.nextBytes(salt);
			security.setCredential(user, saltkey, salt);
		} else {
			security.setCredential(user, saltkey, null);
		}
		security.setCredential(user, pwkey, hash(password, salt));
		return id;
	}

	public String unsetPassword(String id) {
		String user = getUser(id);
		if (user == null)
			return "No such user for " + idkey + "=" + id;

		security.setCredential(user, saltkey, null);
		security.setCredential(user, pwkey, null);
		return id;
	}

	@Override
	public String toString() {
		return "OpenAPI.BasicAuth[" + config.openapi_name() + "]";
	}

	@Override
	public OpenAPISecurityProviderInfo getInfo(HttpServletRequest request) throws Exception {
		OpenAPISecurityProviderInfo info = new OpenAPISecurityProviderInfo();
		info.name = name;
		info.type = "basic";
		info.idKey = idkey;
		info.currentUser = (String) request.getSession().getAttribute(sessionKey);
		return info;
	}

	@Override
	public URI login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Authentication authenticate = authenticate(request, response, null);
		if (!authenticate.isAuthenticated()) {
			authenticate.requestCredentials();
		} else {
			request.getSession().setAttribute(sessionKey, authenticate.getUser());
		}
		String s = config.reportingEndpoint();
		if (s.isEmpty())
			return null;

		return new URI(s + "?error=ok");
	}
	@Override
	public URI logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
		request.getSession().removeAttribute(sessionKey);
		String s = config.reportingEndpoint();
		if (s.isEmpty())
			return null;
		return new URI(s + "?error=ok");
	}
}
