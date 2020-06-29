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
import aQute.openapi.provider.resources.ResourceDomain;
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
	Configuration						configuration;

	@Reference
	CORS								cors;

	@Reference
	OpenAPISecurityEnvironment			security;
	@Reference
	ResourceDomain						resources;

	@ObjectClassDefinition
	public @interface Configuration {
		@AttributeDefinition(description = "Register these prefixes ahead of time so that they can handle timeouts and errors")
		String[] registerOnStart() default {};

		@AttributeDefinition(description = "Delay and try again until found timeout")
		int delayOnNotFoundInSecs() default 30;

		@AttributeDefinition(description = "Cache-Control values. If no headers should be set make it empty. Default is no caching")
		String[] cacheControl() default {
				" no-cache", "no-store", "must-revalidate"
		};

		/**
		 * https://docs.osgi.org/javadoc/osgi.enterprise/7.0.0/org/osgi/service/http/whiteboard/HttpWhiteboardConstants.html#HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED
		 */
		@AttributeDefinition(description = "Enable multi part file uploads")
		boolean mp_enabled() default false;

		/**
		 * https://docs.osgi.org/javadoc/osgi.enterprise/7.0.0/org/osgi/service/http/whiteboard/HttpWhiteboardConstants.html#HTTP_WHITEBOARD_SERVLET_MULTIPART_FILESIZETHRESHOLD
		 */
		@AttributeDefinition(description = "Size threshold after which the file will be written to disk")
		long mp_fileSizeThreshold() default -1;

		/**
		 * https://docs.osgi.org/javadoc/osgi.enterprise/7.0.0/org/osgi/service/http/whiteboard/HttpWhiteboardConstants.html#HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION
		 */
		@AttributeDefinition(description = "Location where the files can be stored on disk")
		String mp_location() default "";

		/**
		 * https://docs.osgi.org/javadoc/osgi.enterprise/7.0.0/org/osgi/service/http/whiteboard/HttpWhiteboardConstants.html#HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE
		 */
		@AttributeDefinition(description = "Maximum size of a file being uploaded")
		long mp_maxFileSize() default -1;

		/**
		 * https://docs.osgi.org/javadoc/osgi.enterprise/7.0.0/org/osgi/service/http/whiteboard/HttpWhiteboardConstants.html#HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXREQUESTSIZE
		 */
		@AttributeDefinition(description = "Maximum request size")
		long mp_maxRequestSize() default -1;
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
	public void activate(BundleContext context, Configuration configuration)
			throws ServletException, NamespaceException {
		this.configuration = configuration;

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
	public void deactivate() throws IOException {
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
		logger.info("Registering servlet {}", alias);
		Hashtable<String,Object> p = new Hashtable<>();
		p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, alias + "/*");

		if (configuration.mp_enabled()) {
			p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_ENABLED, configuration.mp_enabled());

			if (configuration.mp_location() != null && !configuration.mp_location().isEmpty())
				p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_LOCATION, configuration.mp_location());

			if (configuration.mp_fileSizeThreshold() > 0)
				p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_FILESIZETHRESHOLD,
						configuration.mp_fileSizeThreshold());

			if (configuration.mp_maxFileSize() > 0)
				p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXFILESIZE,
						configuration.mp_maxFileSize());

			if (configuration.mp_maxRequestSize() > 0)
				p.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_MULTIPART_MAXREQUESTSIZE,
						configuration.mp_maxRequestSize());
		}

		ServiceRegistration<Servlet> registration = context.registerService(Servlet.class, servlet, p);
		return () -> {
			logger.info("Unregistering servlet {}", alias);
			registration.unregister();
		};
	}
}
