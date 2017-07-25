package gen.references;

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
 * <li>{@link #references() POST /primitives =  References}
 * 
 * </ul>
 * 
 */

@RequireReferencesBase
public abstract class ReferencesBase extends OpenAPIBase {

public static final String BASE_PATH = "/references";

/**
 * 
 * POST /primitives = References
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract References references() throws Exception;

/**
 * 
 * AnEnum
 * 
 */

  public enum AnEnum {
    None("None"),
    Info("Info"),
    Warn("Warn"),
    Error("Error");

    public final String value;

    AnEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * References
 * 
 */

public static class References extends OpenAPIBase.DTO {

    public Optional<AnEnum> benum = Optional.empty();
    public AnEnum anenum;

    public References benum(AnEnum benum){ this.benum=Optional.ofNullable(benum); return this; }
    public Optional<AnEnum> benum(){ return this.benum; }

    public References anenum(AnEnum anenum){ this.anenum=anenum; return this; }
    public AnEnum anenum(){ return this.anenum; }

}

  /*****************************************************************/

  public ReferencesBase() {
    super(BASE_PATH,gen.references.ReferencesBase.class,
         "References           POST   /primitives  RETURN References");
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

    if( index < segments.length && "primitives".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.POST)) {
          references_post_(context);
          return true;
        } 
      }

      // end primitives
    } 

    return false;
  }

private void references_post_(OpenAPIContext context) throws Exception{

    context.setOperation("References");

    Object result = context.call( ()-> references());
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 1.0.0.201707241457
