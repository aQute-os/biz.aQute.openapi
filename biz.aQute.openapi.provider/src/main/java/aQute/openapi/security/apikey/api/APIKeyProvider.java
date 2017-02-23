package aQute.openapi.security.apikey.api;

import aQute.openapi.provider.OpenAPIContext;

public interface APIKeyProvider {
	

	boolean check(OpenAPIContext context, APIKeyDTO dto, String operation, String headerValue);
}
