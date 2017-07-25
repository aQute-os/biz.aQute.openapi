package gen.temperature;

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
 * <li>{@link #temperature() GET /temperature =  temperature}
 * 
 * </ul>
 * 
 */

@RequireTemperatureBase
public abstract class TemperatureBase extends OpenAPIBase {

public static final String BASE_PATH = "/acme/v1";

/**
 * 
 * GET /temperature = temperature
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract double temperature() throws Exception;

  /*****************************************************************/



     public static OpenAPISecurityDefinition basicauth =  OpenAPISecurityDefinition.basic("basicauth",BASE_PATH);


  public TemperatureBase() {
    super(BASE_PATH,gen.temperature.TemperatureBase.class,
         "temperature          GET    /temperature  RETURN double");
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

    if( index < segments.length && "temperature".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          temperature_get_(context);
          return true;
        } 
      }

      // end temperature
    } 

    return false;
  }

private void temperature_get_(OpenAPIContext context) throws Exception{

    context.setOperation("temperature");
    context.verify(gen.temperature.TemperatureBase.basicauth).verify();

    Object result = context.call( ()-> temperature());
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 1.0.0.201707241457
