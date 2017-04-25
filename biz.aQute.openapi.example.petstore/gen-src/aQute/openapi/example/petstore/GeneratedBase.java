package aQute.openapi.example.petstore;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.OffsetDateTime;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * </ul>
 * 
 */

public abstract class GeneratedBase extends OpenAPIBase {

public static final String BASE_PATH = "/v2";

  /*****************************************************************/



     public static OpenAPISecurityDefinition petstore_auth =  OpenAPISecurityDefinition.implicit("petstore_auth", BASE_PATH, "http://petstore.swagger.io/api/oauth/dialog", null, "write:pets modify pets in your account", "read:pets read your pets");


     public static OpenAPISecurityDefinition api_key =  OpenAPISecurityDefinition.apiKey("api_key", BASE_PATH, "header", "api_key");


  public GeneratedBase() {
    super(BASE_PATH);
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( aQute.openapi.example.petstore.GeneratedBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

}


// aQute OpenAPI generator version 1.0.0.201704251535
