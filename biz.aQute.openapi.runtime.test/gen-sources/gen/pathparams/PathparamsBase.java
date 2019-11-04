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
 * <li>{@link #setmap_foo_bar(String) PUT /a/<b>[a]</b>/foo/bar =  setmap_foo_bar}
 * 
 * <li>{@link #setmap_bar(String,String) PUT /a/<b>[a]</b>/bar/<b>[b]</b> =  setmap_bar}
 * 
 * <li>{@link #setmap_foo(String,String) PUT /a/<b>[a]</b>/foo/<b>[b]</b> =  setmap_foo}
 * 
 * <li>{@link #getmap(String) GET /a/<b>[a]</b> =  getmap}
 * 
 * <li>{@link #deletemap(String) DELETE /a/<b>[a]</b> =  deletemap}
 * 
 * <li>{@link #setmap_x(String,String) PUT /a/<b>[x]</b>/<b>[b]</b> =  setmap_x}
 * 
 * </ul>
 * 
 */

@RequirePathparamsBase
public abstract class PathparamsBase extends OpenAPIBase {

public static final String BASE_PATH = "/api/v1";

/**
 * 
 * PUT /a/{a}/foo/bar = setmap_foo_bar
 * 
 * @param a –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void setmap_foo_bar(String a) throws Exception;

/**
 * 
 * PUT /a/{a}/bar/{b} = setmap_bar
 * 
 * @param a –  (path) collectionFormat=%scsv
 * 
 * @param b –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void setmap_bar(String a, String b) throws Exception;

/**
 * 
 * PUT /a/{a}/foo/{b} = setmap_foo
 * 
 * @param a –  (path) collectionFormat=%scsv
 * 
 * @param b –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void setmap_foo(String a, String b) throws Exception;

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

/**
 * 
 * PUT /a/{x}/{b} = setmap_x
 * 
 * @param x –  (path) collectionFormat=%scsv
 * 
 * @param b –  (path) collectionFormat=%scsv
 * 
 */

protected abstract void setmap_x(String x, String b) throws Exception;

  /*****************************************************************/

  public PathparamsBase() {
    super(BASE_PATH,gen.pathparams.PathparamsBase.class,
         "setmap_foo_bar       PUT    /a/{a}/foo/bar",
         "setmap_bar           PUT    /a/{a}/bar/{b}",
         "setmap_foo           PUT    /a/{a}/foo/{b}",
         "getmap               GET    /a/{a}",
         "deletemap            DELETE /a/{a}",
         "setmap_x             PUT    /a/{x}/{b}");
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

        if ( index < segments.length ) {
        context.pathParameter("a",segments[index]);
        context.pathParameter("x",segments[index]);
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.DELETE)) {
            deletemap_delete_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.GET)) {
            getmap_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("DELETE", "GET");

        } else         if( index < segments.length && "bar".equals(segments[index])) {
          index++;

            if ( index < segments.length ) {
            context.pathParameter("b",segments[index]);
            index++;
            if ( segments.length == index) {
              if ( context.isMethod(OpenAPIBase.Method.PUT)) {
                setmap_bar_put_(context);
                return true;
              } 
              return getOpenAPIContext().doOptions("PUT");

            }


          }

          // end bar
        }  else         if( index < segments.length && "foo".equals(segments[index])) {
          index++;

          if( index < segments.length && "bar".equals(segments[index])) {
            index++;
            if ( segments.length == index) {
              if ( context.isMethod(OpenAPIBase.Method.PUT)) {
                setmap_foo_bar_put_(context);
                return true;
              } 
              return getOpenAPIContext().doOptions("PUT");

            }

            // end bar
          }  else             if ( index < segments.length ) {
            context.pathParameter("b",segments[index]);
            index++;
            if ( segments.length == index) {
              if ( context.isMethod(OpenAPIBase.Method.PUT)) {
                setmap_foo_put_(context);
                return true;
              } 
              return getOpenAPIContext().doOptions("PUT");

            }


          }

          // end foo
        }  else           if ( index < segments.length ) {
          context.pathParameter("b",segments[index]);
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.PUT)) {
              setmap_x_put_(context);
              return true;
            } 
            return getOpenAPIContext().doOptions("PUT");

          }


        }


      }

      // end a
    } 

    return false;
  }

private void setmap_foo_bar_put_(OpenAPIContext context) throws Exception{

    context.setOperation("setmap_foo_bar");
String a_ = context.toString(context.path("a"));


    //  VALIDATORS 

    context.begin("setmap_foo_bar");
    context.end();

    context.call( () -> { setmap_foo_bar(a_); return null; });
    context.setResult(null, 200);

}

private void setmap_bar_put_(OpenAPIContext context) throws Exception{

    context.setOperation("setmap_bar");
String a_ = context.toString(context.path("a"));
String b_ = context.toString(context.path("b"));


    //  VALIDATORS 

    context.begin("setmap_bar");
    context.end();

    context.call( () -> { setmap_bar(a_, b_); return null; });
    context.setResult(null, 200);

}

private void setmap_foo_put_(OpenAPIContext context) throws Exception{

    context.setOperation("setmap_foo");
String a_ = context.toString(context.path("a"));
String b_ = context.toString(context.path("b"));


    //  VALIDATORS 

    context.begin("setmap_foo");
    context.end();

    context.call( () -> { setmap_foo(a_, b_); return null; });
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

private void setmap_x_put_(OpenAPIContext context) throws Exception{

    context.setOperation("setmap_x");
String x_ = context.toString(context.path("x"));
String b_ = context.toString(context.path("b"));


    //  VALIDATORS 

    context.begin("setmap_x");
    context.end();

    context.call( () -> { setmap_x(x_, b_); return null; });
    context.setResult(null, 200);

}

}

