package gen.additional_properties;

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
 * <li>{@link #additionalProperties(DeviceResponse) PUT /additionalProperties =  AdditionalProperties}
 * 
 * </ul>
 * 
 */

@RequireAdditional_propertiesBase
public abstract class Additional_propertiesBase extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * PUT /additionalProperties = AdditionalProperties
 * 
 * @param content –  (body) collectionFormat=%scsv
 * 
   * @returns 200 / null
 * 200
 * 
 */

protected abstract DeviceResponse additionalProperties(DeviceResponse content) throws Exception;

/**
 * 
 * DeviceResponse
 * 
 * The device response.
 * 
 */

public static class DeviceResponse extends OpenAPIBase.DTO {

    public java.util.Map<String,List<Link>> _links;
    public Optional<java.util.Map<String,Integer>> ints = Optional.empty();
    public String deviceId;

    public DeviceResponse _links(java.util.Map<String,List<Link>> _links){ this._links=_links; return this; }
    public java.util.Map<String,List<Link>> _links(){ return this._links; }

    public DeviceResponse ints(java.util.Map<String,Integer> ints){ this.ints=Optional.ofNullable(ints); return this; }
    public Optional<java.util.Map<String,Integer>> ints(){ return this.ints; }

    public DeviceResponse deviceId(String deviceId){ this.deviceId=deviceId; return this; }
    public String deviceId(){ return this.deviceId; }

}

/**
 * 
 * Link
 * 
 */

public static class Link extends OpenAPIBase.DTO {

    public Optional<String> href = Optional.empty();

    public Link href(String href){ this.href=Optional.ofNullable(href); return this; }
    public Optional<String> href(){ return this.href; }

}

  /*****************************************************************/

  public Additional_propertiesBase() {
    super(BASE_PATH,gen.additional_properties.Additional_propertiesBase.class,
         "AdditionalProperties PUT    /additionalProperties  PAYLOAD DeviceResponse  RETURN DeviceResponse");
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

    if( index < segments.length && "additionalProperties".equals(segments[index])) {
      index++;
      if ( segments.length == index) {
        if ( context.isMethod(OpenAPIBase.Method.PUT)) {
          additionalProperties_put_(context);
          return true;
        } 
        return getOpenAPIContext().doOptions("PUT");

      }

      // end additionalProperties
    } 

    return false;
  }

private void additionalProperties_put_(OpenAPIContext context) throws Exception{

    context.setOperation("AdditionalProperties");
DeviceResponse content_ = context.body(DeviceResponse.class);


    //  VALIDATORS 

    context.begin("AdditionalProperties");
    context.end();

    Object result = context.call( ()-> additionalProperties(content_));
    context.setResult(result, 200);

}

}

