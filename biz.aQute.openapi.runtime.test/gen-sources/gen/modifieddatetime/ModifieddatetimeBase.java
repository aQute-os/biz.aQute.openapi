package gen.modifieddatetime;

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

public abstract class ModifieddatetimeBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

  /*****************************************************************/

  public ModifieddatetimeBase() {
    super(BASE_PATH,gen.modifieddatetime.ModifieddatetimeBase.class);
  }
  final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss[.SSS]X", java.util.Locale.getDefault());
  public static java.time.Instant toDateTime(String s) {
    return java.time.Instant.from(dateTimeFormat.parse(s));
  }
  public static String fromDateTime(java.time.Instant s) {
    return dateTimeFormat.format(java.time.ZonedDateTime.ofInstant(s, java.time.ZoneId.of("UTC")));
  }
  final static DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-DDD");
  public static LocalDate toDate(String s) {
    return LocalDate.from(dateFormat.parse(s));
  }
  public static String fromDate(LocalDate s) {
    return dateFormat.format(s);
  }

    static final public OpenAPIBase.Codec CODEC = OpenAPIBase.createOpenAPICodec();
    static {
           CODEC.addStringHandler(LocalDate.class, ModifieddatetimeBase::fromDate, ModifieddatetimeBase::toDate);

           CODEC.addStringHandler(java.time.Instant.class, ModifieddatetimeBase::fromDateTime, ModifieddatetimeBase::toDateTime);

    }

    public OpenAPIBase.Codec codec_() {
        return gen.modifieddatetime.ModifieddatetimeBase.CODEC;
    }


  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    return false;
  }

}


// aQute OpenAPI generator version 0
