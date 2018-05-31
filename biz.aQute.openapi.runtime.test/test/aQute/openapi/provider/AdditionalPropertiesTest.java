package aQute.openapi.provider;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

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
			protected void test(Type1000 body) throws Exception {
				System.out.println(body);
				assertEquals( body.__extra.get("n30"), 30);
				assertEquals( body.__extra.get("sbar"), "bar");
				
				Map<String,Object> map = new HashMap<>();
				map.put("a", 1);
				map.put("b", 2);
				assertEquals( body.__extra.get("object"), map);
			}

		}
		rule.add(new AP());

		
		TaggedData go = rule.http.build().put().upload("{\"n30\":30,\"sbar\":\"bar\", \"object\": { \"a\":1, \"b\":2}}").asTag().go(rule.uri.resolve("/v1/additionalProperties"));
		assertEquals(200, go.getResponseCode());

	}
}
