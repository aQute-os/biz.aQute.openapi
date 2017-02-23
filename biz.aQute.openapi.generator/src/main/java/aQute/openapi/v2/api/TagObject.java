package aQute.openapi.v2.api;

import aQute.openapi.annotations.Required;

/**
 * Allows adding meta data to a single tag that is used by the Operation
 * Object. It is not mandatory to have a Tag Object per tag used there.
 */
public class TagObject extends BaseOpenAPIObject {
	/**
	 * string Required. The name of the tag.
	 * 
	 */
	@Required
	public String						name;
	/**
	 * string A short description for the tag. GFM syntax can be used for
	 * rich text representation.
	 */
	public String						description;
	/**
	 * External Documentation Object Additional external documentation for
	 * this tag.
	 */
	public ExternalDocumentationObject	externalDocs;

}

