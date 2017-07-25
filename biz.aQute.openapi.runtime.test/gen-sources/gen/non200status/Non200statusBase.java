package gen.non200status;

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
 * <li>{@link #non200status() GET /non200status =  non200status}
 * 
 * <li>{@link #non200statusexception() GET /non200statusexception =  non200statusexception}
 * 
 * </ul>
 * 
 */

@RequireNon200statusBase
public abstract class Non200statusBase extends OpenAPIBase {

public static final String BASE_PATH = "/non200status";

/**
 * 
 * GET /non200status = non200status
 * 
   * @throws Response 204 / null
 * 204
 * 
 */

protected abstract void non200status() throws Exception;

/**
 * 
 * GET /non200statusexception = non200statusexception
 * 
   * @throws Response 204 / null
 * 204
 * 
 */

protected abstract void non200statusexception() throws Exception;

  /*****************************************************************/

  public Non200statusBase() {
    super(BASE_PATH,gen.non200status.Non200statusBase.class,
         "non200status         GET    /non200status",
         "non200statusexception GET    /non200statusexception");
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

    if( index < segments.length && "non200status".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          non200status_get_(context);
          return true;
        } 
      }

      // end non200status
    }  else     if( index < segments.length && "non200statusexception".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          non200statusexception_get_(context);
          return true;
        } 
      }

      // end non200statusexception
    } 

    return false;
  }

private void non200status_get_(OpenAPIContext context) throws Exception{

    context.setOperation("non200status");

    context.call( () -> { non200status(); return null; });
    context.setResult(null, 204);

}

private void non200statusexception_get_(OpenAPIContext context) throws Exception{

    context.setOperation("non200statusexception");

    context.call( () -> { non200statusexception(); return null; });
    context.setResult(null, 204);

}

}


// aQute OpenAPI generator version 1.0.0.201707241457
