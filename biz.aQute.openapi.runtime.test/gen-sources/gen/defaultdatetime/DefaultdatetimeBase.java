package gen.defaultdatetime;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.util.Optional;
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

public abstract class DefaultdatetimeBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

  /*****************************************************************/

  public DefaultdatetimeBase() {
    super(BASE_PATH);
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( gen.defaultdatetime.DefaultdatetimeBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

}


// aQute OpenAPI generator version 0
