package biz.aQute.openapi.security.examples;

import org.junit.Rule;

import biz.aQute.openapi.runtime.DummyFramework;
import biz.aQute.openapi.runtime.OpenAPIServerTestRule;
import gen.basicauth.BasicauthBase;

public class BasicAuthExample {
	@Rule
	public OpenAPIServerTestRule	runtime	= new OpenAPIServerTestRule();
	DummyFramework			fw		= new DummyFramework();

	static class Impl extends BasicauthBase {

		@Override
		protected void authenticated() throws Exception {
			System.out.println("Authenticated");
		}

		@Override
		protected void unauthenticated() throws Exception {
			System.out.println("Unauthenticated");
		}

	}

	public void testBasicAuthenticated() {

	}
}
