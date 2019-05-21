package gen.parameter_enum.widgetapi;

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
 * <li>{@link #getPowerWidget(GetPowerWidget_type) GET /widgets/gauge/power =  GetPowerWidget}
 * 
 * </ul>
 * 
 */

@RequireParameter_enumWidgetApi
public abstract class Parameter_enumWidgetApi extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /widgets/gauge/power = GetPowerWidget
 * 
 * @param type –  (query) collectionFormat=%scsv
 * 
 */

protected abstract void getPowerWidget(GetPowerWidget_type type) throws Exception;

/**
 * 
 * GetPowerWidget_type
 * 
 */

  public enum GetPowerWidget_type {
    A("A"),
    B("B"),
    C("C");

    public final String value;

    GetPowerWidget_type(String value) {
      this.value = value;
    }
  }

  /*****************************************************************/

  public Parameter_enumWidgetApi() {
    super(BASE_PATH,gen.parameter_enum.Parameter_enumBase.class,
         "GetPowerWidget       GET    /widgets/gauge/power?type");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "widgets".equals(segments[index])) {
      index++;

      if( index < segments.length && "gauge".equals(segments[index])) {
        index++;

        if( index < segments.length && "power".equals(segments[index])) {
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.GET)) {
              getPowerWidget_get_(context);
              return true;
            } 
            return getOpenAPIContext().doOptions("GET");

          }

          // end power
        } 

        // end gauge
      } 

      // end widgets
    } 

    return false;
  }

private void getPowerWidget_get_(OpenAPIContext context) throws Exception{

    context.setOperation("GetPowerWidget");
GetPowerWidget_type type_ = context.toEnumMember(GetPowerWidget_type.class,context.parameter("type"));


    //  VALIDATORS 

    context.begin("GetPowerWidget");
    context.end();

    context.call( () -> { getPowerWidget(type_); return null; });
    context.setResult(null, 200);

}

}

