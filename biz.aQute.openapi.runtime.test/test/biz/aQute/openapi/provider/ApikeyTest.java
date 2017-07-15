package biz.aQute.openapi.provider;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Rule;
import org.junit.Test;
import org.osgi.framework.ServiceRegistration;

import aQute.bnd.service.url.TaggedData;
import aQute.lib.converter.Converter;
import aQute.openapi.provider.OpenAPIBase;
import aQute.openapi.provider.OpenAPIRuntime;
import aQute.openapi.provider.OpenAPIRuntime.Configuration;
import aQute.openapi.security.api.Authentication;
import aQute.openapi.security.api.OpenAPISecurityDefinition;
import aQute.openapi.security.api.OpenAPISecurityProvider;
import biz.aQute.openapi.runtime.DummyFramework;
import biz.aQute.openapi.runtime.OpenAPIServerTestRule;
import gen.apikey.ApikeyBase;

public class ApikeyTest {

	@Rule
	public OpenAPIServerTestRule	runtime	= new OpenAPIServerTestRule();
	DummyFramework			fw		= new DummyFramework();


	@Test
	public void testAccess() throws Exception {

		runtime.runtime.activate(fw.context, geConfig());

		try {
			class X extends ApikeyBase {

				@Override
				protected void overrideNoSecurity() throws Exception {
				}

				@Override
				protected void orAndSecurity() throws Exception {
				}

				@Override
				protected void andSecurity() throws Exception {
				}

				@Override
				protected void defaultSecurity() throws Exception {
				}

				@Override
				protected void orSecurity() throws Exception {
				}
			}

			runtime.add(new X());

			TaggedData go = runtime.http.build().get().asTag().go(runtime.uri.resolve("/v1/overrideNoSecurity"));
			assertEquals(200, go.getResponseCode());

			go = runtime.http.build().get().asTag().headers("Key","ok").go(runtime.uri.resolve("/v1/defaultSecurity")); // api key, not installed
			assertEquals(403, go.getResponseCode());

			OpenAPISecurityProvider apiKey = new OpenAPISecurityProvider() {

				@Override
				public Authentication authenticate(HttpServletRequest request, HttpServletResponse response, OpenAPISecurityDefinition dto) {
					return new Authentication() {

						@Override
						public void requestCredentials() throws IOException {
							System.out.println("requestCredentials");
							throw new OpenAPIBase.Response(401, null);
						}

						@Override
						public boolean needsCredentials() {
							String key = request.getHeader("Key");
							System.out.println("needsCredentials");
							return null == key;
						}

						@Override
						public boolean isAuthenticated() {
							String key = request.getHeader("Key");

							System.out.println("isAuthenticated " + key);
							return "ok".equals(key);
						}

						@Override
						public boolean ignore() {
							System.out.println("ignore");
							return false;
						}

						@Override
						public String getUser() {
							return null;
						}
					};
				}

			};

			Hashtable<String, Object> properties = new Hashtable<>();
			properties.put("name", "ApiKey");
			properties.put("type", "apiKey");

			ServiceRegistration<OpenAPISecurityProvider> apiKeyReg = fw.context
					.registerService(OpenAPISecurityProvider.class, apiKey, properties);

			System.out.println(Arrays.toString(apiKeyReg.getReference().getPropertyKeys()) + " " + properties);
			go = runtime.http.build().get().asTag().headers("Key", "ok").go(runtime.uri.resolve("/v1/defaultSecurity"));
			assertEquals(200, go.getResponseCode());

			System.out.println(Arrays.toString(apiKeyReg.getReference().getPropertyKeys()) + " " + properties);
			go = runtime.http.build().get().asTag().headers("Key", "not ok").go(runtime.uri.resolve("/v1/defaultSecurity"));
			assertEquals(403, go.getResponseCode());

			System.out.println(Arrays.toString(apiKeyReg.getReference().getPropertyKeys()) + " " + properties);

			go = runtime.http.build().get().asTag().go(runtime.uri.resolve("/v1/defaultSecurity"));
			assertEquals(401, go.getResponseCode());

			go = runtime.http.build().get().asTag().go(runtime.uri.resolve("/v1/orSecurity"));
			assertEquals(200, go.getResponseCode());

			go = runtime.http.build().get().asTag().headers("Key", "ok").go(runtime.uri.resolve("/v1/orSecurity"));
			assertEquals(200, go.getResponseCode());

			go = runtime.http.build().get().asTag().headers("Key", "ok").go(runtime.uri.resolve("/v1/andSecurity"));
			assertEquals(403, go.getResponseCode());

			go = runtime.http.build().get().asTag().headers("Key", "ok").go(runtime.uri.resolve("/v1/orSecurity"));
			assertEquals(200, go.getResponseCode());

			apiKeyReg.unregister();

			go = runtime.http.build().get().asTag().headers("Key", "ok").go(runtime.uri.resolve("/v1/orSecurity"));
			assertEquals(403, go.getResponseCode());

		} finally {
			runtime.runtime.deactivate();
		}
	}


	protected Configuration geConfig() throws Exception {
		return Converter.cnv(OpenAPIRuntime.Configuration.class, new HashMap<String,Object>());
	}

	// @Test
	// public void testLogin() throws Exception {
	// AtomicBoolean ok = new AtomicBoolean(false);
	//
	// class X extends ApikeyBase {
	//
	// @Override
	// protected Response use(String header) throws Exception {
	// return null;
	// }
	//
	// @Override
	// protected Response login(Optional<String> header) throws Exception,
	// UnauthorizedResponse {
	// ok.set(true);
	// return null;
	// }
	//
	// }
	//
	// rule.add(new X());
	//
	// rule.runtime.security.add(new OpenAPISecurityProvider() {
	//
	// @Override
	// public boolean verify(OpenAPIContext context, OpenAPISecurityDefinition
	// dto, String... scopes) {
	// // TODO Auto-generated method stub
	// return false;
	// }
	//
	// @Override
	// public void forceAuthentication(OpenAPIContext context,
	// OpenAPISecurityDefinition dto, String... scopes) {
	// // TODO Auto-generated method stub
	//
	// }
	// });
	// TaggedData getUse = rule.http.build().get().asTag().headers("Key",
	// "ok").go(rule.uri.resolve("/v1/operation"));
	// assertEquals(403, getUse.getResponseCode());
	//
	// TaggedData putLogin =
	// rule.http.build().put().asTag().go(rule.uri.resolve("/v1/operation"));
	// assertEquals(200, putLogin.getResponseCode());
	//
	// getUse = rule.http.build().get().asTag().headers("Key",
	// "ok").go(rule.uri.resolve("/v1/operation"));
	// assertEquals(200, getUse.getResponseCode());
	// }
	//
}
