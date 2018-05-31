package gen.cors;

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
 * <li>{@link #a_b_get() GET /a/b =  a_b_get}
 * 
 * <li>{@link #a_put() PUT /a =  a_put}
 * 
 * <li>{@link #a_b_post() POST /a/b =  a_b_post}
 * 
 * <li>{@link #a_get(String) GET /a =  a_get}
 * 
 * <li>{@link #a_post() POST /a =  a_post}
 * 
 * </ul>
 * 
 */

@RequireCorsBase
public abstract class CorsBase extends OpenAPIBase {

public static final String BASE_PATH = "/routes";

/**
 * 
 * GET /a/b = a_b_get
 * 
 */

protected abstract void a_b_get() throws Exception;

/**
 * 
 * PUT /a = a_put
 * 
 */

protected abstract void a_put() throws Exception;

/**
 * 
 * POST /a/b = a_b_post
 * 
 */

protected abstract void a_b_post() throws Exception;

/**
 * 
 * GET /a = a_get
 * 
 * @param foo –  (query) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String a_get(String foo) throws Exception;

/**
 * 
 * POST /a = a_post
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String a_post() throws Exception;

  /*****************************************************************/

  public CorsBase() {
    super(BASE_PATH,gen.cors.CorsBase.class,
         "a_b_get              GET    /a/b",
         "a_put                PUT    /a",
         "a_b_post             POST   /a/b",
         "a_get                GET    /a?foo  RETURN String",
         "a_post               POST   /a  RETURN String");
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

    if( index < segments.length && "a".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          a_put_put_(context);
          return true;
        }  else         if ( context.isMethod(OpenAPIBase.Method.POST)) {
          a_post_post_(context);
          return true;
        }  else         if ( context.isMethod(OpenAPIBase.Method.GET)) {
          a_get_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("PUT", "POST", "GET");

      } else       if( index < segments.length && "b".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.POST)) {
            a_b_post_post_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.GET)) {
            a_b_get_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("POST", "GET");

        }

        // end b
      } 

      // end a
    } 

    return false;
  }

private void a_b_get_get_(OpenAPIContext context) throws Exception{

    context.setOperation("a_b_get");

    context.call( () -> { a_b_get(); return null; });
    context.setResult(null, 200);

}

private void a_put_put_(OpenAPIContext context) throws Exception{

    context.setOperation("a_put");

    context.call( () -> { a_put(); return null; });
    context.setResult(null, 200);

}

private void a_b_post_post_(OpenAPIContext context) throws Exception{

    context.setOperation("a_b_post");

    context.call( () -> { a_b_post(); return null; });
    context.setResult(null, 200);

}

private void a_get_get_(OpenAPIContext context) throws Exception{

    context.setOperation("a_get");
String foo_ = context.toString(context.parameter("foo"));


    //  VALIDATORS 

    context.begin("a_get");
    context.end();

    Object result = context.call( ()-> a_get(foo_));
    context.setResult(result, 200);

}

private void a_post_post_(OpenAPIContext context) throws Exception{

    context.setOperation("a_post");

    Object result = context.call( ()-> a_post());
    context.setResult(result, 200);

}

}

