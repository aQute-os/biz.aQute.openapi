package aQute.openapi.generator;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aQute.lib.strings.Strings;

public abstract class SourceRoute {
	private static final String		PARAMETER_KEY	= "";

	final Map<String,SourceRoute>	segments		= new TreeMap<>();

	final static Pattern			PARAMETER_P		= Pattern.compile("\\{(?<name>.+)\\}");

	public static class RootSourceRoute extends SourceRoute {

	}

	static class SegmentRoute extends SourceRoute {

		String segment;

		public SegmentRoute(String segment) {
			this.segment = segment;
		}

		@Override
		public void generate(OpenAPIGenerator gen, Formatter f, String indent) {
			if (segment != null) {
				f.format("%sif( index < segments.length && \"%s\".equals(segments[index])) {\n", indent, segment);
				f.format("%s  index++;\n", indent);
			}
			super.generate(gen, f, indent);
			if (segment != null) {
				f.format("%s  // end %s\n", indent, segment);
				f.format("%s} ", indent);
			}
		}

		@Override
		public String toString() {
			return "S[" + segment + segments + "]";
		}
	}

	static class ParameterRoute extends SourceRoute {

		Set<String> parameterNames = new HashSet<>();

		@Override
		public void generate(OpenAPIGenerator gen, Formatter f, String indent) {

			f.format("%s  if ( index < segments.length ) {\n", indent);

			for (String parameterName : parameterNames) {
				f.format("%s  context.pathParameter(\"%s\",segments[index]);\n", indent, parameterName);
			}
			f.format("%s  index++;\n", indent);

			super.generate(gen, f, indent);
			f.format("\n%s}", indent);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("/").append("{");
			String del = "";
			for (String s : parameterNames) {
				sb.append(del).append(s);
				del = "|";
			}
			sb.append("}");

			if (segments.size() > 1) {
				sb.append("[ ");
			}
			del = "";
			for (SourceRoute s : segments.values()) {
				sb.append(del).append(s);
				del = ",";
			}
			if (segments.size() > 1) {
				sb.append(" ]");
			}
			return sb.toString();
		}
	}

	static class OperationRoute extends SourceRoute {
		SourceMethod	operation;
		String			method;

		public OperationRoute(String method, SourceMethod operation) {
			this.method = method;
			this.operation = operation;
		}

		@Override
		public void generate(OpenAPIGenerator gen, Formatter f, String indent) {
			f.format("%s  if ( context.isMethod(OpenAPIBase.Method.%s)) {\n", indent, method.toUpperCase());
			f.format("%s    %s_%s_(context);\n", indent, operation.name, method);
			f.format("%s    return true;\n", indent);
			f.format("%s  } ", indent);
		}

		@Override
		public String toString() {
			return method + ":" + operation;
		}
	}

	/**
	 * @param path
	 * @param method
	 * @param operation
	 * @return
	 */
	Set<String> add(String path, String method, SourceMethod operation) {
		Set<String> pathParameterNames = new HashSet<>();

		String segments[] = path.substring(1).split("/");
		SourceRoute rover = this;
		for (int n = 0; n < segments.length; n++) {
			String segment = segments[n];

			Matcher m = PARAMETER_P.matcher(segment);
			if (!m.matches()) {
				// normal segment
				rover = rover.segments.computeIfAbsent(segment, SegmentRoute::new);
			} else {

				ParameterRoute r = (ParameterRoute) rover.segments.computeIfAbsent(PARAMETER_KEY,
						ss -> new ParameterRoute());
				String name = m.group("name");
				r.parameterNames.add(name);
				pathParameterNames.add(name);
				rover = r;
			}
		}
		OperationRoute or = new OperationRoute(method, operation);
		rover.segments.put("$$" + method + "$$", or);
		return pathParameterNames;
	}

	/**
	 * segment(null, segment("accessToken", post(this::$2faccessToken),
	 * segment("refresh", post(this::$2faccessToken$2frefresh))));
	 * 
	 * @param gen
	 * @param f
	 */
	public void generate(OpenAPIGenerator gen, Formatter f, String indent) {

		List<String> methods = new ArrayList<>();

		boolean first = true;
		for (SourceRoute r : segments.values()) {
			if (r instanceof OperationRoute) {
				if (first) {
					first = false;
					f.format("%s  if ( segments.length == index) {\n", indent);
				} else {
					f.format("%s else ", indent);
				}
				methods.add(((OperationRoute) r).method);
				r.generate(gen, f, indent + "  ");
			}
		}
		if (!first) {

			//
			// Print an options header
			//

			if (methods.size() > 0) {
				f.format("\n%s    return getOpenAPIContext().doOptions(%s);\n", indent,
						"\"" + Strings.join("\", \"", methods).toUpperCase() + "\"");
			} else
				f.format("%s    return true;\n", indent);

			f.format("\n%s  }", indent);
		}

		for (SourceRoute r : segments.values()) {
			if (r instanceof SegmentRoute) {
				if (first) {
					first = false;
					f.format("\n");
				} else {
					f.format(" else ");
				}
				r.generate(gen, f, indent + "  ");
			}
		}

		for (SourceRoute r : segments.values()) {
			if (r instanceof ParameterRoute) {
				if (first) {
					first = false;
					f.format("\n");
				} else {
					f.format(" else ", indent);
				}
				r.generate(gen, f, indent + "  ");
			}
		}
		f.format("\n\n");
	}

}
