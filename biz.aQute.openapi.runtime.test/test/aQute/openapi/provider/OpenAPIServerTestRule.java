package aQute.openapi.provider;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.MultipartConfigElement;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.osgi.service.http.NamespaceException;

import aQute.bnd.http.HttpClient;
import aQute.lib.io.IO;

public class OpenAPIServerTestRule implements TestRule {
	static {
		System.setProperty("sun.net.http.allowRestrictedHeaders", "true");
	}
	public URI						uri;
	public int						port;
	public Server					server					= new Server(0);
	public ServletContextHandler	handler;
	public Map<String, Object>		configuration			= new HashMap<>();
	public OpenAPIRuntime			runtime					= new OpenAPIRuntime() {
																@Override
																public java.io.Closeable registerServlet(String alias,
																		Servlet servlet)
																		throws ServletException, NamespaceException {
																	if (alias.equals("/"))
																		alias = "";
																	ServletHolder servletHolder = new ServletHolder(servlet);
																	handler.addServlet(servletHolder,
																			(alias + "/*"));
																	MultipartConfigElement mce = new MultipartConfigElement(IO.getFile("generated").getAbsolutePath());
																	servletHolder.getRegistration().setMultipartConfig(mce);
																	return () -> {
																															};
																};
															};
	public SecurityProviderManager	securityProviderManager	= new SecurityProviderManager();
	public HttpClient				http					= new HttpClient();

	@Override
	public Statement apply(Statement statement, Description description) {

		return new Statement() {

			@Override
			public void evaluate() throws Throwable {
				try {
					handler = new ServletContextHandler(
							ServletContextHandler.SESSIONS);
					handler.setContextPath("/");
					ServletHolder servletHolder = new ServletHolder(securityProviderManager);
					handler.addServlet(servletHolder,
							SecurityProviderManager.PATTERN);
					server.setHandler(handler);
					server.start();

					while (!(server.isStarted() || server.isRunning()))
						Thread.sleep(100);

					uri = server.getURI();
					port = uri.getPort();
					statement.evaluate();
				} finally {
					server.stop();
					server.join();
				}
			}
		};
	}

	public <X extends OpenAPIBase> X add(X x) throws Exception {
		runtime.add(x);
		return x;

	}

	public String put(String path, String payload) throws Exception {
		URL uri = resolve(path);
		return http.build().put().upload(payload.replace('\'', '"')).get(String.class).go(uri).replace('"', '\'');
	}

	public URL resolve(String path) throws MalformedURLException {
		URL uri = this.uri.resolve(path).toURL();
		return uri;
	}

}
