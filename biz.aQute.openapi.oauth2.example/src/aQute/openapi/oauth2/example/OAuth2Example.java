package aQute.openapi.oauth2.example;

import org.osgi.service.component.annotations.Component;

import aQute.openapi.provider.OpenAPIBase;
import osgi.enroute.configurer.api.RequireConfigurerExtender;
import osgi.enroute.google.angular.capabilities.RequireAngularWebResource;
import osgi.enroute.twitter.bootstrap.capabilities.RequireBootstrapWebResource;
import osgi.enroute.webserver.capabilities.RequireWebServerExtender;

@RequireAngularWebResource(resource={"angular.js","angular-resource.js", "angular-route.js"}, priority=1000)
@RequireBootstrapWebResource(resource="css/bootstrap.css")
@RequireWebServerExtender
@RequireConfigurerExtender
@ProvideOAuth2Base
@Component(name="biz.aQute.openapi.oauth2.example", service=OpenAPIBase.class)
public class OAuth2Example extends OAuth2Base {

	@Override
	protected boolean authenticated(String action) throws Exception {
		return hasPermission(action);
	}

	@Override
	protected boolean unauthenticated(String action) throws Exception {
		return hasPermission(action);
	}

}
