package gen.modifieddatetime.dates;

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
 * <li>{@link #putDates(Dates) PUT /dates =  putDates}
 * 
 * </ul>
 * 
 */

@RequireModifieddatetimeDates
public abstract class ModifieddatetimeDates extends OpenAPIBase {

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
    public java.time.Instant dateTime;
    public String error;

    public Dates date(LocalDate date){ this.date=date; return this; }
    public LocalDate date(){ return this.date; }

    public Dates dateTime(java.time.Instant dateTime){ this.dateTime=dateTime; return this; }
    public java.time.Instant dateTime(){ return this.dateTime; }

    public Dates error(String error){ this.error=error; return this; }
    public String error(){ return this.error; }

}

  /*****************************************************************/

  public ModifieddatetimeDates() {
    super(BASE_PATH,
         "putDates             PUT    /dates  PAYLOAD Dates  RETURN Dates");
  }
    public OpenAPIBase.Codec codec_() {
        return gen.modifieddatetime.ModifieddatetimeBase.CODEC;
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

    return false;
  }

private void putDates_put_(OpenAPIContext context) throws Exception{

    context.setOperation("putDates");
Dates token_ = context.body(Dates.class);


    //  VALIDATORS 

    context.begin("putDates");
    context.end();

    Object result = context.call( ()-> putDates(token_));
    context.setResult(result, 200);

}

}


// aQute OpenAPI generator version 0
