package aQute.openapi.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import aQute.lib.strings.Strings;

public abstract class SourceRoute {
	final Map<String,SourceRoute>	segments	= new HashMap<>();

	final static Pattern			PARAMETER_P	= Pattern.compile("\\{(?<name>.+)\\}");

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

		String[] parameterNames;

		public ParameterRoute(String[] parameterNames) {
			this.parameterNames = parameterNames;
		}

		@Override
		public void generate(OpenAPIGenerator gen, Formatter f, String indent) {

			f.format("%sif ( index + %s == segments.length ) {\n", indent, parameterNames.length);
			for (int i = 0; i < parameterNames.length; i++) {
				f.format("%s  context.pathParameter(\"%s\",segments[index++]);\n", indent, parameterNames[i]);
			}
			super.generate(gen, f, indent);
			f.format("\n%s}", indent);
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			String del = "/";
			for (String segment : parameterNames) {
				sb.append(del).append("{").append(segment).append("}");
			}
			for (SourceRoute s : segments.values()) {
				OperationRoute or = (OperationRoute) s;
				sb.append(" ").append(or.method);
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
	 */
	void add(String path, String method, SourceMethod operation) {
		String segments[] = path.substring(1).split("/");
		SourceRoute rover = this;
		for (int n = 0; n < segments.length; n++) {
			String segment = segments[n];

			Matcher m = PARAMETER_P.matcher(segment);
			if (!m.matches()) {
				// normal segment
				rover = rover.segments.computeIfAbsent(segment, SegmentRoute::new);
			} else {

				// all following segments MUST now be
				// parameters

				int arity = segments.length - n;
				String[] parameterNames = new String[arity];
				parameterNames[0] = m.group("name");
				int p = 1;
				n++;
				for (; n < segments.length; n++) {
					m = PARAMETER_P.matcher(segments[n]);
					if (!m.matches()) {
						throw new IllegalArgumentException(
								"After a path parameters is used, all subsequent segments must also be path parameters. I.e. /a/b/{foo}/d is very wrong");
					}
					parameterNames[p++] = m.group("name");
				}

				String key = "__" + arity;

				ParameterRoute candidate = (ParameterRoute) rover.segments.get(key);

				if (candidate == null) {
					candidate = new ParameterRoute(parameterNames);
					rover.segments.put(key, candidate);
				} else {
					if (!Arrays.deepEquals(parameterNames, candidate.parameterNames)) {
						throw new IllegalArgumentException(String.format(
								"There are two or more operations with same prefix path %s and the same number of parameters. This is not valid: previous=%s, current=%s",
								path, Arrays.toString(candidate.parameterNames), Arrays.toString(parameterNames)));
					}
				}

				rover = candidate;
				assert n == segments.length;

			}
		}
		OperationRoute or = new OperationRoute(method, operation);
		rover.segments.put("$$" + method + "$$", or);
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
					f.format(" else ", indent);
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
