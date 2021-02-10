package aQute.openapi.provider;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;

import aQute.lib.converter.TypeReference;
import gen.formdata.FormdataBase;

public class FormDataTest {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	static class FormData extends FormdataBase {

		@Override
		protected Iterable<? extends String> form(String s_1, String s_12, List<String> a_1,
				List<String> a_12, List<String> s_1c2_csv) throws Exception {

			List<String> result = new ArrayList<>();
			result.add(s_1);
			result.add(s_12);
			result.addAll(a_1);
			result.addAll(a_12);
			result.addAll(s_1c2_csv);

			return result;
		}

		@Override
		protected String oauth2(String grant_type, String username, String password) throws Exception {
			return grant_type + ":" + username + ":" + password;
		}

	}

	@Test
	public void testFormData() throws Exception {
		rule.add(new FormData());

		List<String> result = rule.http.build()
				.post()
				.upload("s_1=1&s_12=1&s_12=2&a_1=1&a_12=1&a_12=2&s_1c2_csv=1,2")
				.get(new TypeReference<List<String>>() {
				})
				.go(rule.uri.resolve("/formdata/test"));

		assertThat(result).contains(				"1",
				"1", "2",
				"1",
				"1", "2",
				"1", "2");
	}

	@Test
	public void testOAuth2() throws Exception {
		rule.add(new FormData());

		String result = rule.http.build()
				.post()
				.headers("header", "HEADER")
				.upload("grant_type=password&username=johndoe&password=A3ddj3w")
				.get(String.class)
				.go(rule.uri.resolve("/formdata/oauth2"));

		assertThat(result).isEqualTo("\"password:johndoe:A3ddj3w\"");
	}
}
