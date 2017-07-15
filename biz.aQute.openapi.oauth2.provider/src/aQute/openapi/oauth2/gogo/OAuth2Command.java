package aQute.openapi.oauth2.gogo;

import java.util.Collection;

import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.oauth2.provider.OAuth2Authentication;
import aQute.openapi.user.api.OpenAPISecurity;
import osgi.enroute.debug.api.Debug;

@Component(property = { Debug.COMMAND_SCOPE + "=oauth2", //
		Debug.COMMAND_FUNCTION + "=oauth2", //
}, service = OAuth2Command.class)
public class OAuth2Command {
	private BundleContext context;

	@Reference
	OpenAPISecurity security;

	@Activate
	void activate( BundleContext context) {
		this.context = context;
	}
	public String oauth2(@Parameter(absentValue = "oauth2", names = { "-p", "--provider" }) String providerName,
			String user, String email) throws InvalidSyntaxException {
		OAuth2Authentication provider = getProvider(providerName);
		provider.setEmail( user, email);
		return null;
	}

	public String oauth2(@Parameter(absentValue = "oauth2", names = { "-p", "--provider" }) String providerName,
			String user) throws InvalidSyntaxException {
		OAuth2Authentication provider = getProvider(providerName);
		provider.setEmail( user, null);
		return null;
	}

	OAuth2Authentication getProvider(String name) throws InvalidSyntaxException {
		Collection<ServiceReference<OAuth2Authentication>> refs = context.getServiceReferences(OAuth2Authentication.class, "(&(name="+name+")(type=oauth2))");
		if ( refs.isEmpty())
			throw new IllegalArgumentException("No such provider " + name);

		if ( refs.size() > 1)
			throw new IllegalArgumentException("Multiple providers " + name + " " + refs);

		ServiceReference<OAuth2Authentication> ref = refs.iterator().next();
		return context.getService(ref);
	}

}
