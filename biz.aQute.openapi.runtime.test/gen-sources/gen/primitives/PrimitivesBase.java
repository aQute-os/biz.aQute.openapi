package gen.primitives;

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
 * <li>{@link #primitives() POST /primitives =  Primitives}
 * 
 * </ul>
 * 
 */

@RequirePrimitivesBase
public abstract class PrimitivesBase extends OpenAPIBase {

public static final String BASE_PATH = "/primitives";

/**
 * 
 * POST /primitives = Primitives
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Primitives primitives() throws Exception;

/**
 * 
 * Primitives
 * 
 */

public static class Primitives extends OpenAPIBase.DTO {

    public List<Double> doublearray;
    public double double$;
    public List<Integer> intarray;
    public int int$;

    public Primitives doublearray(List<Double> doublearray){ this.doublearray=doublearray; return this; }
    public List<Double> doublearray(){ return this.doublearray; }

    public Primitives double$(double double$){ this.double$=double$; return this; }
    public double double$(){ return this.double$; }

    public Primitives intarray(List<Integer> intarray){ this.intarray=intarray; return this; }
    public List<Integer> intarray(){ return this.intarray; }

    public Primitives int$(int int$){ this.int$=int$; return this; }
    public int int$(){ return this.int$; }

}

  /*****************************************************************/

  public PrimitivesBase() {
    super(BASE_PATH,gen.primitives.PrimitivesBase.class,
         "Primitives           POST   /primitives  RETURN Primitives");
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
          primitives_post_(context);
          return true;
        } 
      }

      // end primitives
    } 

    return false;
  }

private void primitives_post_(OpenAPIContext context) throws Exception{

    context.setOperation("Primitives");

    Object result = context.call( ()-> primitives());
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 1.0.0.201707241457
