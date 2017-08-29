package aQute.openapi.oauth2.example;

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
 * <li>{@link #authenticated(String) GET /authenticated/<b>[action]</b> =  authenticated}
 * 
 * <li>{@link #unauthenticated(String) GET /unauthenticated/<b>[action]</b> =  unauthenticated}
 * 
 * </ul>
 * 
 */

@RequireOAuth2Base
public abstract class OAuth2Base extends OpenAPIBase {

public static final String BASE_PATH = "/openapi/security/google";

/**
 * 
 * GET /authenticated/{action} = authenticated
 * 
 * @param action –  (path) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract boolean authenticated(String action) throws Exception;

/**
 * 
 * GET /unauthenticated/{action} = unauthenticated
 * 
 * @param action –  (path) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract boolean unauthenticated(String action) throws Exception;

  /*****************************************************************/



     public static OpenAPISecurityDefinition google =  OpenAPISecurityDefinition.accessCode("google", BASE_PATH, "", "" ,"openid","email");


  public OAuth2Base() {
    super(BASE_PATH,aQute.openapi.oauth2.example.OAuth2Base.class,
         "authenticated        GET    /authenticated/{action}  RETURN boolean",
         "unauthenticated      GET    /unauthenticated/{action}  RETURN boolean");
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

      if ( index < segments.length ) {
        context.pathParameter("action",segments[index]);
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            authenticated_get_(context);
            return true;
          } 
        }


      }      // end authenticated
    }  else     if( index < segments.length && "unauthenticated".equals(segments[index])) {
      index++;

      if ( index < segments.length ) {
        context.pathParameter("action",segments[index]);
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            unauthenticated_get_(context);
            return true;
          } 
        }


      }      // end unauthenticated
    } 

    return false;
  }

private void authenticated_get_(OpenAPIContext context) throws Exception{

    context.setOperation("authenticated");
    context.verify(aQute.openapi.oauth2.example.OAuth2Base.google).verify();
String action_ = context.toString(context.path("action"));


    //  VALIDATORS 

    context.begin("authenticated");
    context.end();

    Object result = context.call( ()-> authenticated(action_));
    context.setResult(result, 200);

}

private void unauthenticated_get_(OpenAPIContext context) throws Exception{

    context.setOperation("unauthenticated");
String action_ = context.toString(context.path("action"));


    //  VALIDATORS 

    context.begin("unauthenticated");
    context.end();

    Object result = context.call( ()-> unauthenticated(action_));
    context.setResult(result, 200);

}

}

