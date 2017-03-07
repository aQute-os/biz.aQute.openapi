package gen.apikey;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import java.time.format.DateTimeFormatter;
import java.time.OffsetDateTime;
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
 * @param header –  (header)
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Response use(String header) throws Exception;

/**
 * 
 * PUT /operation = login
 * 
 * @param header –  (header)
 * 
   * @returns 200 / null
   * @returns 401 / null
 * 200
 * 
 * 401
 * 
 */

protected abstract Response login(String header) throws Exception, OpenAPIBase.UnauthorizedResponse;

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

  /*****************************************************************/

  public ApikeyBase() {
    super(BASE_PATH,
         "use                  GET    /operation  RETURN Response",
         "login                PUT    /operation  RETURN Response");


     api_key.name = "api_key";
  }
  protected aQute.openapi.security.apikey.api.APIKeyDTO api_key =  new aQute.openapi.security.apikey.api.APIKeyDTO();

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

    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( gen.apikey.ApikeyBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

private void use_get_(OpenAPIContext context) throws Exception{

    context.setOperation("use");
    context.verify(api_key, context.header("Key"));

String header_ = context.toString(context.header("header"));


    //  VALIDATORS 

    context.begin("use");
    context.require(header_,"header");
    context.end();

    Object result = use(header_);
    context.setResult(result, 200);

}

private void login_put_(OpenAPIContext context) throws Exception{

    context.setOperation("login");

String header_ = context.toString(context.header("header"));


    //  VALIDATORS 

    context.begin("login");
    context.require(header_,"header");
    context.end();

    Object result = login(header_);
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 0
