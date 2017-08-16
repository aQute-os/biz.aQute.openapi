package gen.simple;

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
 * <li>{@link #simple() GET /simple =  simple}
 * 
 * </ul>
 * 
 */

@RequireSimpleBase
public abstract class SimpleBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /simple = simple
 * 
 */

protected abstract void simple() throws Exception;

  /*****************************************************************/

  public SimpleBase() {
    super(BASE_PATH,gen.simple.SimpleBase.class,
         "simple               GET    /simple");
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

    if( index < segments.length && "simple".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          simple_get_(context);
          return true;
        } 
      }

      // end simple
    } 

    return false;
  }

private void simple_get_(OpenAPIContext context) throws Exception{

    context.setOperation("simple");

    context.call( () -> { simple(); return null; });
    context.setResult(null, 200);

}

}

