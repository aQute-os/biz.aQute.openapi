package biz.aQute.openapi.runtime.test;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import generated.apikey.ApikeyBase;

public class ApikeyTest extends Assert 
{
   @Rule
   public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

   @Test
   public void testAccess() throws Exception {
      class X extends ApikeyBase
      {

         @Override
         protected Response use(String header) throws Exception
         {
            // TODO Auto-generated method stub
            return null;
         }

         @Override
         protected Response login(String header) throws Exception, UnauthorizedResponse
         {
            // TODO Auto-generated method stub
            return null;
         }

      }
      
      rule.add(new X());

      TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/v1/operation"));
      assertEquals( 403, go.getResponseCode());
      
      go = rule.http.build().put().upload("{}").asTag().go(rule.uri.resolve("/v1/operation"));
      assertEquals( 200, go.getResponseCode());
   }
}
