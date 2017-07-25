package aQute.openapi.oauth2.provider;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition(description = "OAuth2 Authenticator", name = "OAuth2 Configuration")
public @interface OAuth2Configuration {
	@AttributeDefinition(description = "Authentication provider type", name = "Authentication Type")
	OAuth2ProviderEnum provider() default OAuth2ProviderEnum.OPENID_CONNECT;

	@AttributeDefinition(name = "Authorization Server URL", description = "Endpoint for the authorization server. If not specified, the "
			+ "authentication provider can have defaults or the OpenAPI source can specify this endpoint")
	String authorizationEndpoint() default "";

	@AttributeDefinition(name = "Token Server URL", description = "Endpoint for the token server. If not specified, the "
			+ "authentication provider can have defaults or the OpenAPI source can specify this endpoint")
	String tokenEndpoint() default "";

	@AttributeDefinition(name = "Final login/logout URL", description = "After a login/logout sequence the browser is redirected to this endpoint. If not specified the last response in the sequence receives an empty content.")
	String finalEndpoint() default "";


	@AttributeDefinition(name="Client Id",description="The identity of the client as obtained by the OAuth2 provider")
	String clientId();

	@AttributeDefinition(name="Client Secret",description="The OAuth2 provider's given secret")
	String _clientSecret();

	@AttributeDefinition(name="Name Key",description="The key used to find the user's name in the Security Environment")
	String nameKey() default "email";

	@AttributeDefinition(name="Scopes",description="Requested scopes. The OAuth2 provider or the OpenAPI source can override this field.")
	String[] scopes() default {};

	@AttributeDefinition(name="Authenticator Name",description="The name of this OAuth2 authenticator")
	String openapi_name();
}
