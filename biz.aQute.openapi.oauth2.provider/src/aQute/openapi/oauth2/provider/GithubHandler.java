package aQute.openapi.oauth2.provider;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;

import aQute.openapi.user.api.OpenAPISecurity;
import aQute.www.http.util.HttpRequest;

/**
 * To create a Github authentication you eed to create OAuth app in Github:
 * https://github.com/settings/developers
 *
 * This should give you a client_id and client_secret. You must not forget to
 * set the callback URL at the bottom of the page.
 *
 */
public class GithubHandler extends Handler {

	public static class GithubEmail {
		public String	email;
		public boolean	verified;
		public boolean	primary;
		public boolean	visibility;
	}

	GithubHandler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws URISyntaxException {
		super(logger, config, def);
	}

	static public ProviderDefinition github = new ProviderDefinition();

	static {
		try {

			github.authorization_endpoint = new URI("https://github.com/login/oauth/authorize");
			github.token_endpoint = new URI("https://github.com/login/oauth/access_token");
			github.scopes_supported = new String[] { "user:email" };

		} catch (URISyntaxException e) {
			// cannot happen
		}
	}

	@Override
	public AuthenticateResult authenticate(AccessTokenResponse accessToken, OpenAPISecurity security) throws Exception {
		String uri = HttpRequest.append("https://api.github.com/user", "access_token",
				accessToken.access_token);
		AuthenticateResult result = new AuthenticateResult();

		HttpRequest get = HttpRequest.get(uri);
		if (get.ok()) {
			String body = get.body();
			GithubEmail data = json.dec().from(body)
					.get(GithubEmail.class);

			result.user = security.getUser(idKey, data.email.toLowerCase()).orElse(null);
			if ( result.user == null) {
				result.error = ErrorEnum.x_no_such_user.toString();
				result.error_description="For email " + data.email;
			}
			return result;
		}
		result.error = ErrorEnum.x_github_get_email + "-" + get.code();
		result.error_description = get.body();
		return result;
	}

}
