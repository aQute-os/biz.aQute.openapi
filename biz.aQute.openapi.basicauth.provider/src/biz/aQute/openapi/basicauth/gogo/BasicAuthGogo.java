package biz.aQute.openapi.basicauth.gogo;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;

import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;
import biz.aQute.openapi.basicauth.provider.BasicAuthenticationProvider;
import osgi.enroute.debug.api.Debug;

@Component(property = { Debug.COMMAND_SCOPE + "=basic", //
		Debug.COMMAND_FUNCTION + "=passwd", //
}, service = BasicAuthGogo.class)
public class BasicAuthGogo {

	private static final String	BASIC	= "basic";

	@Reference
	OpenAPISecurityEnvironment	security;

	BundleContext				context;

	@Activate
	void activate(BundleContext context) {
		this.context = context;
	}

	public String passwd(
			@Parameter(absentValue = BASIC, names = { "-p", "--provider" }) String providerName,
			@Parameter(absentValue = "", names = { "-i", "--id" }) String userId,
			String identifier,
			String password) throws InvalidSyntaxException, NoSuchAlgorithmException {
		BasicAuthenticationProvider provider = getProvider(providerName);
		return provider.setPassword(userId, identifier, password);
	}

	public String passwd(@Parameter(absentValue = BASIC, names = { "-p", "--provider" }) String providerName,
			String user) throws InvalidSyntaxException {
		BasicAuthenticationProvider provider = getProvider(providerName);
		return provider.unsetPassword(user);
	}

	BasicAuthenticationProvider getProvider(String name) throws InvalidSyntaxException {
		Collection<ServiceReference<BasicAuthenticationProvider>> refs = context
				.getServiceReferences(BasicAuthenticationProvider.class, OpenAPIAuthenticator.filter(name, BASIC));
		if (refs.isEmpty())
			throw new IllegalArgumentException("No such provider " + name);

		if (refs.size() > 1)
			throw new IllegalArgumentException("Multiple providers " + name + " " + refs);

		ServiceReference<BasicAuthenticationProvider> ref = refs.iterator().next();
		return context.getService(ref);
	}

}
