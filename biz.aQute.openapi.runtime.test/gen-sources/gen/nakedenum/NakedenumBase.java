package gen.nakedenum;

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
 * <li>{@link #nakedEnum() POST /casing =  NakedEnum}
 * 
 * </ul>
 * 
 */

@RequireNakedenumBase
public abstract class NakedenumBase extends OpenAPIBase {

public static final String BASE_PATH = "/enums";

/**
 * 
 * POST /casing = NakedEnum
 * 
   * @returns 200 / Control mode
 * 200
 * 
 */

protected abstract NakedEnum nakedEnum() throws Exception;

/**
 * 
 * NakedEnum
 * 
 */

  public enum NakedEnum {
    A("A"),
    B("B");

    public final String value;

    NakedEnum(String value) {
      this.value = value;
    }
  }

  /*****************************************************************/

  public NakedenumBase() {
    super(BASE_PATH,gen.nakedenum.NakedenumBase.class,
         "NakedEnum            POST   /casing  RETURN NakedEnum");
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

    if( index < segments.length && "casing".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          nakedEnum_post_(context);
          return true;
        } 
      }

      // end casing
    } 

    return false;
  }

private void nakedEnum_post_(OpenAPIContext context) throws Exception{

    context.setOperation("NakedEnum");

    Object result = context.call( ()-> nakedEnum());
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 1.0.0.201707241457
