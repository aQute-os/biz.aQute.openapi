package aQute.openapi.provider;

import java.io.Closeable;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.util.tracker.ServiceTracker;

import aQute.json.codec.JSONCodec;

@Component(service = OpenAPIRuntime.class)
public class OpenAPIRuntime {
	final static JSONCodec				codec		= new JSONCodec();
	static OpenAPIBase.Codec			deflt		= new CodecWrapper(codec);

	ServiceTracker<OpenAPIBase,Tracker>	tracker;
	BundleContext						context;
	final Map<String,Dispatcher>		dispatchers	= new ConcurrentHashMap<>();
	final ThreadLocal<OpenAPIContext>	contexts	= new ThreadLocal<>();

	class Tracker {
		OpenAPIBase	base;
		Dispatcher	dispatcher;

		Tracker(OpenAPIBase service) {
			base = service;
			dispatcher = dispatchers.computeIfAbsent(base.prefix, (key) -> create(key));
			dispatcher.add(this);
		}

		Dispatcher create(String prefix) {
			try {
				return new Dispatcher(OpenAPIRuntime.this, prefix);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		void close() {
			dispatcher.remove(this);
		}

	}

	@Activate
	void activate(BundleContext context) {
		this.context = context;
		tracker = new ServiceTracker<OpenAPIBase,Tracker>(context, OpenAPIBase.class, null) {
			@Override
			public Tracker addingService(ServiceReference<OpenAPIBase> reference) {
				OpenAPIBase service = context.getService(reference);
				// might have been withdrawn
				if (service == null)
					return null;

				return add(service);
			}

			@Override
			public void removedService(ServiceReference<OpenAPIBase> reference, Tracker service) {
				service.close();
				context.ungetService(reference);
			}
		};
		tracker.open();
	}

	@Deactivate
	void deactivate() {
		this.tracker.close();
		this.dispatchers.values().forEach(d -> d.close());
	}

	public Tracker add(OpenAPIBase service) {
		return new Tracker(service);
	}

	/**
	 * Used for testing purposes since servlets are registered using the
	 * whiteboard approach. However, in the test we want to more closely control
	 * it.
	 *
	 * @param alias
	 * @param servlet
	 * @return a closeable
	 */
	public Closeable registerServlet(String alias, Servlet servlet)
			throws ServletException, NamespaceException {
		Hashtable<String,Object> p = new Hashtable<>();
		p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, alias + "/*");
		ServiceRegistration<Servlet> registration = context.registerService(Servlet.class, servlet, p);
		return () -> registration.unregister();
	}

}
