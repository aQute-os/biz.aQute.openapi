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
 * <li>{@link #overrideNoSecurity() GET /overrideNoSecurity =  overrideNoSecurity}
 * 
 * <li>{@link #orAndSecurity() GET /orAndSecurity =  orAndSecurity}
 * 
 * <li>{@link #andSecurity() GET /andSecurity =  andSecurity}
 * 
 * <li>{@link #defaultSecurity() GET /defaultSecurity =  defaultSecurity}
 * 
 * <li>{@link #orSecurity() GET /orSecurity =  orSecurity}
 * 
 * </ul>
 * 
 */

@RequireApikeyBase
public abstract class ApikeyBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /overrideNoSecurity = overrideNoSecurity
 * 
 */

protected abstract void overrideNoSecurity() throws Exception;

/**
 * 
 * GET /orAndSecurity = orAndSecurity
 * 
 */

protected abstract void orAndSecurity() throws Exception;

/**
 * 
 * GET /andSecurity = andSecurity
 * 
 */

protected abstract void andSecurity() throws Exception;

/**
 * 
 * GET /defaultSecurity = defaultSecurity
 * 
 */

protected abstract void defaultSecurity() throws Exception;

/**
 * 
 * GET /orSecurity = orSecurity
 * 
 */

protected abstract void orSecurity() throws Exception;

  /*****************************************************************/



     public static OpenAPISecurityDefinition api_key =  OpenAPISecurityDefinition.apiKey("api_key", BASE_PATH, "header", "ApiKey");


     public static OpenAPISecurityDefinition oauth =  OpenAPISecurityDefinition.implicit("oauth", BASE_PATH, "http://swagger.io/api/oauth/dialog", null ,"a","b");


  public ApikeyBase() {
    super(BASE_PATH,gen.apikey.ApikeyBase.class,
         "overrideNoSecurity   GET    /overrideNoSecurity",
         "orAndSecurity        GET    /orAndSecurity",
         "andSecurity          GET    /andSecurity",
         "defaultSecurity      GET    /defaultSecurity",
         "orSecurity           GET    /orSecurity");
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

    if( index < segments.length && "overrideNoSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          overrideNoSecurity_get_(context);
          return true;
        } 
      }

      // end overrideNoSecurity
    }  else     if( index < segments.length && "orAndSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          orAndSecurity_get_(context);
          return true;
        } 
      }

      // end orAndSecurity
    }  else     if( index < segments.length && "andSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          andSecurity_get_(context);
          return true;
        } 
      }

      // end andSecurity
    }  else     if( index < segments.length && "defaultSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          defaultSecurity_get_(context);
          return true;
        } 
      }

      // end defaultSecurity
    }  else     if( index < segments.length && "orSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          orSecurity_get_(context);
          return true;
        } 
      }

      // end orSecurity
    } 

    return false;
  }

private void overrideNoSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("overrideNoSecurity");

    context.call( () -> { overrideNoSecurity(); return null; });
    context.setResult(null, 200);

}

private void orAndSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("orAndSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).verify(gen.apikey.ApikeyBase.oauth,"a").or().verify(gen.apikey.ApikeyBase.api_key).verify(gen.apikey.ApikeyBase.oauth,"a").verify();

    context.call( () -> { orAndSecurity(); return null; });
    context.setResult(null, 200);

}

private void andSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("andSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).verify(gen.apikey.ApikeyBase.oauth,"a").verify();

    context.call( () -> { andSecurity(); return null; });
    context.setResult(null, 200);

}

private void defaultSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("defaultSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).verify();

    context.call( () -> { defaultSecurity(); return null; });
    context.setResult(null, 200);

}

private void orSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("orSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).or().verify(gen.apikey.ApikeyBase.oauth,"a").verify();

    context.call( () -> { orSecurity(); return null; });
    context.setResult(null, 200);

}

}


// aQute OpenAPI generator version 0
