package aQute.openapi.security.api;

import aQute.openapi.provider.OpenAPIContext;

public interface OpenAPISecurityProvider {

	/**
	 * Mandatory name as used in Open API
	 */
	String	NAME	= "openapi.name";
	/**
	 * Mandatory base path as used in Open API
	 */
	String	BASE	= "openapi.base";


	Authentication authenticate(OpenAPIContext context, OpenAPISecurityDefinition dto);

}
