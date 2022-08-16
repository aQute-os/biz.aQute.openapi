package gen.payloadonerror;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import aQute.openapi.provider.OpenAPIServerTestRule;

public class PayloadonerrorBaseTest {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void test() throws Exception {
		class X extends PayloadonerrorBase {

			@Override
			protected void simple() throws Exception {
				HTTPError httpError = new HTTPError();
				httpError.message="this is a 401";
				throw new Response(401, httpError);
			}
			
		}
		rule.add(new X());
		

		TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/v1/simple"));
		assertEquals(401, go.getResponseCode());
		assertThat(go.toString()).contains("{\"message\":\"this is a 401\"}");
		
		

	}

}
