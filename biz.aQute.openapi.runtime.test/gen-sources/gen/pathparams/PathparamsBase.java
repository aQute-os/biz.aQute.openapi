package gen.pathparams;

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
 * <li>{@link #setmap(String,String) PUT /a/<b>[a]</b>/<b>[b]</b> =  setmap}
 * 
 * <li>{@link #getmap(String) GET /a/<b>[a]</b> =  getmap}
 * 
 * <li>{@link #deletemap(String) DELETE /a/<b>[a]</b> =  deletemap}
 * 
 * </ul>
 * 
 */

@RequirePathparamsBase
public abstract class PathparamsBase extends OpenAPIBase {

public static final String BASE_PATH = "/api/v1";

/**
 * 
 * PUT /a/{a}/{b} = setmap
 * 
 * @param a –  (path) collectionFormat=%scsv
 * 
 * @param b –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void setmap(String a, String b) throws Exception;

/**
 * 
 * GET /a/{a} = getmap
 * 
 * @param a –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void getmap(String a) throws Exception;

/**
 * 
 * DELETE /a/{a} = deletemap
 * 
 * @param a –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void deletemap(String a) throws Exception;

  /*****************************************************************/

  public PathparamsBase() {
    super(BASE_PATH,gen.pathparams.PathparamsBase.class,
         "setmap               PUT    /a/{a}/{b}",
         "getmap               GET    /a/{a}",
         "deletemap            DELETE /a/{a}");
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

      if ( index + 1 == segments.length ) {
        context.pathParameter("a",segments[index++]);
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.DELETE)) {
            deletemap_delete_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.GET)) {
            getmap_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("DELETE", "GET");

        }


      } else       if ( index + 2 == segments.length ) {
        context.pathParameter("a",segments[index++]);
        context.pathParameter("b",segments[index++]);
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.PUT)) {
            setmap_put_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("PUT");

        }


      }

      // end a
    } 

    return false;
  }

private void setmap_put_(OpenAPIContext context) throws Exception{

    context.setOperation("setmap");
String a_ = context.toString(context.path("a"));
String b_ = context.toString(context.path("b"));


    //  VALIDATORS 

    context.begin("setmap");
    context.end();

    context.call( () -> { setmap(a_, b_); return null; });
    context.setResult(null, 200);

}

private void getmap_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getmap");
String a_ = context.toString(context.path("a"));


    //  VALIDATORS 

    context.begin("getmap");
    context.end();

    context.call( () -> { getmap(a_); return null; });
    context.setResult(null, 200);

}

private void deletemap_delete_(OpenAPIContext context) throws Exception{

    context.setOperation("deletemap");
String a_ = context.toString(context.path("a"));


    //  VALIDATORS 

    context.begin("deletemap");
    context.end();

    context.call( () -> { deletemap(a_); return null; });
    context.setResult(null, 200);

}

}

