package aQute.openapi.provider;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.felix.service.command.Converter;
import org.apache.felix.service.command.Descriptor;
import org.apache.felix.service.command.Parameter;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.lib.io.IO;
import aQute.openapi.provider.OpenAPIRuntime.Tracker;
import aQute.openapi.security.api.OpenAPIAuthenticator;
import osgi.enroute.debug.api.Debug;

@Component(property = {
		Debug.COMMAND_SCOPE + "=openapi", Debug.COMMAND_FUNCTION + "=dispatcher",
		Debug.COMMAND_FUNCTION + "=dispatchers", Debug.COMMAND_FUNCTION + "=rest", Debug.COMMAND_FUNCTION + "=providers"
})
public class OpenAPIGogo implements Converter {
	@Reference
	OpenAPIRuntime			runtime;
	@Reference
	SecurityProviderManager	providers;

	@Descriptor("List the dispatchers that are currently active")
	public Set<String> dispatchers() {
		return runtime.dispatchers.keySet();
	}

	@Descriptor("Provide the details of a specific dispatcher")
	public Dispatcher dispatcher(@Descriptor("Name of the dispatcher") String name) {
		Dispatcher dispatcher = runtime.dispatchers.get(name);
		return dispatcher;
	}

	@Descriptor("Make a rest call")
	public Object rest(
			@Descriptor("User for basic authentication, userid:password") @Parameter(absentValue = "", names = {
					"-u", "--user"
			}) String userpassword, URL uri) throws IOException {
		HttpURLConnection urlConnection = (HttpURLConnection) uri.openConnection();
		if (!userpassword.isEmpty()) {
			String authorization = Base64.getEncoder().encodeToString(userpassword.getBytes(StandardCharsets.UTF_8));
			urlConnection.setRequestProperty("Authorization", "Basic " + authorization);
		}
		urlConnection.connect();

		try (Formatter f = new Formatter()) {
			f.format("%s %s\n", urlConnection.getRequestMethod(), urlConnection.getURL().getPath());

			for (Entry<String,List<String>> h : urlConnection.getHeaderFields().entrySet()) {
				String header = h.getKey();
				if (header == null)
					header = "";
				f.format("%-30s: %s\n", header, h.getValue().stream().collect(Collectors.joining()));
			}
			try {
				String content = IO.collect(urlConnection.getInputStream());
				f.format("\n<<<<<<<<<<<<<<<<<\n" + "%s\n>>>>>>>>>>>>>>>>>>\n" + "", content);
			} catch (Exception e) {
				f.format("No content %s", e.getMessage());
			}
			return f.toString();
		}
	}

	public Collection<OpenAPIAuthenticator> providers() {
		return providers.providers.values();
	}
	@Override
	public Object convert(Class< ? > arg0, Object arg1) throws Exception {
		return null;
	}

	@Override
	public CharSequence format(Object target, int level, Converter next) throws Exception {
		if (target instanceof Dispatcher)
			return formatDispatcher((Dispatcher) target, level);
		return null;
	}

	protected CharSequence formatDispatcher(Dispatcher dispatcher, int level) {

		switch (level) {
			case Converter.INSPECT :
				try (Formatter f = new Formatter();) {
					f.format("Base Path        %s\n", dispatcher.prefix);
					for (Tracker t : dispatcher.targets) {
						OpenAPIBase base = t.base;
						f.format("  %s\n", base.getClass().getSimpleName());
						for (String op : base.ops) {
							f.format("     %s\n", op);
						}
					}
					return f.toString();
				}
			case Converter.LINE :
				try (Formatter f = new Formatter();) {
					f.format("%-20s %s", dispatcher.prefix, dispatcher.targets);
					return f.toString();
				}
			default :
			case Converter.PART :
				try (Formatter f = new Formatter();) {
					f.format(dispatcher.prefix);
					return f.toString();
				}
		}
	}
}
