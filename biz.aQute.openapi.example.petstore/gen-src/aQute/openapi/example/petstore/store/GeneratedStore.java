package aQute.openapi.example.petstore.store;

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
 * @param body – order placed for purchasing the pet (body) collectionFormat=%scsv
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
 * @param orderId – ID of pet that needs to be fetched (path) collectionFormat=%scsv
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

protected abstract java.util.Map<String,Integer> getInventory() throws Exception;

/**
 * 
 * DELETE /store/order/{orderId} = deleteOrder
 * 
 * Delete purchase order by ID
 * 
 * For valid response try integer IDs with value < 1000. Anything above 1000 or nonintegers will generate API errors
 * 
 * @param orderId – ID of the order that needs to be deleted (path) collectionFormat=%scsv
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

    public Optional<Long> petId = Optional.empty();
    public Optional<Integer> quantity = Optional.empty();
    public Optional<Long> id = Optional.empty();
    public Optional<java.time.Instant> shipDate = Optional.empty();
    public Optional<Boolean> complete = Optional.empty();
    public Optional<StatusEnum> status = Optional.empty();

    public void validate(OpenAPIContext context, String name) {
       context.begin(name);
       if  (this.quantity.isPresent() ) {
    context.validate(this.quantity.get() <= 23, this.quantity.get(), "this.quantity.get()", "this.quantity.get() <= 23");
       }
     context.end();
    }
    public Order petId(Long petId){ this.petId=Optional.ofNullable(petId); return this; }
    public Optional<Long> petId(){ return this.petId; }

    public Order quantity(Integer quantity){ this.quantity=Optional.ofNullable(quantity); return this; }
    public Optional<Integer> quantity(){ return this.quantity; }

    public Order id(Long id){ this.id=Optional.ofNullable(id); return this; }
    public Optional<Long> id(){ return this.id; }

    public Order shipDate(java.time.Instant shipDate){ this.shipDate=Optional.ofNullable(shipDate); return this; }
    public Optional<java.time.Instant> shipDate(){ return this.shipDate; }

    public Order complete(Boolean complete){ this.complete=Optional.ofNullable(complete); return this; }
    public Optional<Boolean> complete(){ return this.complete; }

    public Order status(StatusEnum status){ this.status=Optional.ofNullable(status); return this; }
    public Optional<StatusEnum> status(){ return this.status; }

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

  /*****************************************************************/

  public GeneratedStore() {
    super(BASE_PATH,aQute.openapi.example.petstore.GeneratedBase.class,
         "placeOrder           POST   /store/order  PAYLOAD Order  RETURN Order",
         "getOrderById         GET    /store/order/{orderId}  RETURN Order",
         "getInventory         GET    /store/inventory  RETURN java.util.Map<String,Integer>",
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
          return getOpenAPIContext().doOptions("GET");

        }

        // end inventory
      }  else       if( index < segments.length && "order".equals(segments[index])) {
        index++;
        if ( segments.length == index) {
          if ( context.isMethod(OpenAPIBase.Method.POST)) {
            placeOrder_post_(context);
            return true;
          } 
          return getOpenAPIContext().doOptions("POST");

        } else           if ( index < segments.length ) {
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
            return getOpenAPIContext().doOptions("DELETE", "GET");

          }


        }

        // end order
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
       if  ( context.require(body_, "body_") ) {
       body_.validate(context, "body_");
       }
    context.end();

    Object result = context.call( ()-> placeOrder(body_));
    context.setResult(result, 200);

}

private void getOrderById_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getOrderById");
Long orderId_ = context.toLong(context.path("orderId"));


    //  VALIDATORS 

    context.begin("getOrderById");
    context.validate(orderId_ >= 1, orderId_, "orderId_", "orderId_ >= 1");
    context.validate(orderId_ <= 5, orderId_, "orderId_", "orderId_ <= 5");
    context.end();

    Object result = context.call( ()-> getOrderById(orderId_));
    context.setResult(result, 200);

}

private void getInventory_get_(OpenAPIContext context) throws Exception{

    context.setOperation("getInventory");
    context.verify(aQute.openapi.example.petstore.GeneratedBase.api_key).verify();

    Object result = context.call( ()-> getInventory());
    context.setResult(result, 200);

}

private void deleteOrder_delete_(OpenAPIContext context) throws Exception{

    context.setOperation("deleteOrder");
Long orderId_ = context.toLong(context.path("orderId"));


    //  VALIDATORS 

    context.begin("deleteOrder");
    context.validate(orderId_ >= 1, orderId_, "orderId_", "orderId_ >= 1");
    context.end();

    context.call( () -> { deleteOrder(orderId_); return null; });
    context.setResult(null, 200);

}

}

