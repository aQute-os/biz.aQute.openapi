package aQute.openapi.provider;

import java.nio.charset.StandardCharsets;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import aQute.lib.io.IO;
import gen.imagereturn.ImagereturnBase;

public class BinaryReturnTest extends Assert {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void checkBinaryReturnAndMimeTypes() throws Exception {
		class X extends ImagereturnBase {

			/**
			 * Declared with multiple 
			 */
			@Override
			protected MimeWrapper image_multiple_mime() throws Exception {
				return getOpenAPIContext().wrap("image/jpeg", new byte[0]);
			}

			@Override
			protected MimeWrapper image_one_mime() throws Exception {
				return getOpenAPIContext().wrap("image/png", new byte[0]);
			}

			@Override
			protected byte[] image_json() throws Exception {
				return "{}".getBytes(StandardCharsets.UTF_8);
			}
		}
		rule.add(new X());

		TaggedData go = rule.http.build().get().asTag().go(rule.uri.resolve("/v1/image_multiple_mime"));
		assertEquals(200, go.getResponseCode());
		assertEquals("image/jpeg", go.getConnection().getContentType());

		go = rule.http.build().get().asTag().go(rule.uri.resolve("/v1/image_one_mime"));
		assertEquals(200, go.getResponseCode());
		assertEquals("image/png", go.getConnection().getContentType());

		go = rule.http.build().get().asTag().go(rule.uri.resolve("/v1/image_json"));
		assertEquals(200, go.getResponseCode());
		assertEquals("application/json", go.getConnection().getContentType());
		String collect = IO.collect(go.getInputStream());
		assertEquals( "{}", collect);
	}

}
