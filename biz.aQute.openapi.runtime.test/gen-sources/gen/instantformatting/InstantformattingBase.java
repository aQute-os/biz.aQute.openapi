package gen.instantformatting;

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

public abstract class InstantformattingBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

  /*****************************************************************/

  public InstantformattingBase() {
    super(BASE_PATH,gen.instantformatting.InstantformattingBase.class);
  }
  final static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("y", java.util.Locale.getDefault());
  public static java.time.Instant toDateTime(String s) {
    return java.time.Instant.from(dateTimeFormat.parse(s));
  }
  public static String fromDateTime(java.time.Instant s) {
    return dateTimeFormat.format(java.time.ZonedDateTime.ofInstant(s, java.time.ZoneId.of("UTC")));
  }
  public static LocalDate toDate(String s) {
    return LocalDate.parse(s);
  }
  public static String fromDate(LocalDate s) {
    return s.toString();
  }

    static final public OpenAPIBase.Codec CODEC = OpenAPIBase.createOpenAPICodec();
    static {
           CODEC.addStringHandler(java.time.Instant.class, InstantformattingBase::fromDateTime, InstantformattingBase::toDateTime);

    }

    public OpenAPIBase.Codec codec_() {
        return gen.instantformatting.InstantformattingBase.CODEC;
    }


  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {


    return false;
  }

}

