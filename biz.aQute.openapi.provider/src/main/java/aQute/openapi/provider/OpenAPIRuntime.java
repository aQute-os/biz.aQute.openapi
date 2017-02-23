package aQute.openapi.provider;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import aQute.json.util.JSONCodec;
import aQute.openapi.security.apikey.api.APIKeyProvider;
import aQute.openapi.security.basic.api.BasicProvider;
import aQute.openapi.security.oauth2.api.OAuth2Provider;

@Component
public class OpenAPIRuntime {
	final static JSONCodec					codec		= new JSONCodec();

	@Reference
	HttpService								http;

	@Reference(cardinality=ReferenceCardinality.OPTIONAL)
	volatile OAuth2Provider					oath2;

	@Reference(cardinality=ReferenceCardinality.OPTIONAL)
	volatile APIKeyProvider					apiKey;

	@Reference(cardinality=ReferenceCardinality.OPTIONAL)
	volatile BasicProvider					basic;

	ServiceTracker<OpenAPIBase, Tracker>	tracker;
	BundleContext							context;
	final Map<String, Dispatcher>			dispatchers	= new ConcurrentHashMap<>();
	final ThreadLocal<OpenAPIContext>		contexts	= new ThreadLocal<>();

	class Tracker {
		private OpenAPIBase	base;
		private Dispatcher	dispatcher;

		Tracker(OpenAPIBase service) {
			base = service;
			dispatcher = dispatchers.computeIfAbsent(base.prefix,
					(key) -> create(key));
			dispatcher.add(base);
		}

		Dispatcher create(String prefix) {
			try {
				return new Dispatcher(OpenAPIRuntime.this, prefix);
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		void close() {
			dispatcher.remove(base);
		}

	}

	@Activate
	void activate(BundleContext context) {
		this.context = context;
		tracker = new ServiceTracker<OpenAPIBase, Tracker>(context,
				OpenAPIBase.class, null) {
			@Override
			public Tracker addingService(
					ServiceReference<OpenAPIBase> reference) {
				OpenAPIBase service = context.getService(reference);
				// might have been withdrawn
				if ( service == null)
					return null;
				
				return add(service);
			}

			@Override
			public void removedService(ServiceReference<OpenAPIBase> reference,
					Tracker service) {
				service.close();
				context.ungetService(reference);
			}
		};
		tracker.open();
	}

	@Deactivate
	void deactivate() {
		this.tracker.close();
	}

	public Tracker add(OpenAPIBase service) {
		return new Tracker(service);
	}

}
