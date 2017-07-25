package aQute.openapi.provider;

import java.io.Closeable;
import java.io.IOException;
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
import org.osgi.service.component.annotations.Modified;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.NamespaceException;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.json.codec.JSONCodec;
import aQute.openapi.provider.OpenAPIRuntime.Configuration;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;

@Designate(ocd = Configuration.class, factory = false)
@Component(service = OpenAPIRuntime.class, immediate = true, configurationPid = "aQute.openapi.runtime")
public class OpenAPIRuntime {
	final static Logger					logger				= LoggerFactory.getLogger(OpenAPIRuntime.class);
	final static JSONCodec				codec				= new JSONCodec();
	static OpenAPIBase.Codec			deflt				= new CodecWrapper(codec);

	ServiceTracker<OpenAPIBase,Tracker>	tracker;
	BundleContext						context;
	final Map<String,Dispatcher>		dispatchers			= new ConcurrentHashMap<>();
	final ThreadLocal<OpenAPIContext>	contexts			= new ThreadLocal<>();
	int									delayOn404Timeout	= 30;

	@Reference
	OpenAPISecurityEnvironment						security;

	@ObjectClassDefinition
	public @interface Configuration {
		@AttributeDefinition(description = "Register these prefixes ahead of time so that they can handle timeouts and errors")
		String[] registerOnStart() default {};

		@AttributeDefinition(description = "Delay and try again until found timeout")
		int delayOnNotFoundInSecs() default 30;
	}

	class Tracker {
		OpenAPIBase	base;
		Dispatcher	dispatcher;

		Tracker(OpenAPIBase service) {
			base = service;
			dispatcher = getDispatcher(base.prefix);
			dispatcher.add(this);
		}

		void close() {
			dispatcher.remove(this);
		}

		@Override
		public String toString() {
			return base.toString();
		}
	}

	Dispatcher getDispatcher(String prefix) {
		return dispatchers.computeIfAbsent(prefix, this::create);
	}

	Dispatcher create(String prefix) {
		try {
			return new Dispatcher(OpenAPIRuntime.this, prefix);
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Activate
	public void activate(BundleContext context, Configuration configuration) {
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
		modified(configuration);
		tracker.open();
	}

	@Modified
	void modified(Configuration configuration) {
		if (configuration.registerOnStart() != null) {
			for (String uriPrefix : configuration.registerOnStart()) {
				getDispatcher(uriPrefix);
			}
		}
		delayOn404Timeout = configuration.delayOnNotFoundInSecs();
	}

	@Deactivate
	public void deactivate() {
		this.tracker.close();
		for (Dispatcher d : this.dispatchers.values()) {
			try {
				d.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
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
	public Closeable registerServlet(String alias, Servlet servlet) throws ServletException, NamespaceException {
		System.out.println("Registering servlet " + alias);
		Hashtable<String,Object> p = new Hashtable<>();
		p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, alias + "/*");
		ServiceRegistration<Servlet> registration = context.registerService(Servlet.class, servlet, p);
		return () -> {
			System.out.println("Unregistering servlet " + alias);
			registration.unregister();
		};
	}

}
