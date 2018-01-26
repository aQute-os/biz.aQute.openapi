package gen.formdata;

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
 * <li>{@link #form(String,String,List<String>,List<String>,List<String>) POST /test =  form}
 * 
 * <li>{@link #oauth2(String,String,String) POST /oauth2 =  oauth2}
 * 
 * </ul>
 * 
 */

@RequireFormdataBase
public abstract class FormdataBase extends OpenAPIBase {

public static final String BASE_PATH = "/formdata";

/**
 * 
 * POST /test = form
 * 
 * @param s_1 –  (formData) collectionFormat=%snone
 * 
 * @param s_12 –  (formData) collectionFormat=%snone
 * 
 * @param a_1 –  (formData) collectionFormat=%snone
 * 
 * @param a_12 –  (formData) collectionFormat=%snone
 * 
 * @param s_1c2_csv –  (formData) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Iterable<? extends String> form(String s_1, String s_12, List<String> a_1, List<String> a_12, List<String> s_1c2_csv) throws Exception;

/**
 * 
 * POST /oauth2 = oauth2
 * 
 * @param grant_type –  (formData) collectionFormat=%scsv
 * 
 * @param username –  (formData) collectionFormat=%scsv
 * 
 * @param password –  (formData) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract String oauth2(String grant_type, String username, String password) throws Exception;

  /*****************************************************************/

  public FormdataBase() {
    super(BASE_PATH,gen.formdata.FormdataBase.class,
         "form                 POST   /test  RETURN List<String>",
         "oauth2               POST   /oauth2  RETURN String");
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

    if( index < segments.length && "test".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          form_post_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("POST");

      }

      // end test
    }  else     if( index < segments.length && "oauth2".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          oauth2_post_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("POST");

      }

      // end oauth2
    } 

    return false;
  }

private void form_post_(OpenAPIContext context) throws Exception{

    context.setOperation("form");
String s_1_ = context.toString(context.formData("s_1"));
String s_12_ = context.toString(context.formData("s_12"));
List<String> a_1_ = context.toArray(String.class, context.formDataArray("a_1"));
List<String> a_12_ = context.toArray(String.class, context.formDataArray("a_12"));
List<String> s_1c2_csv_ = context.toArray(String.class, context.csv(context.formDataArray("s_1c2_csv")));


    //  VALIDATORS 

    context.begin("form");
    context.end();

    Object result = context.call( ()-> form(s_1_, s_12_, a_1_, a_12_, s_1c2_csv_));
    context.setResult(result, 200);

}

private void oauth2_post_(OpenAPIContext context) throws Exception{

    context.setOperation("oauth2");
String grant_type_ = context.toString(context.formData("grant_type"));
String username_ = context.toString(context.formData("username"));
String password_ = context.toString(context.formData("password"));


    //  VALIDATORS 

    context.begin("oauth2");
    context.end();

    Object result = context.call( ()-> oauth2(grant_type_, username_, password_));
    context.setResult(result, 200);

}

}

