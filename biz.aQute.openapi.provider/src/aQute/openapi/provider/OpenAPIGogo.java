package aQute.openapi.provider;

import java.util.Formatter;
import java.util.Set;

import org.apache.felix.service.command.Converter;
import org.apache.felix.service.command.Descriptor;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import aQute.openapi.provider.OpenAPIRuntime.Tracker;
import osgi.enroute.debug.api.Debug;

@Component(property = {
		Debug.COMMAND_SCOPE + "=openapi", Debug.COMMAND_FUNCTION + "=dispatcher",
		Debug.COMMAND_FUNCTION + "=dispatchers"
})
public class OpenAPIGogo implements Converter {
	@Reference
	OpenAPIRuntime runtime;

	@Descriptor("List the dispatchers that are currently active")
	public Set<String> dispatchers() {
		return runtime.dispatchers.keySet();
	}

	@Descriptor("Provide the details of a specific dispatcher")
	public Dispatcher dispatcher(@Descriptor("Name of the dispatcher") String name) {
		Dispatcher dispatcher = runtime.dispatchers.get(name);
		return dispatcher;
	}

	@Override
	public Object convert(Class< ? > arg0, Object arg1) throws Exception {
		return null;
	}

	@Override
	public CharSequence format(Object target, int level, Converter next) throws Exception {
		if (!(target instanceof Dispatcher))
			return null;

		Dispatcher dispatcher = (Dispatcher) target;

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
			case Converter.PART :
				try (Formatter f = new Formatter();) {
					f.format(dispatcher.prefix);
					return f.toString();
				}
		}

		return null;
	}
}
