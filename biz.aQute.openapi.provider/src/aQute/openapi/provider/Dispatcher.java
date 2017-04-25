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

import aQute.lib.exceptions.Exceptions;
import aQute.openapi.provider.OpenAPIRuntime.Tracker;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPISecurityProvider;

public class Dispatcher extends HttpServlet {
	private static final long										serialVersionUID	= 1L;
	final String													prefix;
	final OpenAPIRuntime											runtime;
	final List<Tracker>												targets				= new CopyOnWriteArrayList<>();
	final Map<OpenAPISecurityDefinition,SecurityProviderTracker>	security			= new ConcurrentHashMap<>();
	final Closeable													registration;

	public Dispatcher(OpenAPIRuntime runtime, String prefix) throws ServletException, NamespaceException {
		this.runtime = runtime;
		this.prefix = prefix;

		assert this.prefix.startsWith("/");

		registration = runtime.registerServlet(this.prefix, this);
	}

	public synchronized void add(Tracker base) {
		this.targets.add(base);
	}

	public void remove(Tracker base) {
		this.targets.remove(base);
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

				response.setStatus(HttpServletResponse.SC_NOT_FOUND);

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
			response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		} catch (Exception e) {
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

	public void close() {
		security.values().forEach(SecurityProviderTracker::close);
		Exceptions.wrap(registration::close);
	}

	public OpenAPISecurityProvider getSecurityProvider(OpenAPISecurityDefinition def) {
		return security.computeIfAbsent(def, this::newTracker).getService();
	}

	private SecurityProviderTracker newTracker(OpenAPISecurityDefinition def) {
		SecurityProviderTracker sp = new SecurityProviderTracker(runtime.context, def);
		sp.open();
		return sp;
	}
}
