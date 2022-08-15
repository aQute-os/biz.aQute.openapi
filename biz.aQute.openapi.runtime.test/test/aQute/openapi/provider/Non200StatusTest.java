package aQute.openapi.provider;

import java.net.URI;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import gen.non200status.Non200statusBase;

public class Non200StatusTest extends Assert {
	
	public static class ErrorDTO {
		public String message;
	}

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void checkNon200Status() throws Exception {
		class X extends Non200statusBase {

			@Override
			protected void non200statusexception() throws Exception {
				throw new Response(204, (Object) null);
			}

			@Override
			protected void non200status() throws Exception {
			}

			@Override
			protected void responsemessage() throws Exception {
			}

		}
		rule.add(new X());

		TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/non200status/non200status"));
		assertEquals(204, go.getResponseCode());

		go = rule.http.build().get().asTag().go(rule.uri.resolve("/non200status/non200statusexception"));
		assertEquals(204, go.getResponseCode());
	}

	@Test
	public void exceptionTest() throws Exception {
		class X extends Non200statusBase {

			@Override
			protected void non200statusexception() throws Exception {
				throw new IllegalArgumentException();
			}

			@Override
			protected void non200status() throws Exception {
			}
			
			@Override
			protected void responsemessage() throws Exception {
			}

		}
		rule.add(new X());

		TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/non200status/non200statusexception"));
		assertEquals(500, go.getResponseCode());
	}
	
	@Test
	public void responseStringMessageTest() throws Exception {
		String message = "Custom message";
		
		class Y extends Non200statusBase {

			@Override
			protected void non200status() throws Exception {
			}

			@Override
			protected void non200statusexception() throws Exception {
			}
			
			@Override
			protected void responsemessage() throws Exception {
				throw new Response(204, message);	

			}
		}
		
		rule.add(new Y());

		URI uri = rule.uri.resolve("/non200status/responsemessage");
		TaggedData go = rule.http.build().get().asTag().go(uri);
		
		assertEquals(204, go.getResponseCode());
		String resultString = rule.http.build().get().get(String.class).go(uri);
		assertEquals(message, resultString);

	}
	
	@Test
	public void responseObjectMessageTest() throws Exception {
		String errorMsg = "Custom error message";
		
		class Y extends Non200statusBase {

			@Override
			protected void non200status() throws Exception {
				
			}

			@Override
			protected void non200statusexception() throws Exception {
			
			}
			
			@Override
			protected void responsemessage() throws Exception {
				ErrorDTO dto = new ErrorDTO();
				dto.message = errorMsg;
				throw new Response(404, dto);	
			}
			
		}
		
		rule.add(new Y());

		URI uri = rule.uri.resolve("/non200status/responsemessage");
		TaggedData go = rule.http.build().get().asTag().go(uri);
		
		System.out.println(go.toString());
		
		assertEquals(404, go.getResponseCode());
		ErrorDTO result = rule.http.build().get().get(ErrorDTO.class).go(uri);
		assertEquals(errorMsg, result.message);
	}
}
