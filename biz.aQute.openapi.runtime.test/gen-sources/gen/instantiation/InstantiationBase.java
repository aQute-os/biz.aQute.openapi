package gen.instantiation;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import java.time.format.DateTimeFormatter;
import java.time.OffsetDateTime;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * </ul>
 * 
 */

public abstract class InstantiationBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

  /*****************************************************************/

  public InstantiationBase() {
    super(BASE_PATH);
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( gen.instantiation.InstantiationBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

}


// aQute OpenAPI generator version 0
