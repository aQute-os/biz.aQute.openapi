package aQute.openapi.security.basic.api;

import aQute.openapi.provider.OpenAPIContext;

public interface BasicProvider {

	boolean check(OpenAPIContext openAPIContext, BasicDTO dto, String operation);
}
