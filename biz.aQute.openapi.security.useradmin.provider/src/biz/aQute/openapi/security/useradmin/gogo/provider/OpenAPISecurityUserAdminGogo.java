package biz.aQute.openapi.security.useradmin.gogo.provider;

import static java.util.stream.Collectors.toMap;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.useradmin.User;

import aQute.openapi.security.api.OpenAPISecurityProvider;
import biz.aQute.openapi.security.useradmin.apikey.provider.APIKeyProvider;
import biz.aQute.openapi.security.useradmin.basicauth.provider.BasicAuthenticationProvider;
import biz.aQute.useradmin.util.UserAdminFacade;
import osgi.enroute.debug.api.Debug;

@Component(service = OpenAPISecurityUserAdminGogo.class, property = { //
		Debug.COMMAND_SCOPE + "=oasua", //
		Debug.COMMAND_FUNCTION + "=basicauth", //
		Debug.COMMAND_FUNCTION + "=apikey" //
})
public class OpenAPISecurityUserAdminGogo {

	Pattern			ROLE_NAME_P	= Pattern.compile("\\p{javaJavaIdentifierStart}[\\p{javaJavaIdentifierPart}\\-]*");

	@Reference
	UserAdminFacade	userAdmin;

	BundleContext	context;

	@Activate
	void activate(BundleContext context) {
		this.context = context;
	}

	/**
	 * <
	 *
	 * @param key
	 * @param id
	 * @param password
	 * @throws NoSuchAlgorithmException
	 */
	public String basicauth(
			@Descriptor("Name of the security provider") @Parameter(absentValue = "basicauth", names = { "-n", "--name" }) String name,
			@Descriptor("User Admin id (Needed for the first time)") @Parameter(absentValue = "", names = { "-u", "--user" }) String userAdminId,
			@Descriptor("Userid to use") String id,
			@Descriptor("Password") String password) throws Exception {

		OpenAPISecurityProvider bap = getSecurityProvider(name);
		if (bap == null)
			return "No such provider " + name;

		if (!(bap instanceof BasicAuthenticationProvider))
			return "Not a BasicAuthenticationProvider " + name + " but " + bap;

		BasicAuthenticationProvider b = (BasicAuthenticationProvider) bap;
		return b.setPassword(userAdminId, id, password);
	}

	public String basicauth(
			@Parameter(absentValue = "basicauth", names = { "-n", "--name" }) String name,
			String id) throws Exception {

		OpenAPISecurityProvider bap = getSecurityProvider(name);
		if (bap == null)
			return "No such provider " + name;

		if (!(bap instanceof BasicAuthenticationProvider))
			return "Not a BasicAuthenticationProvider " + name + " but " + bap;

		BasicAuthenticationProvider b = (BasicAuthenticationProvider) bap;
		return b.unsetPassword(id);
	}

	public Set<User> basicauth(
			@Parameter(absentValue = "basicauth", names = { "-n", "--name" }) String name) throws Exception {

		OpenAPISecurityProvider bap = getSecurityProvider(name);
		if (bap == null)
			throw new IllegalArgumentException("No such provider " + name);

		if (!(bap instanceof BasicAuthenticationProvider))
			throw new IllegalArgumentException( "Not a BasicAuthenticationProvider " + name + " but " + bap);

		BasicAuthenticationProvider b = (BasicAuthenticationProvider) bap;
		return b.list();
	}

	public String apikey(
			@Parameter(absentValue = "apikey", names = { "-n", "--name" }) String name,
			@Parameter(absentValue = "false", presentValue = "true", names = { "-r", "--remove" }) boolean remove,
			String id) throws Exception {

		OpenAPISecurityProvider ak = getSecurityProvider(name);
		if (ak == null)
			return "No such provider " + name;

		if (!(ak instanceof APIKeyProvider))
			return "Not a ApiKeyImpl " + name + " but " + ak;

		APIKeyProvider b = (APIKeyProvider) ak;
		if (remove)
			return b.removeAPIKey(id);
		else
			return b.addAPIKey(id);
	}

	public Map<String, OpenAPISecurityProvider> providers() throws InvalidSyntaxException {
		return context.getServiceReferences(OpenAPISecurityProvider.class, null)
				.stream()
				.collect(
						toMap(
								r -> (String) r.getProperty("name"),
								r -> context.getService(r)));
	}

	private OpenAPISecurityProvider getSecurityProvider(String name) throws InvalidSyntaxException {
		Collection<ServiceReference<OpenAPISecurityProvider>> refs = context
				.getServiceReferences(OpenAPISecurityProvider.class, "(name=" + name + ")");
		if (refs.isEmpty())
			return null;

		return context.getService(refs.iterator().next());
	}

}
