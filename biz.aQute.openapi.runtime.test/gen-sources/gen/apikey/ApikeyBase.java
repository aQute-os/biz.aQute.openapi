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
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String overrideNoSecurity() throws Exception;

/**
 * 
 * GET /orAndSecurity = orAndSecurity
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String orAndSecurity() throws Exception;

/**
 * 
 * GET /andSecurity = andSecurity
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String andSecurity() throws Exception;

/**
 * 
 * GET /defaultSecurity = defaultSecurity
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String defaultSecurity() throws Exception;

/**
 * 
 * GET /orSecurity = orSecurity
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String orSecurity() throws Exception;

  /*****************************************************************/



     public static OpenAPISecurityDefinition api_key =  OpenAPISecurityDefinition.apiKey("api_key", BASE_PATH, "header", "ApiKey");


     public static OpenAPISecurityDefinition oauth =  OpenAPISecurityDefinition.implicit("oauth", BASE_PATH, "http://swagger.io/api/oauth/dialog", null ,"a","b");


  public ApikeyBase() {
    super(BASE_PATH,gen.apikey.ApikeyBase.class,
         "overrideNoSecurity   GET    /overrideNoSecurity  RETURN String",
         "orAndSecurity        GET    /orAndSecurity  RETURN String",
         "andSecurity          GET    /andSecurity  RETURN String",
         "defaultSecurity      GET    /defaultSecurity  RETURN String",
         "orSecurity           GET    /orSecurity  RETURN String");
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

    if( index < segments.length && "andSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          andSecurity_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end andSecurity
    }  else     if( index < segments.length && "defaultSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          defaultSecurity_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end defaultSecurity
    }  else     if( index < segments.length && "orAndSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          orAndSecurity_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end orAndSecurity
    }  else     if( index < segments.length && "orSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          orSecurity_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end orSecurity
    }  else     if( index < segments.length && "overrideNoSecurity".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          overrideNoSecurity_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end overrideNoSecurity
    } 

    return false;
  }

private void overrideNoSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("overrideNoSecurity");

    Object result = context.call( ()-> overrideNoSecurity());
    context.setResult(result, 200);

}

private void orAndSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("orAndSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).verify(gen.apikey.ApikeyBase.oauth,"a").or().verify(gen.apikey.ApikeyBase.api_key).verify(gen.apikey.ApikeyBase.oauth,"a").verify();

    Object result = context.call( ()-> orAndSecurity());
    context.setResult(result, 200);

}

private void andSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("andSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).verify(gen.apikey.ApikeyBase.oauth,"a").verify();

    Object result = context.call( ()-> andSecurity());
    context.setResult(result, 200);

}

private void defaultSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("defaultSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).verify();

    Object result = context.call( ()-> defaultSecurity());
    context.setResult(result, 200);

}

private void orSecurity_get_(OpenAPIContext context) throws Exception{

    context.setOperation("orSecurity");
    context.verify(gen.apikey.ApikeyBase.api_key).or().verify(gen.apikey.ApikeyBase.oauth,"a").verify();

    Object result = context.call( ()-> orSecurity());
    context.setResult(result, 200);

}

}

