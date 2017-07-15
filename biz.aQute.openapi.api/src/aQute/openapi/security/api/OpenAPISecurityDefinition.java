package aQute.openapi.security.api;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;


public class OpenAPISecurityDefinition {

	public static class OAuth2Flow {
		/**
		 * string oauth2 ("implicit", "authorizationCode") Required. The
		 * authorization URL to be used for this flow. This MUST be in the form
		 * of a URL.
		 */
		public String				authorizationUrl;

		/**
		 * oauth2 ("password", "clientCredentials", "authorizationCode")
		 * Required. The token URL to be used for this flow. This MUST be in the
		 * form of a URL.
		 */
		public String				tokenUrl;

		/**
		 * string oauth2 The URL to be used for obtaining refresh tokens. This
		 * MUST be in the form of a URL.
		 */
		public String				refreshUrl;

		/**
		 * scopes Scopes Object oauth2 Required. The available scopes for the
		 * OAuth2 security scheme.
		 */
		public Map<String,String>	scopes	= new LinkedHashMap<>();

	}

	public final String	id;
	public final String	type;
	public final String	base;
	public String		description;

	// Http basic Authentication

	// API_KEY
	public String		name;
	public String		in;

	// OAUTH2
	public OAuth2Flow	implicit;
	public OAuth2Flow	password;
	public OAuth2Flow	clientCredentials;
	public OAuth2Flow	authorizationCode;

	// OPENID
	public URI			openIdConnectUrl;

	private OpenAPISecurityDefinition(String id, String type, String base) {
		this.id = id;
		this.type = type;
		this.base = base;
	}

	public static OpenAPISecurityDefinition basic(String id, String base) {
		OpenAPISecurityDefinition def = new OpenAPISecurityDefinition(id, "basic", base);
		return def;
	}

	public static OpenAPISecurityDefinition apiKey(String id, String base, String in, String name) {
		OpenAPISecurityDefinition def = new OpenAPISecurityDefinition(id, "apiKey", base);
		def.in = in;
		def.name = name;
		return def;
	}

	public static OpenAPISecurityDefinition accessCode(String id, String base, String authorizationUrl, String tokenUrl,
			String... scopes) {
		OpenAPISecurityDefinition def = new OpenAPISecurityDefinition(id, base, "authorizationCode");
		OAuth2Flow flow = new OAuth2Flow();

		def.authorizationCode = flow;
		flow.authorizationUrl = authorizationUrl;
		flow.tokenUrl = tokenUrl;

		setScopes(flow, scopes);
		return def;
	}

	public static OpenAPISecurityDefinition implicit(String id, String base, String authorizationUrl, String TODO,
			String... scopes) {
		OpenAPISecurityDefinition def = new OpenAPISecurityDefinition(id, base, "implicit");
		OAuth2Flow flow = new OAuth2Flow();

		def.implicit = flow;
		flow.authorizationUrl = authorizationUrl;

		setScopes(flow, scopes);
		return def;
	}

	public static OpenAPISecurityDefinition password(String id, String base, String tokenUrl, String... scopes) {
		OpenAPISecurityDefinition def = new OpenAPISecurityDefinition(id, base, "clientCredentials");
		OAuth2Flow flow = new OAuth2Flow();

		def.clientCredentials = flow;
		flow.tokenUrl = tokenUrl;

		setScopes(flow, scopes);
		return def;
	}

	public static OpenAPISecurityDefinition application(String id, String base, String tokenUrl,
			String... scopes) {
		OpenAPISecurityDefinition def = new OpenAPISecurityDefinition(id, base, "clientCredentials");
		OAuth2Flow flow = new OAuth2Flow();

		def.clientCredentials = flow;
		flow.tokenUrl = tokenUrl;

		setScopes(flow, scopes);
		return def;
	}

	protected static void setScopes(OAuth2Flow flow, String... scopes) {
		for (String scope : scopes) {
			int n = scope.indexOf(' ');
			if (n > 0) {
				String description = scope.substring(n + 1);
				scope = scope.substring(0, n);
				flow.scopes.put(scope, description);
			} else {
				flow.scopes.put(scope, scope);
			}
		}
	}

}
