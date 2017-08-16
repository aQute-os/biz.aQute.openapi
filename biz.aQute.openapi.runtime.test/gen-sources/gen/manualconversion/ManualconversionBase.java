package gen.manualconversion;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.util.Optional;
import java.time.format.DateTimeFormatter;
import java.util.List;
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
    super(BASE_PATH,gen.manualconversion.ManualconversionBase.class);
  }
  public static java.time.Instant toDateTime(String s) {
    return java.time.Instant.parse(s);
  }
  public static String fromDateTime(java.time.Instant s) {
    return s.toString();
  }
  public static LocalDate toDate(String s) {
    return LocalDate.parse(s);
  }
  public static String fromDate(LocalDate s) {
    return s.toString();
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


    return false;
  }

}

