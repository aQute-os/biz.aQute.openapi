package gen.imagereturn;

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
 * <li>{@link #image_json() GET /image_json =  image_json}
 * 
 * <li>{@link #image_multiple_mime() GET /image_multiple_mime =  image_multiple_mime}
 * 
 * <li>{@link #image_one_mime() GET /image_one_mime =  image_one_mime}
 * 
 * </ul>
 * 
 */

@RequireImagereturnBase
public abstract class ImagereturnBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /image_json = image_json
 * 
   * @returns 200 / 
 * 200
 * 
 */

protected abstract byte[] image_json() throws Exception;

/**
 * 
 * GET /image_multiple_mime = image_multiple_mime
 * 
   * @returns 200 / 
 * 200
 * 
 */

protected abstract MimeWrapper image_multiple_mime() throws Exception;

/**
 * 
 * GET /image_one_mime = image_one_mime
 * 
   * @returns 200 / 
 * 200
 * 
 */

protected abstract MimeWrapper image_one_mime() throws Exception;

  /*****************************************************************/

  public ImagereturnBase() {
    super(BASE_PATH,gen.imagereturn.ImagereturnBase.class,
         "image_json           GET    /image_json  RETURN byte[]",
         "image_multiple_mime  GET    /image_multiple_mime  RETURN MimeWrapper",
         "image_one_mime       GET    /image_one_mime  RETURN MimeWrapper");
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

    if( index < segments.length && "image_json".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          image_json_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end image_json
    }  else     if( index < segments.length && "image_multiple_mime".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          image_multiple_mime_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end image_multiple_mime
    }  else     if( index < segments.length && "image_one_mime".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          image_one_mime_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end image_one_mime
    } 

    return false;
  }

private void image_json_get_(OpenAPIContext context) throws Exception{

    context.setOperation("image_json");

    Object result = context.call( ()-> image_json());
    result = context.wrap("application/json", (byte[]) result);
    context.setResult(result, 200);

}

private void image_multiple_mime_get_(OpenAPIContext context) throws Exception{

    context.setOperation("image_multiple_mime");

    Object result = context.call( ()-> image_multiple_mime());
    context.setResult(result, 200);

}

private void image_one_mime_get_(OpenAPIContext context) throws Exception{

    context.setOperation("image_one_mime");

    Object result = context.call( ()-> image_one_mime());
    context.setResult(result, 200);

}

}

