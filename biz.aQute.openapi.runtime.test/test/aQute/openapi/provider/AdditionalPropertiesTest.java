package aQute.openapi.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import gen.additional_properties.Additional_propertiesBase;

public class AdditionalPropertiesTest {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void test() throws Exception {
		class AP extends Additional_propertiesBase {


			@Override
			protected DeviceResponse additionalProperties(DeviceResponse content) throws Exception {
				System.out.println(content);
				assertEquals( 1, content._links.size());
				assertEquals( 2, content._links.get("foo").size());
				assertEquals( "http://www.foo.com", content._links.get("foo").get(0).href.get());
				assertFalse(content._links.get("foo").get(1).href.isPresent());
				return null;
			}
		}
		rule.add(new AP());

		
		TaggedData go = rule.http.build().put().upload("{ \"_links\": {\"foo\":[ {\"href\":\"http://www.foo.com\"},{}]}}").asTag().go(rule.uri.resolve("/v1/additionalProperties"));
		assertEquals(200, go.getResponseCode());

	}
}
