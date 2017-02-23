package aQute.openapi.v2.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.openapi.annotations.Required;

/**
 * Swagger Object
 * 
 * This is the root document object for the API specification.It combines what
 * previously was the Resource Listing and API Declaration(version 1.2 and
 * earlier)together into one document.
 * 
 * @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#swagger-object
 */
public class SwaggerObject extends BaseOpenAPIObject {

	/**
	 * Required. Specifies the Swagger Specification version being used. It can
	 * be used by the Swagger UI and other clients to interpret the API listing.
	 * The value MUST be "2.0".
	 */
	@Required
	public String							swagger		= "2.0.0";

	/**
	 * Required. Provides metadata about the API. The metadata can be used by
	 * the clients if needed.
	 */
	@Required
	public InfoObject						info;

	/**
	 * An array of Host objects which provide scheme, host, port, and basePath
	 * in an associative manner.
	 */
	public String							host;

	/**
	 * The base path on which the API is served, which is relative to the host.
	 * If it is not included, the API is served directly under the host. The
	 * value MUST start with a leading slash (/). The basePath does not support
	 * path templating.
	 */
	public String							basePath;

	/**
	 * The transfer protocol of the API. Values MUST be from the list: "http",
	 * "https", "ws", "wss". If the schemes is not included, the default scheme
	 * to be used is the one used to access the Swagger definition itself.
	 */

	public List<Scheme>						schemes;

	/**
	 * A list of MIME types the APIs can consume. This is global to all APIs but
	 * can be overridden on specific API calls. Value MUST be as described under
	 * Mime Types.
	 */
	public List<String>						consumes;

	/**
	 * A list of MIME types the APIs can produce. This is global to all APIs but
	 * can be overridden on specific API calls. Value MUST be as described under
	 * Mime Types.
	 */
	public List<String>						produces;

	/**
	 * Required. The available paths and operations for the API.
	 */
	@Required
	public Map<String, PathItemObject>		paths		= new HashMap<>();

	/**
	 * An element to hold various schemas for the specification.
	 */
	public Map<String, Object>				definitions	= new HashMap<>();

	/**
	 * An object to hold parameters that can be used across operations. This
	 * property does not define global parameters for all operations.
	 */
	public Map<String, ParameterObject>		parameters	= new HashMap<>();

	/**
	 * An object to hold responses that can be used across operations. This
	 * property does not define global responses for all operations.
	 */
	public Map<String, ResponseObject>		responses	= new HashMap<>();;

	/**
	 * A declaration of which security schemes are applied for the API as a
	 * whole. The list of values describes alternative security schemes that can
	 * be used (that is, there is a logical OR between the security
	 * requirements). Individual operations can override this definition.
	 */
	public List<Map<String, List<String>>>	security;

	/**
	 * A list of tags used by the specification with additional metadata. The
	 * order of the tags can be used to reflect on their order by the parsing
	 * tools. Not all tags that are used by the Operation Object must be
	 * declared. The tags that are not declared may be organized randomly or
	 * based on the tools' logic. Each tag name in the list MUST be unique.
	 */
	public List<TagObject>					tags;

	/**
	 * Additional external documentation.
	 */
	public ExternalDocumentationObject		externalDocs;

	/**
	 * Security Definitions Object
	 * 
	 * A declaration of the security schemes available to be used in the
	 * specification. This does not enforce the security schemes on the
	 * operations and only serves to provide the relevant details for each
	 * scheme.
	 * 
	 * 
	 */
	
	public Map<String,SecuritySchemeObject> securityDefinitions;
}
