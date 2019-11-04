package aQute.openapi.provider;

import java.util.List;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import gen.parameters.ParametersBase;

public class ParameterSourceTest extends Assert {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void checkPostParameterSources() throws Exception {
		class X extends ParametersBase {

			@Override
			protected Response postParameter(String path, String form1, int form2, String header, String query)
					throws Exception {
				Response response = new Response();

				try {
					assertEquals("1", form1);
					assertEquals(2, form2);
					assertEquals("PATH", path);
					assertEquals("HEADER", header);
					assertEquals("QUERY", query);

				} catch (Throwable e) {
					response.error = e.getMessage();
				}
				return response;
			}

			@Override
			protected Response putParameter(Body body, String path, String header, String query) throws Exception {
				Response response = new Response();

				try {
					assertEquals("PAYLOAD", body.payload);
					assertEquals("PATH", path);
					assertEquals("HEADER", header);
					assertEquals("QUERY", query);

				} catch (Throwable e) {
					response.error = e.getMessage();
				}
				return response;
			}

			@Override
			protected Response arrayConversion(String path, List<String> array, List<String> arrayNone, List<String> arrayPipes,
					List<String> arrayTsv, List<String> arrayMulti, List<String> arrayCsv, List<String> arraySsv)
					throws Exception {
				// TODO Auto-generated method stub
				return null;
			}

		}
		rule.add(new X());

		String result = rule.http.build().post().headers("header", "HEADER").upload("form1=1&form2=2").get(String.class)
				.go(rule.uri.resolve("/v1/parameter/PATH?query=QUERY")).replace('"', '\'');
		assertEquals("{}", result);

		result = rule.http.build().put().headers("header", "HEADER").upload("{\"payload\":\"PAYLOAD\"}")
				.get(String.class)
				.go(rule.uri.resolve("/v1/parameter/PATH?query=QUERY")).replace('"', '\'');
		assertEquals("{}", result);
	}
}
