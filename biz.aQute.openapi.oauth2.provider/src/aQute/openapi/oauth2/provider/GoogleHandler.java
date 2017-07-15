package aQute.openapi.oauth2.provider;

import java.net.URI;
import java.net.URISyntaxException;

import org.slf4j.Logger;

/**
 * Place to set data: https://console.developers.google.com/apis/credentials/oauthclient
 *
 */
public class GoogleHandler extends OpenIdHandler {

	GoogleHandler(Logger logger, OAuth2Configuration config, ProviderDefinition def) throws URISyntaxException {
		super(logger,config, def);
	}

	static public ProviderDefinition	google	= new ProviderDefinition();

	static {
		try {

			google.authorization_endpoint = new URI("https://accounts.google.com/o/oauth2/auth");
			google.token_endpoint = new URI("https://accounts.google.com/o/oauth2/token");
			google.issuer = "https://accounts.google.com";
			google.scopes_supported = new String[] { "openid", "email" };
		} catch (URISyntaxException e) {
			// cannot happen
		}
	}
}
