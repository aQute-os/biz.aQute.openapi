package gen.casing;

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
 * <li>{@link #case$() POST /casing =  Case}
 * 
 * </ul>
 * 
 */

@RequireCasingBase
public abstract class CasingBase extends OpenAPIBase {

public static final String BASE_PATH = "/enums";

/**
 * 
 * POST /casing = Case
 * 
 */

protected abstract void case$() throws Exception;

  /*****************************************************************/

  public CasingBase() {
    super(BASE_PATH,gen.casing.CasingBase.class,
         "Case                 POST   /casing");
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

    if( index < segments.length && "casing".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          case$_post_(context);
          return true;
        } 
      }

      // end casing
    } 

    return false;
  }

private void case$_post_(OpenAPIContext context) throws Exception{

    context.setOperation("Case");

    context.call( () -> { case$(); return null; });
    context.setResult(null, 200);

}

}


// aQute OpenAPI generator version 0
