package gen.oauth2;

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
 * <li>{@link #authenticated() GET /authenticated =  authenticated}
 * 
 * <li>{@link #unauthenticated() GET /unauthenticated =  unauthenticated}
 * 
 * </ul>
 * 
 */

@RequireOauth2Base
public abstract class Oauth2Base extends OpenAPIBase {

public static final String BASE_PATH = "/oauth2";

/**
 * 
 * GET /authenticated = authenticated
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract void authenticated() throws Exception;

/**
 * 
 * GET /unauthenticated = unauthenticated
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract void unauthenticated() throws Exception;

  /*****************************************************************/



     public static OpenAPISecurityDefinition oauth2 =  OpenAPISecurityDefinition.accessCode("oauth2", BASE_PATH, "", "" ,"openid","email");


  public Oauth2Base() {
    super(BASE_PATH,gen.oauth2.Oauth2Base.class,
         "authenticated        GET    /authenticated",
         "unauthenticated      GET    /unauthenticated");
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

    if( index < segments.length && "authenticated".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          authenticated_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end authenticated
    }  else     if( index < segments.length && "unauthenticated".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          unauthenticated_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end unauthenticated
    } 

    return false;
  }

private void authenticated_get_(OpenAPIContext context) throws Exception{

    context.setOperation("authenticated");
    context.verify(gen.oauth2.Oauth2Base.oauth2).verify();

    context.call( () -> { authenticated(); return null; });
    context.setResult(null, 200);

}

private void unauthenticated_get_(OpenAPIContext context) throws Exception{

    context.setOperation("unauthenticated");

    context.call( () -> { unauthenticated(); return null; });
    context.setResult(null, 200);

}

}

