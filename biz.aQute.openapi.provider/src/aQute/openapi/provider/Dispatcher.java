package aQute.openapi.provider;

import java.io.Closeable;
import java.io.IOException;
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

import aQute.openapi.provider.OpenAPIRuntime.Tracker;
import aQute.openapi.security.api.OpenAPISecurityProvider;

public class Dispatcher extends HttpServlet {
	private static final int					SECURITY_PROVIDER_TIMEOUT	= 5000;
	private static final long					serialVersionUID			= 1L;
	final String								prefix;
	final OpenAPIRuntime						runtime;
	final List<Tracker>							targets						= new CopyOnWriteArrayList<>();
	final Map<String,SecurityProviderTracker>	security					= new ConcurrentHashMap<>();
	final Closeable								registration;
	final Object								lock						= new Object();

	public Dispatcher(OpenAPIRuntime runtime, String prefix) throws ServletException, NamespaceException {
		this.runtime = runtime;
		this.prefix = prefix;

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
		try {
			runtime.contexts.set(context);
			try {
				String path = request.getPathInfo();
				if (path.endsWith("/"))
					path = path.substring(1, path.length() - 1);
				else
					path = path.substring(1);

				String segments[] = path.split("/");
				int counter = runtime.delayOn404Timeout;
				do {
					for (Tracker target : targets) {
						OpenAPIBase base = target.base;
						context.setTarget(base);
						base.before_(context);
						try {
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
		} catch (SecurityException se) {
			OpenAPIRuntime.logger.warn("Forbidden {} {}", se, request.getPathInfo());
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception e) {
			OpenAPIRuntime.logger.warn("Server Error {} {}", e, request.getPathInfo());
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			context.report(e);
		}
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

	public OpenAPISecurityProvider getSecurityProvider(String id, String type) throws InterruptedException {
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
		OpenAPISecurityProvider provider = t.waitForService(SECURITY_PROVIDER_TIMEOUT);
		return provider;
	}
}
