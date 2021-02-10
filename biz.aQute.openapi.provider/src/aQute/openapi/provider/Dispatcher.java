package aQute.openapi.provider;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.http.NamespaceException;

import aQute.lib.strings.Strings;
import aQute.openapi.provider.OpenAPIRuntime.Tracker;
import aQute.openapi.security.api.OpenAPIAuthenticator;

public class Dispatcher extends HttpServlet {
	private static final int					SECURITY_PROVIDER_TIMEOUT	= 5000;
	private static final long					serialVersionUID			= 1L;
	final String								prefix;
	final OpenAPIRuntime						runtime;
	final List<Tracker>							targets						= new CopyOnWriteArrayList<>();
	final Map<String,SecurityProviderTracker>	security					= new ConcurrentHashMap<>();
	final Closeable								registration;
	final Object								lock						= new Object();
	final String								cacheControl;

	public Dispatcher(OpenAPIRuntime runtime, String prefix) throws ServletException, NamespaceException {
		this.runtime = runtime;
		this.prefix = prefix;

		if (runtime.configuration != null && runtime.configuration.cacheControl().length > 0) {
			cacheControl = Strings.join(runtime.configuration.cacheControl());
		} else
			cacheControl = null;

		assert this.prefix.startsWith("/");

		registration = runtime.registerServlet(this.prefix, this);
	}

	public void add(Tracker base) {
		synchronized (lock) {
			this.targets.add(base);
			lock.notifyAll();
		}
	}

	public void remove(Tracker base) {
		synchronized (lock) {
			this.targets.remove(base);
		}
	}

	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		OpenAPIContext context = new OpenAPIContext(runtime, this, request, response);
		String encoderName = StandardCharsets.UTF_8.name();
		String path = getUnencodedPathInfo(request, encoderName);
		try {
			try {
				runtime.contexts.set(context);
				try {
					if (path.endsWith("/"))
						path = path.substring(1, path.length() - 1);
					else if (path.startsWith("/"))
						path = path.substring(1);

					String segments[] = path.split("/");
					for (int i = 0; i < segments.length; i++) {
						segments[i] = URLDecoder.decode(segments[i], encoderName);
					}

					int counter = runtime.delayOn404Timeout;
					do {
						for (Tracker target : targets) {
							OpenAPIBase base = target.base;
							context.setTarget(base);
							base.before_(context);
							try {
								doFinalHeaders(request, response);
								if (base.dispatch_(context, segments, 0)) {
									if (response.getContentType() == null)
										response.setContentType("application/json");

									return;
								}
							} finally {
								base.after_(context);
							}
						}

						if (counter-- > 0) {
							synchronized (lock) {
								lock.wait(1000);
							}
						} else {
							response.setStatus(HttpServletResponse.SC_NOT_FOUND);
							return;
						}
					} while (true);
				} finally {
					runtime.contexts.remove();
				}
			} catch (OpenAPIBase.Response e) {
				response.setStatus(e.resultCode);
				for (Entry<String,String> entry : e.headers.entrySet()) {
					response.addHeader(entry.getKey(), entry.getValue());
				}
				Object result = e.getResult();
				context.report(e);
				doFinalHeaders(request, response);
			} catch (OpenAPIBase.DoNotTouchResponse e) {
				// do not touch response
				doFinalHeaders(request, response);
			} catch (SecurityException se) {
				OpenAPIRuntime.logger.warn("Forbidden {} {}", se, request.getPathInfo());
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
			} catch (Exception e) {
				OpenAPIRuntime.logger.warn("Server Error on " + request.getPathInfo(), e);
				context.report(e);
				if (runtime.security == null
						|| !runtime.security.handleException(e, context.getOperation(), request, response))
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (Exception e) {
			OpenAPIRuntime.logger.error("Failure in post request processing", e);
		}
	}

	private void doFinalHeaders(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (runtime.cors != null) {
			runtime.cors.fixup(request, response);
		}
		if (cacheControl != null && response.getHeader("Cache-Control") == null) {
			response.setHeader("Cache-Control", cacheControl);
		}
	}

	private String getUnencodedPathInfo(HttpServletRequest request, String encoderName)
			throws UnsupportedEncodingException {
		String path = request.getRequestURI()
				.substring(request.getContextPath().length() + request.getServletPath().length());

		int pos = path.indexOf(';');
		if (pos != -1) {
			path = path.substring(0, pos);
		}
		return path;
	}

	@Override
	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append(prefix);
		s.append(" - ").append(targets);
		return super.toString();
	}

	public void close() throws IOException {
		security.values().forEach(SecurityProviderTracker::close);
		registration.close();
	}

	public OpenAPIAuthenticator getSecurityProvider(String id, String type) throws InterruptedException {
		String key = id + "-" + type;
		SecurityProviderTracker t;

		synchronized (security) {
			t = security.get(key);
			if (t == null) {
				t = new SecurityProviderTracker(runtime.context, id, type);
				security.put(key, t);
				t.open();
			}
		}
		OpenAPIAuthenticator provider = t.waitForService(SECURITY_PROVIDER_TIMEOUT);
		return provider;
	}
}
