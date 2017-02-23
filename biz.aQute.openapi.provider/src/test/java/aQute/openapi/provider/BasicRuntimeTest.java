package aQute.openapi.provider;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Dictionary;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

import aQute.json.util.JSONCodec;
import aQute.lib.io.IO;
import aQute.openapi.provider.OpenAPIRuntime;
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

		public void doGet(HttpServletRequest rq, HttpServletResponse rsp)
				throws IOException {
			rsp.getWriter().println("Hello World");
		}
	}

	public void testSimple() throws Exception {
		System.out.println(uri);
		handler.addServletWithMapping(new ServletHolder(new HelloServlet()),
				"/*");
		URL url = new URL(uri + "/foo");
		String s = IO.collect(url.openStream());
		assertEquals("Hello World\n", s);
	}

	/************************************************/

	public static class AccessTokenImpl extends GeneratedAccessTokenApi {

		@Override
		protected TokenResult accessTokenPost(String username, String password)
				throws Exception {
			TokenResult token = new TokenResult();
			token.accessToken = "AccessToken";
			token.expireDateTime = 1234;
			return token;
		}

		@Override
		protected TokenResult refresh() throws Exception {
			return null;
		}

		@Override
		protected TokenResult accessTokenGet(long id, char[] password)
				throws Exception {
			TokenResult token = new TokenResult();
			token.accessToken = new String(password);
			token.expireDateTime = id;
			return token;
		}

	}

	public void testRuntime() throws Exception {
		OpenAPIRuntime runtime = getRuntime();
		AccessTokenImpl impl = new AccessTokenImpl();
		runtime.add(impl);

		URL url = new URL(uri + "api/v1/accessToken?username=peter&password=sec%2Dret");
		TokenResult tokenResult = new JSONCodec().dec().from(url.openStream())
				.get(TokenResult.class);
		assertEquals(1234, tokenResult.expireDateTime);
		assertEquals("AccessToken", tokenResult.accessToken);
		
		url = new URL(uri + "api/v1/accessToken/%3123454%35?password=secret");
		tokenResult = new JSONCodec().dec().from(url.openStream())
				.get(TokenResult.class);
		assertEquals(1234545, tokenResult.expireDateTime);
		assertEquals("secret", tokenResult.accessToken);
		
	}

	private OpenAPIRuntime getRuntime() {
		OpenAPIRuntime runtime = new OpenAPIRuntime();
		runtime.http = new HttpService() {

			@Override
			public void unregister(String alias) {
			}

			@Override
			public void registerServlet(String alias, Servlet servlet,
					@SuppressWarnings("rawtypes") Dictionary initparams, HttpContext context)
					throws ServletException, NamespaceException {
				handler.addServletWithMapping(new ServletHolder(servlet),
						(alias + "/*"));
			}

			@Override
			public void registerResources(String alias, String name,
					HttpContext context) throws NamespaceException {

			}

			@Override
			public HttpContext createDefaultHttpContext() {
				return null;
			}
		};
		return runtime;
	}
}
