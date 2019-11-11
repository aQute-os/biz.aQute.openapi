package aQute.openapi.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import gen.fieldcasing.FieldcasingBase;

public class FieldcasingTest {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();


	@Test
	public void testFieldCasing() throws Exception {
		Semaphore s = new Semaphore(0);
		
		class Fieldcasing extends FieldcasingBase {

			@Override
			protected Response putParameter(Body body) throws Exception {
				assertEquals("1",body.StartsWithUppercase.get());
				s.release();
				return null;
			}
		}
		rule.add(new Fieldcasing());

		TaggedData go = rule.http.build()
				.put()
				.asTag()
				.upload("{\"StartsWithUppercase\":\"1\"}")
				.go(rule.uri.resolve("/v1/parameter"));

		assertEquals(200,go.getResponseCode());
		assertTrue( s.tryAcquire(3, TimeUnit.SECONDS));
	}

}
