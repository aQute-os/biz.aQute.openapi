package aQute.openapi.oauth2.provider;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

@ObjectClassDefinition
public @interface OAuth2Configuration {
	@AttributeDefinition
	OAuth2ProviderEnum provider() default OAuth2ProviderEnum.OPENID_CONNECT;

	String issuer() default "";
	String origin() default "";
	String authorizationEndpoint() default "";
	String tokenEndpoint() default "";
	String finalEndpoint() default "";
	String clientId();
	String _clientSecret();
	String identityKey();
	boolean requireVerified() default true;
	boolean exposeHtmlResponse() default false;
	String[] scopes() default {};
	String name();
}

