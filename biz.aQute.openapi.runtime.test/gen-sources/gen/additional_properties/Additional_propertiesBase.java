package gen.additional_properties;

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
 * <li>{@link #test(Type1000) PUT /additionalProperties =  test}
 * 
 * </ul>
 * 
 */

@RequireAdditional_propertiesBase
public abstract class Additional_propertiesBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * PUT /additionalProperties = test
 * 
 * @param body –  (body) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract void test(Type1000 body) throws Exception;

/**
 * 
 * Type1000
 * 
 */

public static class Type1000 extends OpenAPIBase.DTO {


    public java.util.Map<String,Object> __extra;
    public java.util.Map<String,Object> __extra(){ return this.__extra; }

}

  /*****************************************************************/

  public Additional_propertiesBase() {
    super(BASE_PATH,gen.additional_properties.Additional_propertiesBase.class,
         "test                 PUT    /additionalProperties  PAYLOAD Type1000");
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

    if( index < segments.length && "additionalProperties".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          test_put_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("PUT");

      }

      // end additionalProperties
    } 

    return false;
  }

private void test_put_(OpenAPIContext context) throws Exception{

    context.setOperation("test");
Type1000 body_ = context.body(Type1000.class);


    //  VALIDATORS 

    context.begin("test");
    context.end();

    context.call( () -> { test(body_); return null; });
    context.setResult(null, 200);

}

}

