package aQute.openapi.oauth2.provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

import aQute.json.codec.JSONCodec;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;
import aQute.www.http.util.HttpRequest;

public abstract class Handler {
	protected final static JSONCodec	json	= new JSONCodec();

	URI									authorizationEndpoint;
	URI									tokenEndpoint;
	String								clientSecret;
	String								clientId;
	String								nameKey;
	String								scopes;
	OAuth2ProviderEnum					type;
	final Logger						logger;

	Handler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws URISyntaxException {
		this.logger = logger;
		authorizationEndpoint = config.authorizationEndpoint().isEmpty() ? def.authorization_endpoint
				: new URI(config.authorizationEndpoint());
		tokenEndpoint = config.tokenEndpoint().isEmpty() ? def.token_endpoint
				: new URI(config.tokenEndpoint());
		clientSecret = config._clientSecret();
		clientId = config.clientId();
		nameKey = config.nameKey();
		scopes = Stream.of(config.scopes()).collect(Collectors.joining(" "));
	}

	public URI authorize(String callback, String state) throws URISyntaxException {
		String url = HttpRequest.append(authorizationEndpoint.toString(),
				"response_type", "code",
				"client_id", clientId,
				"redirect_uri", callback,
				"scope", scopes,
				"state", state);

		return new URI(url);
	}

	public AccessTokenResponse getAccessToken(String code, String callback) throws Exception {

		HttpRequest post = HttpRequest.post(tokenEndpoint.toURL())
				.header("Accept", "application/json")
				.form("grant_type", "authorization_code")
				.form("code", code)
				.form("client_id", clientId)
				.form("client_secret", clientSecret)
				.form("scope", scopes)
				.form("redirect_uri", callback);

		if (post.ok()) {
			String body = post.body();
			return toAccessToken(body);
		} else {
			AccessTokenResponse resp = new AccessTokenResponse();
			resp.error = ErrorEnum.x_bad_access_token_request.toString();
			resp.body = post.body();
			return resp;
		}
	}

	protected AccessTokenResponse toAccessToken(String body) throws Exception {
		return json.dec().from(body)
				.get(AccessTokenResponse.class);
	}

	static class AuthenticateResult {
		String	error;
		String	error_description;
		String	user;
	}

	public abstract AuthenticateResult authenticate(AccessTokenResponse accessToken, OpenAPISecurityEnvironment security)
			throws Exception;

}
