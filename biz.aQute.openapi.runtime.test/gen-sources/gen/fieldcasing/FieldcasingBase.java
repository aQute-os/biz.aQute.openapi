package gen.fieldcasing;

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
 * <li>{@link #putParameter(Body) PUT /parameter =  putParameter}
 * 
 * </ul>
 * 
 */

@RequireFieldcasingBase
public abstract class FieldcasingBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * PUT /parameter = putParameter
 * 
 * @param body –  (body) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Response putParameter(Body body) throws Exception;

/**
 * 
 * Response
 * 
 */

public static class Response extends OpenAPIBase.DTO {

    public String error;

    public Response error(String error){ this.error=error; return this; }
    public String error(){ return this.error; }

}

/**
 * 
 * Body
 * 
 */

public static class Body extends OpenAPIBase.DTO {

    public Optional<String> StartsWithUppercase = Optional.empty();

    public Body StartsWithUppercase(String StartsWithUppercase){ this.StartsWithUppercase=Optional.ofNullable(StartsWithUppercase); return this; }
    public Optional<String> StartsWithUppercase(){ return this.StartsWithUppercase; }

}

  /*****************************************************************/

  public FieldcasingBase() {
    super(BASE_PATH,gen.fieldcasing.FieldcasingBase.class,
         "putParameter         PUT    /parameter  PAYLOAD Body  RETURN Response");
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

    if( index < segments.length && "parameter".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          putParameter_put_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("PUT");

      }

      // end parameter
    } 

    return false;
  }

private void putParameter_put_(OpenAPIContext context) throws Exception{

    context.setOperation("putParameter");
Body body_ = context.body(Body.class);


    //  VALIDATORS 

    context.begin("putParameter");
    context.end();

    Object result = context.call( ()-> putParameter(body_));
    context.setResult(result, 200);

}

}

