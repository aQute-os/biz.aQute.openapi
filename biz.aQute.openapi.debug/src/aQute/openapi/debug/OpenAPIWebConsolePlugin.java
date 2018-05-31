package aQute.openapi.debug;

import java.io.IOException;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.HttpService;

@Component(name = "biz.aQute.openapi.webconsole", service = Servlet.class, property = "felix.webconsole.label="
		+ OpenAPIWebConsolePlugin.PLUGIN)
public class OpenAPIWebConsolePlugin extends org.apache.felix.webconsole.AbstractWebConsolePlugin {
	private static final long	serialVersionUID	= 1L;
	final static String			PLUGIN				= "openapi";

	@Reference
	HttpService					http;

	@Override
	@Activate
	public void activate(BundleContext context) {
		super.activate(context);
	}

	@Override
	public void deactivate() {
		super.deactivate();
	}

	@Override
	public String getLabel() {
		return PLUGIN;
	}

	@Override
	public String getTitle() {
		return "OpenAPI";
	}

	@Override
	protected void renderContent(HttpServletRequest rq, HttpServletResponse rsp) throws ServletException, IOException {
		rsp.getWriter().println("Hello World");
	}

	public URL getResource(String resource) {
		if (resource.equals("/" + PLUGIN))
			return null;

		resource = resource.replaceAll("/" + PLUGIN + "/", "");
		return getClass().getResource(resource);
	}

}
