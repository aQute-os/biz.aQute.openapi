package aQute.openapi.v2.api;

import java.util.List;

import javax.xml.crypto.dsig.XMLObject;

public class ItemsObject extends BaseOpenAPIObject {

	/**
	 * Required. The internal type of the array. The value MUST be one of
	 * "string", "number", "integer", "boolean", or "array". Files and models
	 * are not allowed.
	 */
	public String		type;
	/**
	 * The extending format for the previously mentioned type. See Data Type
	 * Formats for further details.
	 */
	public String		format;

	/**
	 * Items Object Required if type is "array". Describes the type of items in
	 * the array.
	 */
	public SchemaObject	items;

	/**
	 * string Determines the format of the array if type array is used. Possible
	 * values are: csv - comma separated values foo,bar. ssv - space separated
	 * values foo bar. tsv - tab separated values foo\tbar. pipes - pipe
	 * separated values foo|bar. Default value is csv.
	 */
	public CollectionFormat		collectionFormat;

	/**
	 * Declares the value of the item that the server will use if none is
	 * provided. (Note: "default" has no meaning for required items.) See
	 * http://json-schema.org/latest/json-schema-validation.html#anchor101.
	 * Unlike JSON Schema this value MUST conform to the defined type for the
	 * data type.
	 */
	public String		default$;

	/**
	 * The value of "maximum" MUST be a number, representing an upper limit for
	 * a numeric instance.
	 * 
	 * If the instance is a number, then this keyword validates if
	 * "exclusiveMaximum" is true and instance is less than the provided value,
	 * or else if the instance is less than or exactly equal to the provided
	 * value.
	 */
	public double		maximum				= Double.NaN;
	/**
	 * The value of "minimum" MUST be a number, representing a lower limit for a
	 * numeric instance.
	 * 
	 * If the instance is a number, then this keyword validates if
	 * "exclusiveMinimum" is true and instance is greater than the provided
	 * value, or else if the instance is greater than or exactly equal to the
	 * provided value.
	 */
	public double		minimum				= Double.NaN;

	/**
	 * The value of "exclusiveMaximum" MUST be a boolean, representing whether
	 * the limit in "maximum" is exclusive or not. An undefined value is the
	 * same as false.
	 * 
	 * If "exclusiveMaximum" is true, then a numeric instance SHOULD NOT be
	 * equal to the value specified in "maximum". If "exclusiveMaximum" is false
	 * (or not specified), then a numeric instance MAY be equal to the value of
	 * "maximum".
	 */
	public boolean		exclusiveMaximum	= false;
	/**
	 * The value of "exclusiveMinimum" MUST be a boolean, representing whether
	 * the limit in "minimum" is exclusive or not. An undefined value is the
	 * same as false.
	 * 
	 * If "exclusiveMinimum" is true, then a numeric instance SHOULD NOT be
	 * equal to the value specified in "minimum". If "exclusiveMinimum" is false
	 * (or not specified), then a numeric instance MAY be equal to the value of
	 * "minimum".
	 */
	public boolean		exclusiveMinimum	= false;

	/**
	 * The value of this keyword MUST be a non-negative integer.
	 * 
	 * The value of this keyword MUST be an integer. This integer MUST be
	 * greater than, or equal to, 0.
	 * 
	 * A string instance is valid against this keyword if its length is less
	 * than, or equal to, the value of this keyword.
	 * 
	 * The length of a string instance is defined as the number of its
	 * characters as defined by RFC 7159 [RFC7159].
	 */
	public int			maxLength = -1;
	/**
	 * A string instance is valid against this keyword if its length is greater
	 * than, or equal to, the value of this keyword.
	 * 
	 * The length of a string instance is defined as the number of its
	 * characters as defined by RFC 7159 [RFC7159].
	 * 
	 * The value of this keyword MUST be an integer. This integer MUST be
	 * greater than, or equal to, 0.
	 * 
	 * "minLength", if absent, may be considered as being present with integer
	 * value 0.
	 */
	public int			minLength = -1;

	/**
	 * The value of this keyword MUST be a string. This string SHOULD be a valid
	 * regular expression, according to the ECMA 262 regular expression dialect.
	 * 
	 * A string instance is considered valid if the regular expression matches
	 * the instance successfully. Recall: regular expressions are not implicitly
	 * anchored.
	 * 
	 */
	public String		pattern;

	/**
	 * The value of "multipleOf" MUST be a number, strictly greater than 0.
	 * 
	 * A numeric instance is only valid if division by this keyword's value
	 * results in an integer.
	 */
	public int			multipleOf			= 0;

	public List<Object>	enum$;

	/**
	 * An array instance is valid against "maxItems" if its size is less than,
	 * or equal to, the value of this keyword.
	 */
	public int			maxItems = -1;
	/**
	 * An array instance is valid against "minItems" if its size is greater
	 * than, or equal to, the value of this keyword.
	 */
	public int			minItems			= -1;
	/**
	 * 
	 * If this keyword has boolean value false, the instance validates
	 * successfully. If it has boolean value true, the instance validates
	 * successfully if all of its elements are unique.
	 * 
	 * 
	 */
	public boolean		uniqueItems;
	public String		description;
	/**
	 * This MAY be used only on properties schemas. It has no effect on root
	 * schemas. Adds Additional metadata to describe the XML representation
	 * format of this property.
	 */
	public XMLObject	xml					= null;

	/**
	 * A free-form property to include an example of an instance for this
	 * schema.
	 */
	public Object		example;
}
