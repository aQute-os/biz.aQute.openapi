package aQute.openapi.provider;

import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.service.url.TaggedData;
import aQute.openapi.provider.cors.CORSImplementation;
import gen.routes.RoutesBase;

public class CORSTest extends Assert {
	@Rule
	public OpenAPIServerTestRule	rule	= new OpenAPIServerTestRule();

	Logger							l		= LoggerFactory.getLogger("Test");

	class X extends RoutesBase {

		@Override
		protected void a_b_get() throws Exception {
			System.out.println("a_b_get");
		}

		@Override
		protected void a_put() throws Exception {
			System.out.println("a_put");

		}

		@Override
		protected void a_b_post() throws Exception {
			System.out.println("a_b_post");

		}

		@Override
		protected void a_get() throws Exception {
			System.out.println("a_get");

		}

		@Override
		protected void a_post() throws Exception {
			System.out.println("a_post");
		}

	}

	@Test
	public void checkOptionsSimpleHeader() throws Exception {
		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] {}, new String[] {}, true, -1);
		URL resolve = rule.resolve("/routes/a");
		TaggedData tag = rule.http.build().verb("OPTIONS").headers("Origin", "http://foo.com")
				.headers("Access-Control-Request-Method", "GET").asTag().go(resolve);

		System.out.println(tag);
		assertEquals(204, tag.getResponseCode());
	}

	@Test
	public void checkOptionsNotSimpleHeaderWithAllowedHeaders() throws Exception {
		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] {},
				new String[] { "x-foo", "x-bar" }, true, 60);
		URL resolve = rule.resolve("/routes/a");
		TaggedData tag = rule.http.build().verb("OPTIONS").headers("Origin", "http://foo.com")
				.headers("Access-Control-Request-Method", "PUT")
				.headers("Access-Control-Request-Headers", "X-FOO, X-BAR").asTag().go(resolve);

		System.out.println(tag);

		assertEquals(204, tag.getResponseCode());

		HttpURLConnection c = (HttpURLConnection) tag.getConnection();
		assertEquals("x-foo,x-bar", c.getHeaderField("Access-Control-Allow-Headers"));
		assertEquals("true", c.getHeaderField("Access-Control-Allow-Credentials"));
		assertEquals("http://foo.com", c.getHeaderField("Access-Control-Allow-Origin"));
		assertEquals("GET,POST,PUT", c.getHeaderField("Access-Control-Allow-Methods"));
		assertEquals("60", c.getHeaderField("Access-Control-Max-Age"));
	}

	@Test
	public void checkOptionsNotSimpleHeader() throws Exception {
		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] {}, new String[] {}, true, 60);
		URL resolve = rule.resolve("/routes/a");
		TaggedData tag = rule.http.build().verb("OPTIONS").headers("Origin", "http://foo.com")
				.headers("Access-Control-Request-Method", "PUT").asTag().go(resolve);

		System.out.println(tag);

		HttpURLConnection c = (HttpURLConnection) tag.getConnection();
		assertEquals(null, c.getHeaderField("Access-Control-Allow-Headers"));
		assertEquals("true", c.getHeaderField("Access-Control-Allow-Credentials"));
		assertEquals("http://foo.com", c.getHeaderField("Access-Control-Allow-Origin"));
		assertEquals("GET,POST,PUT", c.getHeaderField("Access-Control-Allow-Methods"));
		assertEquals("60", c.getHeaderField("Access-Control-Max-Age"));
		assertEquals("true", c.getHeaderField("Access-Control-Allow-Credentials"));
		assertEquals(204, tag.getResponseCode());
	}

	@Test
	public void checkNoCredentials() throws Exception {
		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] {}, new String[] {}, false, 60);
		URL resolve = rule.resolve("/routes/a");
		TaggedData tag = rule.http.build().verb("OPTIONS").headers("Origin", "http://foo.com")
				.headers("Access-Control-Request-Method", "PUT").asTag().go(resolve);

		System.out.println(tag);

		HttpURLConnection c = (HttpURLConnection) tag.getConnection();
		assertEquals(null, c.getHeaderField("Access-Control-Allow-Headers"));
		assertEquals("http://foo.com", c.getHeaderField("Access-Control-Allow-Origin"));
		assertEquals("GET,POST,PUT", c.getHeaderField("Access-Control-Allow-Methods"));
		assertEquals("60", c.getHeaderField("Access-Control-Max-Age"));
		assertEquals(null, c.getHeaderField("Access-Control-Allow-Credentials"));
		assertEquals(204, tag.getResponseCode());
	}

	@Test
	public void getSimple() throws Exception {
		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] { "x-foo" }, new String[] {}, false,
				60);
		URL resolve = rule.resolve("/routes/a");
		TaggedData tag = rule.http.build().get().headers("Origin", "http://foo.com").asTag().go(resolve);

		System.out.println(tag);
		assertEquals(200, tag.getResponseCode());

		HttpURLConnection c = (HttpURLConnection) tag.getConnection();
		assertEquals("x-foo", c.getHeaderField("Access-Control-Expose-Headers"));
		assertEquals("http://foo.com", c.getHeaderField("Access-Control-Allow-Origin"));
		assertEquals(null, c.getHeaderField("Access-Control-Allow-Credentials"));
	}

	@Test
	public void getSimpleNoCredentials() throws Exception {
		rule.add(new X());
		rule.runtime.cors = new CORSImplementation(l, new String[] {}, new String[] { "x-foo" }, new String[] {}, true,
				60);
		URL resolve = rule.resolve("/routes/a");
		TaggedData tag = rule.http.build().get().headers("Origin", "http://foo.com").asTag().go(resolve);

		System.out.println(tag);
		assertEquals(200, tag.getResponseCode());

		HttpURLConnection c = (HttpURLConnection) tag.getConnection();
		assertEquals("x-foo", c.getHeaderField("Access-Control-Expose-Headers"));
		assertEquals("http://foo.com", c.getHeaderField("Access-Control-Allow-Origin"));
		assertEquals("true", c.getHeaderField("Access-Control-Allow-Credentials"));
	}

}
