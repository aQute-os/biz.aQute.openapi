package gen.recursivetype;

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
 * <li>{@link #nested() POST /recursive =  nested}
 * 
 * </ul>
 * 
 */

@RequireRecursivetypeBase
public abstract class RecursivetypeBase extends OpenAPIBase {

public static final String BASE_PATH = "/recursive";

/**
 * 
 * POST /recursive = nested
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Item nested() throws Exception;

/**
 * 
 * Item
 * 
 */

public static class Item extends OpenAPIBase.DTO {

    public Optional<List<Item>> values = Optional.empty();

    public Item values(List<Item> values){ this.values=Optional.ofNullable(values); return this; }
    public Optional<List<Item>> values(){ return this.values; }

}

  /*****************************************************************/

  public RecursivetypeBase() {
    super(BASE_PATH,gen.recursivetype.RecursivetypeBase.class,
         "nested               POST   /recursive  RETURN Item");
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

    if( index < segments.length && "recursive".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          nested_post_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("POST");

      }

      // end recursive
    } 

    return false;
  }

private void nested_post_(OpenAPIContext context) throws Exception{

    context.setOperation("nested");

    Object result = context.call( ()-> nested());
    context.setResult(result, 200);

}

}

