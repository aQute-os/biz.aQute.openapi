package biz.aQute.openapi.provider;

import static org.junit.Assert.assertEquals;

import java.net.URI;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;

import aQute.lib.converter.Converter;
import aQute.openapi.oauth2.provider.OAuth2Authentication;
import aQute.openapi.provider.OpenAPIRuntime;
import aQute.openapi.provider.OpenAPIRuntime.Configuration;
import aQute.openapi.security.api.OpenAPISecurityProvider;
import aQute.openapi.util.WWWUtils;
import aQute.www.http.util.HttpRequest;
import biz.aQute.openapi.runtime.DummyFramework;
import biz.aQute.openapi.runtime.OpenAPIServerTestRule;
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
		properties.put("name", "oauth2");
		properties.put("type", "oauth2");

		OAuth2Authentication oauth2 = new OAuth2Authentication();

		ServiceRegistration<OpenAPISecurityProvider> oauth2reg = fw.context
				.registerService(OpenAPISecurityProvider.class, oauth2, properties);

		URI resolve = runtime.uri.resolve("/oauth2/.security/oauth2/oauth2/login");

		HttpRequest req = HttpRequest.get(resolve.toURL());
		assertEquals( 302, req.code());
		URI location = new URI(req.header("Location"));
		Map<String,String[]> map = WWWUtils.parameters(location);
		System.out.println(location);
		System.out.println(map);

		assertEquals( "code", map.get("response_type")[0]);
		String state = map.get("state")[0];
		URI callback = new URI(map.get("redirect_uri")[0] + "?code=4/agFEUr24d6UzMRQ9icmzc5A2ire6Xf2sdzhPQcPEDwI&authuser=0&session_state=9b88fa8da5e809d511a24ea33e24922896bedc9c..c738&prompt=consent&state="+state);

		HttpRequest reqw = HttpRequest.get(callback.toURL());
		assertEquals( 302, reqw.code());
		oauth2reg.unregister();

	}

	protected Configuration getConfig() throws Exception {
		return Converter.cnv(OpenAPIRuntime.Configuration.class, new HashMap<String,Object>());
	}

}
