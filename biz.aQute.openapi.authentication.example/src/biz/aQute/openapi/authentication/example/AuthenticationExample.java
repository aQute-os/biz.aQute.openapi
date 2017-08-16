package biz.aQute.openapi.authentication.example;

import org.osgi.service.component.annotations.Component;

import aQute.openapi.authentication.generated.AuthenticationBase;
import aQute.openapi.authentication.generated.ProvideAuthenticationBase;
import aQute.openapi.provider.OpenAPIBase;

@ProvideAuthenticationBase
@Component(service = OpenAPIBase.class)
public class AuthenticationExample extends AuthenticationBase {

	public AuthenticationExample() {
		System.out.println("OpenAPI Authentication Tester");
	}

	@Override
	protected String google() throws Exception {
		System.out.println("Google authenticated");
		return getOpenAPIContext().getUser();
	}
	@Override
	protected String basic() throws Exception {
		System.out.println("Basic authenticated");
		return getOpenAPIContext().getUser();
	}

	@Override
	protected void unauthenticated() throws Exception {
		System.out.println("unauthenticated");
	}

}
