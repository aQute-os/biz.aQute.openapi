package biz.aQute.openapi.runtime.test;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import aQute.openapi.provider.OpenAPIContext;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPISecurityProvider;
import gen.apikey.ApikeyBase;

public class ApikeyTest extends Assert
{
   @Rule
   public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

   @Test
   public void testAccess() throws Exception
   {
      class X extends ApikeyBase
      {

         @Override
         protected Response use(String header) throws Exception
         {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         protected Response login(Optional<String> header) throws Exception, UnauthorizedResponse
         {
            // TODO Auto-generated method stub
            return null;
         }

      }

      rule.add(new X());

      TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/v1/operation"));
      assertEquals(403, go.getResponseCode());

      go = rule.http.build().put().upload("{}").asTag().go(rule.uri.resolve("/v1/operation"));
      assertEquals(200, go.getResponseCode());

//      rule.runtime.apiKey = new APIKeyProvider()
//      {
//
//         @Override
//         public boolean check(OpenAPIContext context, APIKeyDTO dto, String operation, String headerValue)
//         {
//            return "ok".equals(headerValue);
//         }
//      };

      go = rule.http.build().get().asTag().headers("Key", "ok").go(rule.uri.resolve("/v1/operation"));
      assertEquals(200, go.getResponseCode());

      go = rule.http.build().get().asTag().headers("Key", "not ok").go(rule.uri.resolve("/v1/operation"));
      assertEquals(403, go.getResponseCode());
   }

   @Test
   public void testLogin() throws Exception
   {
      AtomicBoolean ok = new AtomicBoolean(false);

      class X extends ApikeyBase
      {

         @Override
         protected Response use(String header) throws Exception
         {
            return null;
         }

         @Override
         protected Response login(Optional<String> header) throws Exception, UnauthorizedResponse
         {
            ok.set(true);
            return null;
         }

      }

      rule.add(new X());

//      rule.runtime.security.add(new OpenAPISecurityProvider()
//      {
//
//         @Override
//         public boolean verify(OpenAPIContext context, OpenAPISecurityDefinition dto, String... scopes)
//         {
//            // TODO Auto-generated method stub
//            return false;
//         }
//
//         @Override
//         public void forceAuthentication(OpenAPIContext context, OpenAPISecurityDefinition dto, String... scopes)
//         {
//            // TODO Auto-generated method stub
//
//         }
//      });
      TaggedData getUse = rule.http.build().get().asTag().headers("Key", "ok").go(rule.uri.resolve("/v1/operation"));
      assertEquals(403, getUse.getResponseCode());

      TaggedData putLogin = rule.http.build().put().asTag().go(rule.uri.resolve("/v1/operation"));
      assertEquals(200, putLogin.getResponseCode());

      getUse = rule.http.build().get().asTag().headers("Key", "ok").go(rule.uri.resolve("/v1/operation"));
      assertEquals(200, getUse.getResponseCode());
   }

}
