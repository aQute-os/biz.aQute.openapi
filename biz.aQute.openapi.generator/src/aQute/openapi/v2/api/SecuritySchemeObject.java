package aQute.openapi.v2.api;

import java.net.URI;
import java.util.Map;

/**
 * Security Scheme Object Allows the definition of a security scheme that can be
 * used by the operations. Supported schemes are basic authentication, an API
 * key (either as a header or as a query parameter) and OAuth2's common flows
 * (implicit, password, application and access code).
 */
public class SecuritySchemeObject extends BaseOpenAPIObject {
	public enum Type {
		basic, api_key, oauth2;
	};

	public enum OAuth2Flow {
		implicit, password, application, accessCode;
	}

	/**
	 * The type of the security scheme. Valid values are "basic", "apiKey" or
	 * "oauth2".
	 */
	public Type					type;

	/**
	 * A short description for security scheme.
	 */
	public String				description;

	/**
	 * Required. The name of the header or query parameter to be used (apiKey)
	 */
	public String				name;

	/**
	 * Required The location of the API key. Valid values are "query" or
	 * "header". (apiKey)
	 */
	public In					in;

	/**
	 * string oauth2 Required. The flow used by the OAuth2 security scheme.
	 * Valid values are "implicit", "password", "application" or "accessCode".
	 */
	public String				flow;

	/**
	 * string oauth2 ("implicit", "accessCode") Required. The authorization URL
	 * to be used for this flow. This SHOULD be in the form of a URL.
	 */
	public URI					authorizationUrl;

	/**
	 * string oauth2 ("password", "application", "accessCode") Required. The
	 * token URL to be used for this flow. This SHOULD be in the form of a URL.
	 */
	public URI					tokenUrl;

	/**
	 * Scopes Object oauth2 Required. The available scopes for the OAuth2
	 * security scheme.
	 */
	public Map<String,String>	scopes;
}
