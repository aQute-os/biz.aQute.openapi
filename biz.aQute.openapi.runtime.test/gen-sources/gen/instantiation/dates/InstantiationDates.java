package gen.instantiation.dates;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import java.time.format.DateTimeFormatter;
import java.time.OffsetDateTime;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * <li>{@link #putDates(Dates) PUT /dates =  putDates}
 * 
 * </ul>
 * 
 */

@RequireInstantiationDates
public abstract class InstantiationDates extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * PUT /dates = putDates
 * 
 * Validate dates
 * 
 * @param token –  (body)
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract Dates putDates(Dates token) throws Exception;

/**
 * 
 * Dates
 * 
 * 
 * 
 */

public static class Dates extends OpenAPIBase.DTO {

    public LocalDate date;
    public java.time.OffsetDateTime dateTime;
    public String error;

    public Dates date(LocalDate date){ this.date=date; return this; }
    public LocalDate date(){ return this.date; }

    public Dates dateTime(java.time.OffsetDateTime dateTime){ this.dateTime=dateTime; return this; }
    public java.time.OffsetDateTime dateTime(){ return this.dateTime; }

    public Dates error(String error){ this.error=error; return this; }
    public String error(){ return this.error; }

}

  /*****************************************************************/

  public InstantiationDates() {
    super(BASE_PATH,
         "putDates             PUT    /dates  PAYLOAD Dates  RETURN Dates");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "dates".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          putDates_put_(context);
          return true;
        } 
      }

      // end dates
    } 

    if ( segments.length == 1 && "openapi.json".equals(segments[0])) {
        getOpenAPIContext().copy( gen.instantiation.InstantiationBase.class.getResourceAsStream("openapi.json"), "application/json");
        return true;
    }
    return false;
  }

private void putDates_put_(OpenAPIContext context) throws Exception{

    context.setOperation("putDates");
Dates token_ = context.body(Dates.class);


    //  VALIDATORS 

    context.begin("putDates");
    context.require(token_,"token");
    context.end();

    Object result = putDates(token_);
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 0