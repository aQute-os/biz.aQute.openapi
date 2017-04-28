package aQute.openapi.provider;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.felix.service.command.Converter;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.service.http.NamespaceException;

import aQute.json.codec.JSONCodec;
import aQute.lib.io.IO;
import junit.framework.TestCase;
import local.test.accesstokenapi.GeneratedAccessTokenApi;
import local.test.accesstokenapi.GeneratedAccessTokenApi.TokenResult;

public class BasicRuntimeTest extends TestCase {
	Server					server	= new Server(0);
	URI						uri;
	private ServletHandler	handler;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		handler = new ServletHandler();
		server.setHandler(handler);
		server.start();
		while (!(server.isStarted() || server.isRunning()))
			Thread.sleep(100);

		uri = server.getURI();
	}

	@Override
	protected void tearDown() throws Exception {
		server.stop();
		server.join();
		super.tearDown();
	}

	class HelloServlet extends HttpServlet {
		private static final long serialVersionUID = 1L;

		public void doGet(HttpServletRequest rq, HttpServletResponse rsp) throws IOException {
			rsp.getWriter().println("Hello World");
		}
	}

	public void testSimple() throws Exception {
		System.out.println(uri);
		handler.addServletWithMapping(new ServletHolder(new HelloServlet()), "/*");
		URL url = new URL(uri + "/foo");
		String s = IO.collect(url.openStream());
		assertEquals("Hello World\n", s);
	}

	/************************************************/

	public static class AccessTokenImpl extends GeneratedAccessTokenApi {

		@Override
		protected TokenResult accessTokenPost(String username, String password) throws Exception {
			TokenResult token = new TokenResult();
			token.accessToken = "AccessToken";
			token.expireDateTime = OffsetDateTime.now();
			return token;
		}

		@Override
		protected TokenResult refresh() throws Exception {
			return null;
		}
	}

	public void testRuntime() throws Exception {
		OpenAPIRuntime runtime = getRuntime();
		AccessTokenImpl impl = new AccessTokenImpl();
		runtime.add(impl);

		URL url = new URL(uri + "api/v1/accessToken?username=peter&password=sec%2Dret");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		TokenResult tokenResult = new JSONCodec().dec().from(connection.getInputStream()).get(TokenResult.class);
		assertEquals(1234, tokenResult.expireDateTime);
		assertEquals("AccessToken", tokenResult.accessToken);

		url = new URL(uri + "api/v1/accessToken/%3123454%35?password=secret");
		connection = (HttpURLConnection) url.openConnection();
		connection.setRequestMethod("POST");
		tokenResult = new JSONCodec().dec().from(connection.getInputStream()).get(TokenResult.class);
		assertEquals(1234545, tokenResult.expireDateTime);
		assertEquals("secret", tokenResult.accessToken);

	}

	public void testGogo() throws Exception {
		OpenAPIRuntime runtime = getRuntime();

		OpenAPIGogo gogo = new OpenAPIGogo();
		gogo.runtime = runtime;
		Set<String> dispatchers = gogo.dispatchers();
		assertTrue(dispatchers.isEmpty());

		AccessTokenImpl impl = new AccessTokenImpl();
		runtime.add(impl);

		dispatchers = gogo.dispatchers();
		assertTrue(!dispatchers.isEmpty());
		String first = dispatchers.iterator().next();
		assertEquals("/api/v1", first);
		Dispatcher dispatcher = gogo.dispatcher(first);
		assertNotNull(dispatcher);

		assertEquals(1, dispatcher.targets.size());

		OpenAPIBase base = dispatcher.targets.get(0).base;

		assertEquals(impl, base);

		assertEquals(
				"Base Path        /api/v1\n" //
						+ "  AccessTokenImpl\n"
						+ "     AccessTokenPost      POST   /accessToken?username&password RETURN TokenResult\n"
						+ "     Refresh              POST   /accessToken/refresh RETURN TokenResult\n",
				gogo.format(dispatcher, Converter.INSPECT, null).toString());
		assertEquals("/api/v1              [AccessTokenImpl]",
				gogo.format(dispatcher, Converter.LINE, null).toString());
		assertEquals("/api/v1", gogo.format(dispatcher, Converter.PART, null).toString());

	}

	public void testDateEncoding() throws IOException
	{
		// class X extends sma.accesstokenapi.GeneratedAccessTokenApi {
		//
		// @Override
		// protected TokenResult accessTokenPost(String username, String
		// password) throws Exception {
		// return refresh();
		// }
		//
		// @Override
		// protected TokenResult refresh() throws Exception {
		//
		// return new TokenResult().accessToken("fooo")
		// .expireDateTime(OffsetDateTime.of(1972, 3, 22, 10, 33, 00, 00,
		// ZoneOffset.UTC));
		// }
		// }
		//
		// OpenAPIRuntime runtime = getRuntime();
		// runtime.add(new X());
		// URL url = new URL(uri +
		// "api/v1/accessToken?username=peter&password=sec%2Dret");
		// String s = IO.collect(url.openStream());
		//
		// assertTrue(s, s.contains("\"xx\""));
	}

	private OpenAPIRuntime getRuntime() {
		OpenAPIRuntime runtime = new OpenAPIRuntime() {

			@Override
			public Closeable registerServlet(String alias, Servlet servlet)
					throws ServletException, NamespaceException {
				ServletHolder servletHolder = new ServletHolder(servlet);
				handler.addServletWithMapping(servletHolder, (alias + "/*"));
				return () -> {};
			}
		};
		return runtime;
	}
}
