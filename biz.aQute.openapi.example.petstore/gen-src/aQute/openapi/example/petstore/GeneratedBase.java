package aQute.openapi.example.petstore;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import java.time.format.DateTimeFormatter;
import java.time.ZonedDateTime;
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


// aQute OpenAPI generator version 1.0.0.201703021854
