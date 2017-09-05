package gen.basicauth;

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
 * <li>{@link #unauthenticated() GET unauthenticated =  unauthenticated}
 * 
 * </ul>
 * 
 */

@RequireBasicauthBase
public abstract class BasicauthBase extends OpenAPIBase {

public static final String BASE_PATH = "/basic";

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
 * GET unauthenticated = unauthenticated
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract void unauthenticated() throws Exception;

  /*****************************************************************/



     public static OpenAPISecurityDefinition basicauth =  OpenAPISecurityDefinition.basic("basicauth",BASE_PATH);


  public BasicauthBase() {
    super(BASE_PATH,gen.basicauth.BasicauthBase.class,
         "authenticated        GET    /authenticated",
         "unauthenticated      GET    unauthenticated");
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
      }

      // end authenticated
    }  else     if( index < segments.length && "nauthenticated".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          unauthenticated_get_(context);
          return true;
        } 
      }

      // end nauthenticated
    } 

    return false;
  }

private void authenticated_get_(OpenAPIContext context) throws Exception{

    context.setOperation("authenticated");
    context.verify(gen.basicauth.BasicauthBase.basicauth).verify();

    context.call( () -> { authenticated(); return null; });
    context.setResult(null, 200);

}

private void unauthenticated_get_(OpenAPIContext context) throws Exception{

    context.setOperation("unauthenticated");

    context.call( () -> { unauthenticated(); return null; });
    context.setResult(null, 200);

}

}

