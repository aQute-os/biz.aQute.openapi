package aQute.openapi.v2.api;

import java.net.URI;

import aQute.openapi.annotations.Required;

/**
 * License information for the exposed API.
 * 
 * <pre>
 * { 
 * 	"name": "Apache 2.0", 
 * 	"url": "http://www.apache.org/licenses/LICENSE-2.0.html" 
 * }
 * </pre>
 * 
 * @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#license-object
 */
public class LicenseObject extends BaseOpenAPIObject {
	/**
	 * Required. The license name used for the API.
	 */
	@Required
	public String	name;

	/**
	 * A URL to the license used for the API. MUST be in the format of a URL.
	 */
	public URI		url;
}
