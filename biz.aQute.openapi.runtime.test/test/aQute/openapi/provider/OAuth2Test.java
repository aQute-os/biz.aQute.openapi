package aQute.openapi.provider;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;

import aQute.lib.converter.Converter;
import aQute.openapi.oauth2.provider.OAuth2AuthenticationProvider;
import aQute.openapi.oauth2.provider.OAuth2Configuration;
import aQute.openapi.provider.OpenAPIRuntime.Configuration;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import aQute.openapi.util.WWWUtils;
import aQute.www.http.util.HttpRequest;
import gen.oauth2.Oauth2Base;

@SuppressWarnings("restriction")
public class OAuth2Test {

	@Rule
	public OpenAPIServerTestRule	runtime	= new OpenAPIServerTestRule();
	DummyFramework					fw		= new DummyFramework();

	@Test
	public void testAccess() throws Exception {

		runtime.runtime.activate(fw.context, getConfig());

		class X extends Oauth2Base {

			@Override
			protected void authenticated() throws Exception {
				System.out.println("authenticated " + getOpenAPIContext().getUser());
			}

			@Override
			protected void unauthenticated() throws Exception {
				System.out.println("unauthenticated " + getOpenAPIContext().getUser());

			}

		}

		runtime.add(new X());

		Hashtable<String, Object> properties = new Hashtable<>();
		properties.put("openapi.name", "oauth2");
		properties.put("openapi.type", "oauth2");

		OAuth2AuthenticationProvider oauth2 = new OAuth2AuthenticationProvider();
		properties.put("authorizationEndpoint", "http://bndtools.com/authz");
		properties.put("tokenEndpoint", "http://bndtools.com/token");
		properties.put("finalEndpoint", "http://bndtools.com/final");
		properties.put("clientId", "clientId");

		oauth2.activate(Converter.cnv(OAuth2Configuration.class, properties));
		runtime.securityProviderManager.addSecurityProvider(properties, oauth2);

		ServiceRegistration<OpenAPIAuthenticator> oauth2reg = fw.context
				.registerService(OpenAPIAuthenticator.class, oauth2, properties);

		URI resolve = runtime.uri.resolve("/.openapi/security/oauth2/oauth2/login");

		HttpRequest req = HttpRequest.get(resolve.toURL()).followRedirects(false);
		assertEquals(302, req.code());
		URI location = new URI(req.header("Location"));
		Map<String, String[]> map = WWWUtils.parameters(location);
		System.out.println(location);
		System.out.println(map);

		assertThat(location.getHost(), is("bndtools.com"));
		assertThat(map.get("client_id")[0], is("clientId"));
		assertThat(map.get("redirect_uri")[0], startsWith( runtime.uri.toString()));
		assertThat("code", is(map.get("response_type")[0]));

		String state = map.get("state")[0];
		URI callback = new URI(map.get("redirect_uri")[0]
				+ "?code=4/agFEUr24d6UzMRQ9icmzc5A2ire6Xf2sdzhPQcPEDwI&authuser=0&session_state=9b88fa8da5e809d511a24ea33e24922896bedc9c..c738&prompt=consent&state="
				+ state);

		HttpRequest reqw = HttpRequest.get(callback.toURL()).followRedirects(false);
		assertEquals(302, reqw.code());
		location = new URI(reqw.header("Location"));
		assertThat(location.toString(), startsWith("http://bndtools.com/final?error"));
		oauth2reg.unregister();

	}

	protected Configuration getConfig() throws Exception {
		return Converter.cnv(OpenAPIRuntime.Configuration.class, new HashMap<String, Object>());
	}

}
