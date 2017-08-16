package gen.optional;

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
 * <li>{@link #optionalValidation(Optional<String>) GET /validation =  optionalValidation}
 * 
 * </ul>
 * 
 */

@RequireOptionalBase
public abstract class OptionalBase extends OpenAPIBase {

public static final String BASE_PATH = "/optional";

/**
 * 
 * GET /validation = optionalValidation
 * 
 * @param componentId – Gets or sets the component identifier. (query)
 * 
 */

protected abstract void optionalValidation(Optional<String> componentId) throws Exception;

  /*****************************************************************/

  public OptionalBase() {
    super(BASE_PATH,gen.optional.OptionalBase.class,
         "optionalValidation   GET    /validation?componentId");
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

    if( index < segments.length && "validation".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.GET)) {
          optionalValidation_get_(context);
          return true;
        } 
      }

      // end validation
    } 

    return false;
  }

private void optionalValidation_get_(OpenAPIContext context) throws Exception{

    context.setOperation("optionalValidation");
Optional<String> componentId_ = context.optional(context.toString(context.parameter("componentId")));


    //  VALIDATORS 

    context.begin("optionalValidation");
       if  (componentId_.isPresent() ) {
    context.validate(componentId_.get().length() >= 0, componentId_.get(), "componentId_.get()", "componentId_.get().length() >= 0");
    context.validate(componentId_.get().length() <= 40, componentId_.get(), "componentId_.get()", "componentId_.get().length() <= 40");
       }
    context.end();

    context.call( () -> { optionalValidation(componentId_); return null; });
    context.setResult(null, 200);

}

}

