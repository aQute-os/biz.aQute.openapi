package aQute.openapi.provider.cors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.openapi.provider.CORS;
import aQute.openapi.provider.cors.CorsComponent.Configuration;

@Designate(ocd = Configuration.class, factory = false)
@Component
public class CorsComponent implements CORS {
	final static Logger	logger	= LoggerFactory.getLogger(CorsComponent.class);

	CORS				cors;

	@ObjectClassDefinition
	public @interface Configuration {

		@AttributeDefinition(description = "Allowed origin. Either a case sensitive match to the Origin header or *")
		String[] origins() default {
				"*"
		};

		@AttributeDefinition(description = "Enable access control")
		boolean enabled() default true;

		@AttributeDefinition(description = "Max Cache Age")
		int maxAge() default 86400;

		@AttributeDefinition(description = "Allow headers")
		String[] allowedHeaders() default {
				"*"
		};

		@AttributeDefinition(description = "Expose headers")
		String[] exposeHeaders() default {};

		@AttributeDefinition(description = "Credentials")
		boolean credentials() default true;

	}

	@Activate
	void activate(Configuration c) {
		if (c != null && c.enabled()) {
			cors = new CORSImplementation(logger, c.origins(), c.exposeHeaders(), c.allowedHeaders(), c.credentials(),
					c.maxAge());
		} else {
			cors = new CORS() {

				@Override
				public boolean fixup(HttpServletRequest request, HttpServletResponse response) throws Exception {
					return false;
				}

				@Override
				public boolean doOptions(HttpServletRequest request, HttpServletResponse response, String... methods)
						throws Exception {
					return false;
				}

			};
		}
	}

	@Override
	public boolean fixup(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return cors.fixup(request, response);
	}

	@Override
	public boolean doOptions(HttpServletRequest request, HttpServletResponse response, String... methods)
			throws Exception {
		return cors.doOptions(request, response, methods);
	}

}
