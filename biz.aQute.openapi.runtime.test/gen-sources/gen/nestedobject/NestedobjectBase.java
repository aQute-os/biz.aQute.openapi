package gen.nestedobject;

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
 * <li>{@link #nested() POST /nested =  nested}
 * 
 * </ul>
 * 
 */

@RequireNestedobjectBase
public abstract class NestedobjectBase extends OpenAPIBase {

public static final String BASE_PATH = "/nestedobject";

/**
 * 
 * POST /nested = nested
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Nested nested() throws Exception;

/**
 * 
 * Nested
 * 
 */

public static class Nested extends OpenAPIBase.DTO {

    public List<Values> values;

    public Nested values(List<Values> values){ this.values=values; return this; }
    public List<Values> values(){ return this.values; }

}

/**
 * 
 * Values
 * 
 */

public static class Values extends OpenAPIBase.DTO {

    public String tag;

    public Values tag(String tag){ this.tag=tag; return this; }
    public String tag(){ return this.tag; }

}

  /*****************************************************************/

  public NestedobjectBase() {
    super(BASE_PATH,
         "nested               POST   /nested  RETURN Nested");
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

    if( index < segments.length && "nested".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          nested_post_(context);
          return true;
        } 
      }

      // end nested
    } 

    return false;
  }

private void nested_post_(OpenAPIContext context) throws Exception{

    context.setOperation("nested");

    Object result = nested();
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 0
