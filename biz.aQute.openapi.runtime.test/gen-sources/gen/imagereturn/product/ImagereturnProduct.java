package gen.imagereturn.product;

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
 * <li>{@link #getProductIcon(String,String,int,Optional<String>) GET /product/icon/<b>[productTagId]</b>/<b>[iconType]</b>/<b>[size]</b> =  GetProductIcon}
 * 
 * </ul>
 * 
 */

@RequireImagereturnProduct
public abstract class ImagereturnProduct extends OpenAPIBase {

public static final String BASE_PATH = "/v1";

/**
 * 
 * GET /product/icon/{productTagId}/{iconType}/{size} = GetProductIcon
 * 
 * Gets the product icon.
 * 
 * @param productTagId – The product tag identifier. (path) collectionFormat=%scsv
 * 
 * @param iconType – Type of the icon.(neutral,alarm,warning,comerror,notactive,ok) (path) collectionFormat=%scsv
 * 
 * @param size – The size.(24, 150) (path) collectionFormat=%scsv
 * 
 * @param fileFormat – The file format.(png,svg) (query) collectionFormat=%scsv
 * 
   * @returns 200 / icon image file of specified file format
   * @returns 404 / Resource not found or no authorization
 * 200
 * 
 * 404
 * 
 */

protected abstract MimeWrapper getProductIcon(String productTagId, String iconType, int size, Optional<String> fileFormat) throws Exception;

  /*****************************************************************/

  public ImagereturnProduct() {
    super(BASE_PATH,gen.imagereturn.ImagereturnBase.class,
         "GetProductIcon       GET    /product/icon/{productTagId}/{iconType}/{size}?fileFormat  RETURN MimeWrapper");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "product".equals(segments[index])) {
      index++;

      if( index < segments.length && "icon".equals(segments[index])) {
        index++;

        if ( index < segments.length ) {
          context.pathParameter("productTagId",segments[index]);
          index++;

          if ( index < segments.length ) {
            context.pathParameter("iconType",segments[index]);
            index++;

            if ( index < segments.length ) {
              context.pathParameter("size",segments[index]);
              index++;
              if ( segments.length == index) {
                if ( context.isMethod(OpenAPIBase.Method.GET)) {
                  getProductIcon_get_(context);
                  return true;
                } 
                return getOpenAPIContext().doOptions("GET");

              }


            }
          }
        }        // end icon
      } 

      // end product
    } 

    return false;
  }

private void getProductIcon_get_(OpenAPIContext context) throws Exception{

    context.setOperation("GetProductIcon");
String productTagId_ = context.toString(context.path("productTagId"));
String iconType_ = context.toString(context.path("iconType"));
Integer size_ = context.toInt(context.path("size"));
Optional<String> fileFormat_ = context.optional(context.toString(context.parameter("fileFormat")));


    //  VALIDATORS 

    context.begin("GetProductIcon");
    context.end();

    Object result = context.call( ()-> getProductIcon(productTagId_, iconType_, size_, fileFormat_));
    context.setResult(result, 200);

}

}

