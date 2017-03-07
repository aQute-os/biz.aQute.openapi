package gen.manualconversion;

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

public abstract class ManualconversionBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

  /*****************************************************************/

  public ManualconversionBase() {
    super(BASE_PATH);
  }

    static final public OpenAPIBase.Codec CODEC = OpenAPIBase.createOpenAPICodec();
    static {
             DateTimeFormatter idtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]X").withZone(java.time.ZoneId.of("UTC"));
       CODEC.addStringHandler(Instant.class, (i) -> idtf.format(i), (s)-> Instant.from(idtf.parse(s)));

    }

    public OpenAPIBase.Codec codec_() {
        return gen.manualconversion.ManualconversionBase.CODEC;
    }


  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( gen.manualconversion.ManualconversionBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

}


// aQute OpenAPI generator version 0
