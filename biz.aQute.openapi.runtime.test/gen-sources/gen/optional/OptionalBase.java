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
 * <li>{@link #optionalValidation(Optional<String>,Optional<Body>,Optional<String>,Optional<String>,Optional<String>) GET /validation =  optionalValidation}
 * 
 * </ul>
 * 
 */

@RequireOptionalBase
public abstract class OptionalBase extends OpenAPIBase {

public static final String BASE_PATH = "/optional/{path}";

/**
 * 
 * GET /validation = optionalValidation
 * 
 * @param inoptional –  (query)
 * 
 * @param inbody –  (body)
 * 
 * @param inheader –  (header)
 * 
 * @param inpath –  (path)
 * 
 * @param formData –  (formData)
 * 
 */

protected abstract void optionalValidation(Optional<String> inoptional, Optional<Body> inbody, Optional<String> inheader, Optional<String> inpath, Optional<String> formData) throws Exception;

/**
 * 
 * Body
 * 
 */

public static class Body extends OpenAPIBase.DTO {

    public Optional<String> payload = Optional.empty();

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  (this.payload.isPresent() ) {
    context.validate(this.payload.get().length() >= 0, this.payload.get(), "this.payload.get()", "this.payload.get().length() >= 0");
    context.validate(this.payload.get().length() <= 40, this.payload.get(), "this.payload.get()", "this.payload.get().length() <= 40");
       }
     context.end();
    }
    public Body payload(String payload){ this.payload=Optional.ofNullable(payload); return this; }
    public Optional<String> payload(){ return this.payload; }

}

  /*****************************************************************/

  public OptionalBase() {
    super(BASE_PATH,gen.optional.OptionalBase.class,
         "optionalValidation   GET    /validation?inoptional  PAYLOAD Optional<Body>");
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
Optional<String> inoptional_ = context.optional(context.toString(context.parameter("inoptional")));
Optional<Body> inbody_ = context.optional(context.body(Body.class));
Optional<String> inheader_ = context.optional(context.toString(context.header("inheader")));
Optional<String> inpath_ = context.optional(context.toString(context.path("inpath")));
Optional<String> formData_ = context.optional(context.toString(context.parameter("formData")));


    //  VALIDATORS 

    context.begin("optionalValidation");
       if  (inoptional_.isPresent() ) {
    context.validate(inoptional_.get().length() >= 0, inoptional_.get(), "inoptional_.get()", "inoptional_.get().length() >= 0");
    context.validate(inoptional_.get().length() <= 40, inoptional_.get(), "inoptional_.get()", "inoptional_.get().length() <= 40");
       }
       if  (inbody_.isPresent() ) {
       inbody_.get().validate(context, "inbody_.get()");
       }
       if  (inheader_.isPresent() ) {
    context.validate(inheader_.get().length() >= 0, inheader_.get(), "inheader_.get()", "inheader_.get().length() >= 0");
    context.validate(inheader_.get().length() <= 40, inheader_.get(), "inheader_.get()", "inheader_.get().length() <= 40");
       }
       if  (inpath_.isPresent() ) {
    context.validate(inpath_.get().length() >= 0, inpath_.get(), "inpath_.get()", "inpath_.get().length() >= 0");
    context.validate(inpath_.get().length() <= 40, inpath_.get(), "inpath_.get()", "inpath_.get().length() <= 40");
       }
       if  (formData_.isPresent() ) {
    context.validate(formData_.get().length() >= 0, formData_.get(), "formData_.get()", "formData_.get().length() >= 0");
    context.validate(formData_.get().length() <= 40, formData_.get(), "formData_.get()", "formData_.get().length() <= 40");
       }
    context.end();

    context.call( () -> { optionalValidation(inoptional_, inbody_, inheader_, inpath_, formData_); return null; });
    context.setResult(null, 200);

}

}

