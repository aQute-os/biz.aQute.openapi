package aQute.openapi.provider;

import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import gen.non200status.Non200statusBase;

public class Non200StatusTest extends Assert {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void checkNon200Status() throws Exception {
		class X extends Non200statusBase {

			@Override
			protected void non200statusexception() throws Exception {
				throw new Response(410, (Object)"really bad, bad, bad request");
			}

			@Override
			protected void non200status() throws Exception {
			}

		}
		rule.add(new X());

		TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/non200status/non200status"));
		assertEquals(204, go.getResponseCode());

		go = rule.http.build().retries(0).get().asTag().go(rule.uri.resolve("/non200status/non200statusexception"));
		assertEquals(410, go.getResponseCode());
		Assertions.assertThat(go.toString()).contains("really bad, bad, bad request");
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

		}
		rule.add(new X());

		TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/non200status/non200statusexception"));
		assertEquals(500, go.getResponseCode());
	}
}
