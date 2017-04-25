package gen.apikey;

import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import java.time.format.DateTimeFormatter;
import java.time.OffsetDateTime;
import java.time.LocalDate;

/**
 *
 * <ul>
 *
 * <li>{@link #use(String) GET /operation = use}
 *
 * <li>{@link #login(String) PUT /operation = login}
 *
 * </ul>
 *
 */

@RequireApikeyBase
public abstract class ApikeyBase extends OpenAPIBase
{

   public static final String BASE_PATH = "/v1";

   /**
    *
    * GET /operation = use
    *
    * @param Key – (header)
    *
    * @returns 200 / null 200
    *
    */

   protected abstract Response use(String key) throws Exception;

   /**
    *
    * PUT /operation = login
    *
    * @param Key – (header)
    *
    * @returns 200 / null
    * @returns 401 / null 200
    *
    *          401
    *
    */

   protected abstract Response login(java.util.Optional<String> key) throws Exception, OpenAPIBase.UnauthorizedResponse;

   /**
    *
    * Response
    *
    */

   public static class Response extends OpenAPIBase.DTO
   {

      public String error;

      public Response error(String error)
      {
         this.error = error;
         return this;
      }

      public String error()
      {
         return this.error;
      }

   }

   /*****************************************************************/



//   public OpenAPISecurityDefinition api_key = OpenAPISecurityDefinition.apiKey("api_key", "header", "Key");


   public ApikeyBase()
   {
      super(BASE_PATH, "use                  GET    /operation  RETURN Response",
               "login                PUT    /operation  RETURN Response");
   }

   public boolean dispatch_(OpenAPIContext context, String segments[], int index) throws Exception
   {

      if (index < segments.length && "operation".equals(segments[index]))
      {
         index++;
         if (segments.length == index)
         {
            if (context.isMethod(OpenAPIBase.Method.PUT))
            {
               login_put_(context);
               return true;
            }
            else if (context.isMethod(OpenAPIBase.Method.GET))
            {
               use_get_(context);
               return true;
            }
         }

         // end operation
      }

      if (segments.length == 1 && "openapi.json".equals(segments[0]))
      {
         getOpenAPIContext().copy(gen.apikey.ApikeyBase.class.getResourceAsStream("openapi.json"), "application/json");
         return true;
      }
      return false;
   }

   private void use_get_(OpenAPIContext context) throws Exception
   {

      context.setOperation("use");
      //context.verify(api_key, context.header("Key"));

      String key_ = context.toString(context.header("Key"));


      // VALIDATORS

      context.begin("use");
      context.require(key_, "Key");
      context.end();

      Object result = use(key_);
      context.setResult(result, 200);

   }

   private void login_put_(OpenAPIContext context) throws Exception
   {

      context.setOperation("login");

      java.util.Optional<String> key_ = context.optional(context.toString(context.header("Key")));

      Object result = login(key_);
      context.setResult(result, 200);

   }

}


// aQute OpenAPI generator version 0
