package aQute.openapi.oauth2.gogo;

import static aQute.openapi.oauth2.provider.OAuth2AuthenticationProvider.OAUTH2;

import java.util.Collection;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.oauth2.provider.OAuth2AuthenticationProvider;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.security.environment.api.OpenAPISecurityEnvironment;
import osgi.enroute.debug.api.Debug;

@Component(property = { Debug.COMMAND_SCOPE + "=oauth2", //
		Debug.COMMAND_FUNCTION + "=oauth2", //
}, service = OAuth2Command.class)
public class OAuth2Command {

	private BundleContext		context;

	@Reference
	OpenAPISecurityEnvironment	security;

	@Activate
	void activate(BundleContext context) {
		this.context = context;
	}

	//@formatter:off
	@Descriptor("Associate the user name with the user identity in the security environment \n"
			+ "for a given provider. For example,"
			+ " 'oauth2 google u123456 john.doe@example.com'\n")
	public String oauth2(

			@Descriptor("Provider name")
			String providerName,

			@Descriptor("User identity")
			String user,

			@Descriptor("User name")
			String email

	//@formatter:on
	) throws InvalidSyntaxException {
		OAuth2AuthenticationProvider provider = getProvider(providerName);
		provider.setEmail(user, email);
		return null;
	}

	//@formatter:off
	@Descriptor("Show the name associated with a given identity for an oauth2 provider\n"
	)
	public String oauth2(

			@Descriptor("Provider name")
			String providerName,

			@Descriptor("User identity")
			String user

			//@formatter:on
	) throws InvalidSyntaxException {
		OAuth2AuthenticationProvider provider = getProvider(providerName);
		provider.setEmail(user, null);
		return null;
	}

	OAuth2AuthenticationProvider getProvider(String name) throws InvalidSyntaxException {
		Collection<ServiceReference<OAuth2AuthenticationProvider>> refs = context
				.getServiceReferences(OAuth2AuthenticationProvider.class, OpenAPIAuthenticator.filter(name, OAUTH2));
		if (refs.isEmpty())
			throw new IllegalArgumentException("No such provider " + name);

		if (refs.size() > 1)
			throw new IllegalArgumentException("Multiple providers " + name + " " + refs);

		ServiceReference<OAuth2AuthenticationProvider> ref = refs.iterator().next();
		return context.getService(ref);
	}

}
