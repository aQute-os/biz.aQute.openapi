package aQute.openapi.v2.api;

import aQute.openapi.annotations.Required;

/**
 * If in is not "body":
 */
public class ParameterObject extends ItemsObject {
	/**
	 * Required. The name of the parameter. Parameter names are case sensitive.
	 * If in is "path", the name field MUST correspond to the associated path
	 * segment from the path field in the Paths Object. See Path Templating for
	 * further information. For all other cases, the name corresponds to the
	 * parameter name used based on the in property.
	 */
	@Required
	public String		name;
	/**
	 * Required. The location of the parameter. Possible values are "query",
	 * "header", "path", "formData" or "body".
	 */
	@Required
	public In			in;
	/**
	 * A brief description of the parameter. This could contain examples of use.
	 * GFM syntax can be used for rich text representation.
	 */
	public String		description;
	/**
	 * Determines whether this parameter is mandatory. If the parameter is in
	 * "path", this property is required and its value MUST be true. Otherwise,
	 * the property MAY be included and its default value is false.
	 */
	public boolean		required	= false;

	public SchemaObject	schema;
}