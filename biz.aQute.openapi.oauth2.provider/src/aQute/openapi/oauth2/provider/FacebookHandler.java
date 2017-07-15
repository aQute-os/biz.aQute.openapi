package aQute.openapi.oauth2.provider;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;

import aQute.openapi.user.api.OpenAPISecurity;
/**
 * See https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow#checktoken
 *
 * TODO
 */
public class FacebookHandler extends Handler {

	FacebookHandler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws URISyntaxException {
		super(logger, config, def);
	}

	static public ProviderDefinition	facebook	= new ProviderDefinition();

	static {
		try {

			facebook.authorization_endpoint = new URI("https://www.facebook.com/v2.9/dialog/oauth");
			facebook.token_endpoint = new URI("https://graph.facebook.com/v2.9/oauth/access_token");
			facebook.scopes_supported = new String[] { "email" };
		} catch (URISyntaxException e) {
			// cannot happen
		}
	}

	@Override
	public AuthenticateResult authenticate(AccessTokenResponse accessToken, OpenAPISecurity security) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
