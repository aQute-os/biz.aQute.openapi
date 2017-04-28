package gen.apikey;

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
 * <li>{@link #use(String) GET /operation =  use}
 * 
 * <li>{@link #login(String) PUT /operation =  login}
 * 
 * </ul>
 * 
 */

@RequireApikeyBase
public abstract class ApikeyBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /operation = use
 * 
 * @param Key –  (header)
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Response use(String Key) throws Exception;

/**
 * 
 * PUT /operation = login
 * 
 * @param Key –  (header)
 * 
   * @returns 200 / null
   * @returns 401 / null
 * 200
 * 
 * 401
 * 
 */

protected abstract Response login(Optional<String> Key) throws Exception, OpenAPIBase.UnauthorizedResponse;

/**
 * 
 * Response
 * 
 */

public static class Response extends OpenAPIBase.DTO {

    public Optional<String> error = Optional.empty();

    public Response error(String error){ this.error=Optional.ofNullable(error); return this; }
    public Optional<String> error(){ return this.error; }

}

  /*****************************************************************/



     public static OpenAPISecurityDefinition api_key =  OpenAPISecurityDefinition.apiKey("api_key", BASE_PATH, "header", "Key");


  public ApikeyBase() {
    super(BASE_PATH,
         "use                  GET    /operation  RETURN Response",
         "login                PUT    /operation  RETURN Response");
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

    if( index < segments.length && "operation".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          login_put_(context);
          return true;
        }  else         if ( context.isMethod(OpenAPIBase.Method.GET)) {
          use_get_(context);
          return true;
        } 
      }

      // end operation
    } 

    return false;
  }

private void use_get_(OpenAPIContext context) throws Exception{

    context.setOperation("use");
    context.verify(gen.apikey.ApikeyBase.api_key, context.header("Key"));

String Key_ = context.toString(context.header("Key"));


    //  VALIDATORS 

    context.begin("use");
    context.require(Key_,"Key");
    context.end();

    Object result = use(Key_);
    context.setResult(result, 200);

}

private void login_put_(OpenAPIContext context) throws Exception{

    context.setOperation("login");

Optional<String> Key_ = context.optional(context.toString(context.header("Key")));

    Object result = login(Key_);
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 0
