package gen.modifieddatetime;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import java.time.format.DateTimeFormatter;
import java.time.Instant;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * </ul>
 * 
 */

public abstract class ModifieddatetimeBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

  /*****************************************************************/

  public ModifieddatetimeBase() {
    super(BASE_PATH);
  }

    static final public OpenAPIBase.Codec CODEC = OpenAPIBase.createOpenAPICodec();
    static {
               addDateTimeHandler(CODEC, LocalDate.class, "yyyy-DDD");

               addDateTimeHandler(CODEC, java.time.Instant.class, "yyyy-MM-dd'T'HH:mm:ss[.SSS]X");

    }

    public OpenAPIBase.Codec codec_() {
        return gen.modifieddatetime.ModifieddatetimeBase.CODEC;
    }


  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( gen.modifieddatetime.ModifieddatetimeBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

}


// aQute OpenAPI generator version 0
