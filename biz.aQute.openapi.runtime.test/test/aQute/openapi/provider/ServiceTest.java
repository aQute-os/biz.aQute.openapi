package aQute.openapi.provider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import aQute.openapi.provider.OpenAPIRuntime.CodecType;
import gen.useofservice.UseofserviceBase;

public class ServiceTest {

	@Rule
	public OpenAPIServerTestRule rule = new OpenAPIServerTestRule();


	@Test
	public void testCodecService() throws Exception {
		AtomicBoolean encode = new AtomicBoolean(false);
		AtomicBoolean decode = new AtomicBoolean(false);
		rule.runtime.codecType= CodecType.SERVICE;
		rule.runtime.serviceCodec= new CodecWrapper() {
			@Override
			public String encode(Object object, OutputStream out) throws Exception {
				encode.set(true);
				return super.encode(object, out);
			}
			@Override
			public <T, X> T decode(Class<T> type, InputStream in, String mime, Function<Class<X>, X> instantiator)
					throws Exception {
				decode.set(true);
				return super.decode(type, in, mime, instantiator);
			}
			
			@Override
			public <T, X> List<T> decodeList(Class<T> type, InputStream in, String mime,
					Function<Class<X>, X> instantiator) throws Exception {
				decode.set(true);
				return super.decodeList(type, in, mime, instantiator);
			}
		};
		Semaphore s = new Semaphore(0);
		
		class CodecServiceTest extends UseofserviceBase {

			@Override
			protected Body putParameter(Body body) throws Exception {
				assertEquals("1",body.StartsWithUppercase.get());
				s.release();
				return body;
			}
		}
		rule.add(new CodecServiceTest());

		TaggedData go = rule.http.build()
				.put()
				.asTag()
				.upload("{\"StartsWithUppercase\":\"1\"}")
				.go(rule.uri.resolve("/v1/parameter"));

		assertEquals(200,go.getResponseCode());
		assertTrue( s.tryAcquire(3, TimeUnit.SECONDS));
		assertTrue(decode.get());
		assertTrue(encode.get());
	}

}
