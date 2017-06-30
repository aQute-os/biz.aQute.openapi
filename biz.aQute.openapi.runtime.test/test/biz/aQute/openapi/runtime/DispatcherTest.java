package biz.aQute.openapi.runtime;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import aQute.bnd.service.url.TaggedData;
import aQute.lib.converter.Converter;
import aQute.libg.map.MAP;
import aQute.openapi.provider.OpenAPIRuntime;
import aQute.openapi.provider.OpenAPIRuntime.Configuration;
import gen.simple.SimpleBase;

public class DispatcherTest {
	static ScheduledExecutorService	executor	= Executors.newScheduledThreadPool(2);

	@Rule
	public OpenAPIServerTestRule	runtime		= new OpenAPIServerTestRule();
	DummyFramework					fw;

	@Before
	public void before() throws Exception {
		fw = new DummyFramework();
	}

	@After
	public void close() throws Exception {
		fw.close();
	}

	@Test
	public void testNoBlockingWhenNotFound() throws Exception {
		runtime.runtime.activate(fw.context, getConfig(MAP.$("registerOnStart", new String[] {})));
		long start = System.currentTimeMillis();
		TaggedData go = runtime.http.build().get().asTag().go(runtime.uri.resolve("/abc/whatever"));
		assertThat(go.getResponseCode(), is(404));
		assertThat(System.currentTimeMillis() - start, lessThan(5000L));
	}

	@Test
	public void testBlockingWhenNotFound() throws Exception {
		runtime.runtime.activate(fw.context,
				getConfig(MAP.$("registerOnStart", (Object) new String[] { "/abc" }).$("delayOnNotFoundInSecs", 2)));
		long start = System.currentTimeMillis();
		TaggedData go = runtime.http.build().get().asTag().go(runtime.uri.resolve("/abc/whatever"));
		assertThat(go.getResponseCode(), is(404));
		assertThat(System.currentTimeMillis() - start, greaterThan(2000L));
	}

	@Test
	public void testBlockingWhenNotFoundUntilRegistered() throws Exception {
		System.out.println("testBlockingWhenNotFoundUntilRegistered");
		AtomicBoolean visited = new AtomicBoolean();

		runtime.runtime.activate(fw.context,
				getConfig(MAP.$("registerOnStart", (Object) new String[] { "/v1" }).$("delayOnNotFoundInSecs",
						2)));
		long start = System.currentTimeMillis();

		executor.schedule(() -> {
			System.out.println("Adding /v1");
			runtime.runtime.add(new SimpleBase() {

				@Override
				protected void simple() throws Exception {
					visited.set(true);
				}
			});
		}, 1000, TimeUnit.MILLISECONDS);

		System.out.println("Waiting for response ");
		TaggedData go = runtime.http.build().get().asTag().go(runtime.uri.resolve("/v1/simple"));
		System.out.println("Response " + go);
		assertThat(go.getResponseCode(), is(200));
		assertThat(System.currentTimeMillis() - start, lessThan(2000L));
		assertThat(visited.get(), is(true));
	}

	private Configuration getConfig(Map<String, Object> config) throws Exception {
		return Converter.cnv(OpenAPIRuntime.Configuration.class, config);
	}

}
