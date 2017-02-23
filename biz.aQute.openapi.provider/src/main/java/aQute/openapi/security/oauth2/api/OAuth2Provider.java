package aQute.openapi.security.oauth2.api;

import aQute.openapi.provider.OpenAPIContext;

public interface OAuth2Provider {
	
	
	boolean check( OpenAPIContext context, OAuth2DTO dto, String operation, String ... requiredScopes);
	
}
