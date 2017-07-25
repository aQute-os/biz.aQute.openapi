package aQute.openapi.oauth2.provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.security.SecureRandom;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.openapi.oauth2.provider.Handler.AuthenticateResult;
import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.security.api.OpenAPISecurityProviderInfo;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;
import aQute.www.http.util.HttpRequest;

@Designate(ocd = OAuth2Configuration.class, factory = true)
@Component(service = {
		OAuth2AuthenticationProvider.class,
		OpenAPIAuthenticator.class
}, configurationPid = OAuth2AuthenticationProvider.PID, configurationPolicy = ConfigurationPolicy.REQUIRE, property = OpenAPIAuthenticator.TYPE+"="+OAuth2AuthenticationProvider.OAUTH2)
public class OAuth2AuthenticationProvider implements OpenAPIAuthenticator {

	public static final String	OAUTH2				= "oauth2";
	public static final String	PID					= "biz.aQute.openapi.oauth2";
	final static Logger			logger				= LoggerFactory.getLogger(OAuth2AuthenticationProvider.class);
	final Map<String, Progress>	progress			= new ConcurrentHashMap<>();
	final static SecureRandom	random				= new SecureRandom();
	private static final long	CALLBACK_TIMEOUT	= TimeUnit.MINUTES.toMillis(10);
	URI							errorEndpoint;

	static class Progress {
		String			state	= random.nextLong() + "";;
		long			time	= System.currentTimeMillis();
		String			ip;
		public String	callback;
		public URI		success;
		public URI		fail;
	}

	Handler						handler;
	String						sessionKey;
	String						name;

	@Reference
	OpenAPISecurityEnvironment	security;

	@Activate
	public void activate(OAuth2Configuration config) throws Exception {
		ProviderDefinition def = config.provider().getProviderDefinition();
		if (def == null)
			def = new ProviderDefinition();

		handler = config.provider().handler(logger, config, def);
		sessionKey = PID + "." + config.openapi_name();
		name = config.openapi_name();
		errorEndpoint = new URI(config.finalEndpoint());
	}

	@Override
	public Authentication authenticate(HttpServletRequest request, HttpServletResponse response,
			OpenAPISecurityDefinition dto) {

		HttpSession session = request.getSession();
		String user = (String) session.getAttribute(sessionKey);

		return new Authentication() {

			@Override
			public void requestCredentials() throws Exception {

			}

			@Override
			public boolean needsCredentials() throws Exception {
				return false;
			}

			@Override
			public boolean isAuthenticated() throws Exception {
				return user != null;
			}

			@Override
			public boolean ignore() {
				return false;
			}

			@Override
			public String getUser() {
				return user;
			}
		};
	}

	@Override
	public URI login(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// check post & origin

		String user = (String) request.getSession().getAttribute(sessionKey);
		if (user != null)
			return report(ErrorEnum.ok, user, response);

		Progress p = new Progress();
		p.ip = request.getRemoteAddr() + ":" + request.getRemotePort();
		progress.put(p.state, p);

		URI base = new URI(request.getRequestURL().toString());
		URI redirect = base.resolve("callback");
		p.callback = redirect.toString();

		return handler.authorize(p.callback, p.state);
	}

	@Override
	public URI other(String command, HttpServletRequest request, HttpServletResponse response) throws Exception {

		if (!command.equals("callback")) {
			return report(ErrorEnum.x_unknown_request, command, response);
		}

		String state = request.getParameter("state");
		Progress p = progress.remove(state);
		if (p == null) {
			return report(ErrorEnum.x_no_callback_expected, "?", response);
		}

		if (p.time + CALLBACK_TIMEOUT < System.currentTimeMillis()) {
			return report(ErrorEnum.x_callback_expired,
					"Callback should happen within " + TimeUnit.MILLISECONDS.toMinutes(CALLBACK_TIMEOUT), response);
		}

		String error = request.getParameter("error");
		String error_description = request.getParameter("error_description");
		// String error_uri = request.getParameter("error_uri");
		if (error != null) {
			return report(error, error_description, response);
		}

		String code = request.getParameter("code");

		// code=4%2FXqCQpoHdoequnj1noMKbYC2Tcv7Ur_wRI4Jur4ge8YM
		// redirect_uri=https%3A%2F%2Fdevelopers.google.com%2Foauthplayground
		// client_id=407408718192.apps.googleusercontent.com
		// client_secret=************&scope=&grant_type=authorization_code

		AccessTokenResponse accessToken = handler.getAccessToken(code, p.callback);
		if (accessToken.error != null) {
			return report(accessToken.error, accessToken.body, response);
		}

		AuthenticateResult result = handler.authenticate(accessToken, security);
		if (result.error != null) {
			return report(result.error, result.error_description, response);
		}

		if (result.user != null) {
			request.getSession().setAttribute(sessionKey, result.user);
			return report(ErrorEnum.ok, name + " -> " + result.user, response);
		} else {
			return report(ErrorEnum.x_authentication_failed, result.user, response);
		}
	}

	private URI report(String error, String error_description, HttpServletResponse response) throws URISyntaxException {
		try {
			ErrorEnum e = ErrorEnum.valueOf(error);
			return report(e, error_description, response);
		} catch (IllegalArgumentException ee) {
			return report(ErrorEnum.x_unknown_error, error + ":" + error_description, response);
		}
	}

	private URI report(ErrorEnum error, String error_description, HttpServletResponse response)
			throws URISyntaxException {
		if (error != ErrorEnum.ok) {
			logger.warn("{} - {}", error, error_description);
		}

		String uri = HttpRequest.append(errorEndpoint.toString(),
				"error", error.toString(),
				"error_description", error_description);
		return new URI(uri);
	}

	@Override
	public URI logout(HttpServletRequest request, HttpServletResponse response) throws URISyntaxException {
		// check post & origin
		String user = (String) request.getSession().getAttribute(sessionKey);
		if (user != null) {
			request.getSession().removeAttribute(sessionKey);
			return report(ErrorEnum.ok, "", response);
		}
		return report(ErrorEnum.x_not_logged_in, "", response);
	}

	public void setEmail(String user, String email) {
		security.setProperty(user, handler.nameKey, email);
	}

	@Override
	public OpenAPISecurityProviderInfo getInfo(HttpServletRequest request) {
		OpenAPISecurityProviderInfo info = new OpenAPISecurityProviderInfo();
		info.name = name;
		info.type = OAUTH2;
		info.idKey = handler.nameKey;

		String user = (String) request.getSession().getAttribute(sessionKey);
		if (user != null) {
			info.currentUser = user;
			info.idValue = security.getProperty(user, handler.nameKey).orElse(null);
		}

		return info;
	}

	@Override
	public String toString() {
		return "OpenAPI.OAuth2["+name+"]";
	}
}
