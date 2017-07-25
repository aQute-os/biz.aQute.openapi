package aQute.openapi.basicauth.example;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.provider.OpenAPIBase;
import osgi.enroute.authorization.api.Authority;
import osgi.enroute.configurer.api.RequireConfigurerExtender;

@RequireConfigurerExtender
@ProvideBasicAuthBase
@Component(service=OpenAPIBase.class)
public class BasicAuthExample extends BasicAuthBase {


	@Reference
	Authority authority;

	@Override
	protected boolean authenticated(String action) throws Exception {
		System.out.println("Authenticated " + action);
		return hasPermission(action);
	}


	@Override
	protected boolean unauthenticated(String action) throws Exception {
		boolean permission = hasPermission(action);
		System.out.println("Unauthenticated: Has permission " + permission);
		return permission;
	}

}