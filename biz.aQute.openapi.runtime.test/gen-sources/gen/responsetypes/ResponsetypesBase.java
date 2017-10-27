package gen.responsetypes;

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
 * <li>{@link #file() GET /file =  File}
 * 
 * </ul>
 * 
 */

@RequireResponsetypesBase
public abstract class ResponsetypesBase extends OpenAPIBase {

public static final String BASE_PATH = "/primitives";

/**
 * 
 * GET /file = File
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract java.io.File file() throws Exception;

  /*****************************************************************/

  public ResponsetypesBase() {
    super(BASE_PATH,gen.responsetypes.ResponsetypesBase.class,
         "File                 GET    /file  RETURN OpenAPIBase.Part");
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

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "file".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          file_get_(context);
          return true;
        } 
      }

      // end file
    } 

    return false;
  }

private void file_get_(OpenAPIContext context) throws Exception{

    context.setOperation("File");

    Object result = context.call( ()-> file());
    context.setResult(result, 200);

}

}

