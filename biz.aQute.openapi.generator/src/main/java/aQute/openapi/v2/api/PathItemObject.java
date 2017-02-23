package aQute.openapi.v2.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Describes the operations available on a single path. A Path Item may be
 * empty, due to ACL constraints. The path itself is still exposed to the
 * documentation viewer but they will not know which operations and parameters
 * are available.
 * 
 * 
 * <pre>
 * {
"get": {
"description": "Returns pets based on ID",
"summary": "Find pets by ID",
"operationId": "getPetsById",
"produces": [
  "application/json",
  "text/html"
],
"responses": {
  "200": {
    "description": "pet response",
    "schema": {
      "type": "array",
      "items": {
        "$ref": "#/definitions/Pet"
      }
    }
  },
  "default": {
    "description": "error payload",
    "schema": {
      "$ref": "#/definitions/ErrorModel"
    }
  }
}
},
"parameters": [
{
  "name": "id",
  "in": "path",
  "description": "ID of pet to use",
  "required": true,
  "type": "array",
  "items": {
    "type": "string"
  },
  "collectionFormat": "csv"
}
]
}
 * </pre>
 * 
 * @see https://github.com/OAI/OpenAPI-Specification/blob/master/versions/2.0.md#path-item-object
 *
 */
public class PathItemObject extends BaseOpenAPIObject {

	/**
	 * A definition of a GET operation on this path.
	 */
	public OperationObject	get;
	/**
	 * A definition of a PUT operation on this path.
	 */
	public OperationObject	put;
	/**
	 * A definition of a POST operation on this path.
	 */
	public OperationObject	post;
	/**
	 * A definition of a DELETE operation on this path.
	 */
	public OperationObject	delete;
	/**
	 * A definition of a OPTIONS operation on this path.
	 */
	public OperationObject	options;
	/**
	 * A definition of a HEAD operation on this path.
	 */
	public OperationObject	head;
	/**
	 * A definition of a PATCH operation on this path.
	 */
	public OperationObject	patch;

	/**
	 * A list of parameters that are applicable for all the operations described
	 * under this path. These parameters can be overridden at the operation
	 * level, but cannot be removed there. The list MUST NOT include duplicated
	 * parameters. A unique parameter is defined by a combination of a name and
	 * location. The list can use the Reference Object to link to parameters
	 * that are defined at the Swagger Object's parameters. There can be one
	 * "body" parameter at most.
	 */
	public List<ParameterObject>		parameters	= new ArrayList<>();
	
	/**
	 * Fixup structures to provide collections of operations and implemented methods. Not part of specification
	 */
	public Map<MethodEnum,OperationObject> operations = new HashMap<>();
	
	/**
	 * Fixup to provide path in path item
	 */
	
	public String path;
	
	
}
