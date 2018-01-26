package gen.inlineresponse;

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
 * <li>{@link #inline() GET /inline =  inline}
 * 
 * </ul>
 * 
 */

@RequireInlineresponseBase
public abstract class InlineresponseBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /inline = inline
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract InlineResponse inline() throws Exception;

/**
 * 
 * InlineResponse
 * 
 */

public static class InlineResponse extends OpenAPIBase.DTO {

    public Optional<String> p = Optional.empty();

    public InlineResponse p(String p){ this.p=Optional.ofNullable(p); return this; }
    public Optional<String> p(){ return this.p; }

}

  /*****************************************************************/

  public InlineresponseBase() {
    super(BASE_PATH,gen.inlineresponse.InlineresponseBase.class,
         "inline               GET    /inline  RETURN InlineResponse");
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

    if( index < segments.length && "inline".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          inline_get_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("GET");

      }

      // end inline
    } 

    return false;
  }

private void inline_get_(OpenAPIContext context) throws Exception{

    context.setOperation("inline");

    Object result = context.call( ()-> inline());
    context.setResult(result, 200);

}

}

