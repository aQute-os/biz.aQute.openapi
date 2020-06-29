package aQute.openapi.provider;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicReference;

import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import aQute.lib.io.IO;
import gen.file.FileBase;

public class FileTest {
	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();

	@Test
	public void checkFileUpload() throws Exception {
		AtomicReference<String> ref = new AtomicReference<>();
		
		class X extends FileBase {

			@Override
			protected void file(Part file) throws Exception {
				String collect = IO.collect(file.getInputStream());
				ref.set(collect);
			}

		}
		rule.add(new X());

		
		String mp = "--12345\r\n" + 
				"Content-Disposition: form-data; name=\"file\"\r\n" + 
				"\r\n" + 
				"binary data\r\n" + 
				"--12345--";
		
		TaggedData go = rule.http.build().post().upload(mp).asTag().headers("Content-Type", "multipart/form-data;boundary=12345").go(rule.uri.resolve("/v1/file"));
		assertEquals(200, go.getResponseCode());
		assertThat( ref.get()).isEqualTo("binary data");
	}


}
