package aQute.openapi.provider;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.openapi.security.api.OpenAPISecurityProvider;

public class SecurityProviderTracker extends ServiceTracker<OpenAPISecurityProvider,OpenAPISecurityProvider> {
	static Logger logger = LoggerFactory.getLogger(SecurityProviderTracker.class);

	public SecurityProviderTracker(BundleContext context, String id, String type) {
		super(context, filter(id, type), null);
	}

	private static Filter filter(String id, String type) {
		try {
			String filterString = String.format("(&(objectClass=%s)(name=%s)(type=%s))",
					OpenAPISecurityProvider.class.getName(), id, type);
			return FrameworkUtil.createFilter(filterString);
		} catch (InvalidSyntaxException e) {
			throw new RuntimeException(e);
		}
	}

}
