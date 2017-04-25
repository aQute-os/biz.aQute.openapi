package aQute.openapi.example.petstore.store;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.time.OffsetDateTime;
import java.time.LocalDate;
/**
 * 
 * <ul>
 * 
 * <li>{@link #placeOrder(Order) POST /store/order =  placeOrder}
 * 
 * <li>{@link #getOrderById(long) GET /store/order/<b>[orderId]</b> =  getOrderById}
 * 
 * <li>{@link #getInventory() GET /store/inventory =  getInventory}
 * 
 * <li>{@link #deleteOrder(long) DELETE /store/order/<b>[orderId]</b> =  deleteOrder}
 * 
 * </ul>
 * 
 */

@RequireGeneratedStore
public abstract class GeneratedStore extends OpenAPIBase {

public static final String BASE_PATH = "/v2";

/**
 * 
 * POST /store/order = placeOrder
 * 
 * Place an order for a pet
 * 
 * 
 * 
 * @param body – order placed for purchasing the pet (body)
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid Order
 * 200
 * 
 * 400
 * 
 */

protected abstract Order placeOrder(Order body) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * GET /store/order/{orderId} = getOrderById
 * 
 * Find purchase order by ID
 * 
 * For valid response try integer IDs with value <= 5 or > 10. Other values will generated exceptions
 * 
 * @param orderId – ID of pet that needs to be fetched (path)
 * 
   * @returns 200 / successful operation
   * @returns 400 / Invalid ID supplied
   * @returns 404 / Order not found
 * 200
 * 
 * 400
 * 
 * 404
 * 
 */

protected abstract Order getOrderById(long orderId) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * GET /store/inventory = getInventory
 * 
 * Returns pet inventories by status
 * 
 * Returns a map of status codes to quantities
 * 
   * @returns 200 / successful operation
 * 200
 * 
 */

protected abstract Anonymous_1000 getInventory() throws Exception;

/**
 * 
 * DELETE /store/order/{orderId} = deleteOrder
 * 
 * Delete purchase order by ID
 * 
 * For valid response try integer IDs with value < 1000. Anything above 1000 or nonintegers will generate API errors
 * 
 * @param orderId – ID of the order that needs to be deleted (path)
 * 
   * @throws Response BadRequestResponse / Invalid ID supplied
   * @throws Response 404 / Order not found
 * 400
 * 
 * 404
 * 
 */

protected abstract void deleteOrder(long orderId) throws Exception, OpenAPIBase.BadRequestResponse;

/**
 * 
 * Order
 * 
 */

public static class Order extends OpenAPIBase.DTO {

    public long petId;
    public int quantity;
    public long id;
    public java.time.OffsetDateTime shipDate;
    public boolean complete;
    public StatusEnum status;

    protected void validate(OpenAPIContext context, String name) {
       context.begin(name);
    context.validate(quantity <= 23, quantity, "quantity", "quantity <= 23");
     context.end();
    }
    public Order petId(long petId){ this.petId=petId; return this; }
    public long petId(){ return this.petId; }

    public Order quantity(int quantity){ this.quantity=quantity; return this; }
    public int quantity(){ return this.quantity; }

    public Order id(long id){ this.id=id; return this; }
    public long id(){ return this.id; }

    public Order shipDate(java.time.OffsetDateTime shipDate){ this.shipDate=shipDate; return this; }
    public java.time.OffsetDateTime shipDate(){ return this.shipDate; }

    public Order complete(boolean complete){ this.complete=complete; return this; }
    public boolean complete(){ return this.complete; }

    public Order status(StatusEnum status){ this.status=status; return this; }
    public StatusEnum status(){ return this.status; }

}

/**
 * 
 * StatusEnum
 * 
 * Order Status
 * 
 */

  public enum StatusEnum {
    placed("placed"),
    approved("approved"),
    delivered("delivered");

    public final String value;

    StatusEnum(String value) {
      this.value = value;
    }
  }

/**
 * 
 * Anonymous_1000
 * 
 */

public static class Anonymous_1000 extends OpenAPIBase.DTO {


}

  /*****************************************************************/

  public GeneratedStore() {
    super(BASE_PATH,
         "placeOrder           POST   /store/order  PAYLOAD Order  RETURN Order",
         "getOrderById         GET    /store/order/{orderId}  RETURN Order",
         "getInventory         GET    /store/inventory  RETURN Anonymous_1000",
         "deleteOrder          DELETE /store/order/{orderId}");
  }

  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {

    if( index < segments.length && "store".equals(segments[index])) {
      index++;

      if( index < segments.length && "inventory".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.GET)) {
            getInventory_get_(context);
            return true;
          } 
        }

        // end inventory
      }  else       if( index < segments.length && "order".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.POST)) {
            placeOrder_post_(context);
            return true;
          } 
        } else         if ( index < segments.length ) {
          context.pathParameter("orderId",segments[index]);
          index++;
          if ( segments.length == index) {
            if ( context.isMethod(OpenAPIBase.Method.DELETE)) {
              deleteOrder_delete_(context);
              return true;
            }  else             if ( context.isMethod(OpenAPIBase.Method.GET)) {
              getOrderById_get_(context);
              return true;
            } 
          }


        }        // end order
      } 

      // end store
    } 

    return false;
  }

private void placeOrder_post_(OpenAPIContext context) throws Exception{

    context.setOperation("placeOrder");
Order body_ = context.body(Order.class);


    //  VALIDATORS 

    context.begin("placeOrder");
       body_.validate(context, "body_");
    context.require(body_,"body");
    context.end();

    Object result = placeOrder(body_);
    context.setResult(result, 200);

}

private void getOrderById_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getOrderById");
Long orderId_ = context.toLong(context.path("orderId"));


    //  VALIDATORS 

    context.begin("getOrderById");
    context.validate(orderId_ >= 1, orderId_, "orderId_", "orderId_ >= 1");
    context.validate(orderId_ <= 5, orderId_, "orderId_", "orderId_ <= 5");
    context.require(orderId_,"orderId");
    context.end();

    Object result = getOrderById(orderId_);
    context.setResult(result, 200);

}

private void getInventory_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getInventory");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.api_key, context.header("api_key"));


    Object result = getInventory();
    context.setResult(result, 200);

}

private void deleteOrder_delete_(OpenAPIContext context) throws Exception{

    context.setOperation("deleteOrder");
Long orderId_ = context.toLong(context.path("orderId"));


    //  VALIDATORS 

    context.begin("deleteOrder");
    context.validate(orderId_ >= 1, orderId_, "orderId_", "orderId_ >= 1");
    context.require(orderId_,"orderId");
    context.end();

    deleteOrder(orderId_);
    context.setResult(null, 200);

}

}


// aQute OpenAPI generator version 1.0.0.201704251535
