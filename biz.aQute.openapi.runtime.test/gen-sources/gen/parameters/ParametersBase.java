package gen.parameters;

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
 * <li>{@link #postParameter(String,int,String,String,String) POST /parameter/<b>[path]</b> =  postParameter}
 * 
 * <li>{@link #arrayConversion(List<String>,List<String>,List<String>,List<String>,List<String>,List<String>,List<String>) GET /parameter/<b>[path]</b> =  arrayConversion}
 * 
 * <li>{@link #putParameter(Body,String,String,String) PUT /parameter/<b>[path]</b> =  putParameter}
 * 
 * </ul>
 * 
 */

@RequireParametersBase
public abstract class ParametersBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * POST /parameter/{path} = postParameter
 * 
 * @param form1 –  (formData) collectionFormat=%scsv
 * 
 * @param form2 –  (formData) collectionFormat=%scsv
 * 
 * @param path –  (path) collectionFormat=%scsv
 * 
 * @param header –  (header) collectionFormat=%scsv
 * 
 * @param query –  (query) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Response postParameter(String form1, int form2, String path, String header, String query) throws Exception;

/**
 * 
 * GET /parameter/{path} = arrayConversion
 * 
 * @param array –  (query) collectionFormat=%scsv
 * 
 * @param arrayNone –  (query) collectionFormat=%snone
 * 
 * @param arrayPipes –  (query) collectionFormat=%spipes
 * 
 * @param arrayTsv –  (query) collectionFormat=%stsv
 * 
 * @param arrayMulti –  (query) collectionFormat=%smulti
 * 
 * @param arrayCsv –  (query) collectionFormat=%scsv
 * 
 * @param arraySsv –  (query) collectionFormat=%sssv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Response arrayConversion(List<String> array, List<String> arrayNone, List<String> arrayPipes, List<String> arrayTsv, List<String> arrayMulti, List<String> arrayCsv, List<String> arraySsv) throws Exception;

/**
 * 
 * PUT /parameter/{path} = putParameter
 * 
 * @param body –  (body) collectionFormat=%scsv
 * 
 * @param path –  (path) collectionFormat=%scsv
 * 
 * @param header –  (header) collectionFormat=%scsv
 * 
 * @param query –  (query) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Response putParameter(Body body, String path, String header, String query) throws Exception;

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

/**
 * 
 * Body
 * 
 */

public static class Body extends OpenAPIBase.DTO {

    public String payload;

    public Body payload(String payload){ this.payload=payload; return this; }
    public String payload(){ return this.payload; }

}

  /*****************************************************************/

  public ParametersBase() {
    super(BASE_PATH,gen.parameters.ParametersBase.class,
         "postParameter        POST   /parameter/{path}?query  RETURN Response",
         "arrayConversion      GET    /parameter/{path}?array&arrayNone&arrayPipes&arrayTsv&arrayMulti&arrayCsv&arraySsv  RETURN Response",
         "putParameter         PUT    /parameter/{path}?query  PAYLOAD Body  RETURN Response");
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

    if( index < segments.length && "parameter".equals(segments[index])) {
      index++;

      if ( index < segments.length ) {
        context.pathParameter("path",segments[index]);
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.PUT)) {
            putParameter_put_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.POST)) {
            postParameter_post_(context);
            return true;
          }  else           if ( context.isMethod(OpenAPIBase.Method.GET)) {
            arrayConversion_get_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("PUT", "POST", "GET");

        }


      }      // end parameter
    } 

    return false;
  }

private void postParameter_post_(OpenAPIContext context) throws Exception{

    context.setOperation("postParameter");
String form1_ = context.toString(context.formData("form1"));
Integer form2_ = context.toInt(context.formData("form2"));
String path_ = context.toString(context.path("path"));
String header_ = context.toString(context.header("header"));
String query_ = context.toString(context.parameter("query"));


    //  VALIDATORS 

    context.begin("postParameter");
    context.end();

    Object result = context.call( ()-> postParameter(form1_, form2_, path_, header_, query_));
    context.setResult(result, 200);

}

private void arrayConversion_get_(OpenAPIContext context) throws Exception{

    context.setOperation("arrayConversion");
List<String> array_ = context.toArray(String.class, context.csv(context.parameters("array")));
List<String> arrayNone_ = context.toArray(String.class, context.parameters("arrayNone"));
List<String> arrayPipes_ = context.toArray(String.class, context.pipes(context.parameters("arrayPipes")));
List<String> arrayTsv_ = context.toArray(String.class, context.tsv(context.parameters("arrayTsv")));
List<String> arrayMulti_ = context.toArray(String.class, context.parameters("arrayMulti"));
List<String> arrayCsv_ = context.toArray(String.class, context.csv(context.parameters("arrayCsv")));
List<String> arraySsv_ = context.toArray(String.class, context.ssv(context.parameters("arraySsv")));


    //  VALIDATORS 

    context.begin("arrayConversion");
    context.end();

    Object result = context.call( ()-> arrayConversion(array_, arrayNone_, arrayPipes_, arrayTsv_, arrayMulti_, arrayCsv_, arraySsv_));
    context.setResult(result, 200);

}

private void putParameter_put_(OpenAPIContext context) throws Exception{

    context.setOperation("putParameter");
Body body_ = context.body(Body.class);
String path_ = context.toString(context.path("path"));
String header_ = context.toString(context.header("header"));
String query_ = context.toString(context.parameter("query"));


    //  VALIDATORS 

    context.begin("putParameter");
    context.end();

    Object result = context.call( ()-> putParameter(body_, path_, header_, query_));
    context.setResult(result, 200);

}

}

