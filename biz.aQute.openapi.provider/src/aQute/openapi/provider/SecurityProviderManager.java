package aQute.openapi.provider;

import static aQute.openapi.security.api.OpenAPIAuthenticator.NAME;
import static aQute.openapi.security.api.OpenAPIAuthenticator.TYPE;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.json.codec.JSONCodec;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.security.api.OpenAPISecurityProviderInfo;

@Component(service = {
		Servlet.class, SecurityProviderManager.class
}, property = HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "="
		+ SecurityProviderManager.PATTERN, configurationPid = SecurityProviderManager.PID)
public class SecurityProviderManager extends HttpServlet {
	public static final String				PID					= "aQute.openapi.security.manager";
	public static final String				PATTERN				= "/.openapi/security/*";
	final static Logger						logger				= LoggerFactory
			.getLogger(SecurityProviderManager.class);
	final static JSONCodec					json				= new JSONCodec();
	private static final long				serialVersionUID	= 1L;
	final Map<String,OpenAPIAuthenticator>	providers			= new ConcurrentHashMap<String,OpenAPIAuthenticator>();

	@Override
	public void service(HttpServletRequest request, HttpServletResponse response) throws IOException {

		try {
			String path = request.getPathInfo();

			if (path == null) {
				if (!request.getMethod().equalsIgnoreCase("GET")) {
					response.sendError(HttpServletResponse.SC_BAD_REQUEST);
					return;
				}
				List<OpenAPISecurityProviderInfo> result = new ArrayList<>();
				for (OpenAPIAuthenticator provider : providers.values()) {
					OpenAPISecurityProviderInfo info = provider.getInfo(request);
					if (info != null)
						result.add(info);
				}
				String answer = json.enc().put(result).toString();
				response.setContentType("application/json");
				response.getWriter().write(answer);
				response.getWriter().close();
				return;
			}

			String parts[] = path.split("/");

			if (parts.length == 4) {

				String providerId = parts[1];
				String providerType = parts[2];
				String command = parts[3];

				OpenAPIAuthenticator securityProvider = getSecurityProvider(providerId, providerType);
				if (securityProvider == null) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}

				URI redirect;
				switch (command) {
					case "login" :
						redirect = securityProvider.login(request, response);
						break;
					case "logout" :
						redirect = securityProvider.logout(request, response);
						break;

					default :
						redirect = securityProvider.other(command, request, response);
						break;
				}
				if (redirect != null) {
					response.sendRedirect(response.encodeRedirectURL(redirect.toString()));
				}
				// else should be handled by the securityProvider
			}
		} catch (Exception e) {
			logger.error("Unexpected", e);
		}
	}

	private OpenAPIAuthenticator getSecurityProvider(String providerId, String providerType) {
		String key = getKey(providerId, providerType);
		return providers.get(key);
	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
	void addSecurityProvider(Map<String,Object> properties, OpenAPIAuthenticator provider) {
		String key = getKey(properties.get(NAME), properties.get(TYPE));
		providers.put(key, provider);
	}

	void removeSecurityProvider(Map<String,Object> properties, OpenAPIAuthenticator provider) {
		String key = getKey(properties.get(NAME), properties.get(TYPE));
		providers.remove(key);
	}

	protected String getKey(Object name, Object type) {
		assert name != null;
		assert type != null;
		String key = name + ":" + type;
		return key;
	}

}
