package aQute.openapi.generator;

import aQute.openapi.generator.SourceType.ArrayType;
import aQute.openapi.generator.SourceType.OptionalType;
import aQute.openapi.v2.api.ParameterObject;

public class SourceArgument {

	private final String			name;
	private final ParameterObject	par;
	private final SourceType		type;

	public SourceArgument(OpenAPIGenerator gen, SourceMethod method, ParameterObject par) {
		this.par = par;
		this.name = method.toParameterName(par.name);

		String contextName = gen.toTypeName(method.name + "_" + this.name);

		SourceType type = par.schema != null
				? method.getParent().getGen().getSourceType(par.schema, contextName /*
																					 * XXX
																					 */)
				: method.getParent().getGen().getSourceType(par, contextName);
		if (type == null) {
			throw new IllegalArgumentException("No type for " + par.schema);
		}

		if (!par.required) {
			type = new OptionalType(type);
		}

		this.type = type;
	}

	private String convert(SourceType type, String access) {
		String s = type.conversion(access, getPar().collectionFormat);
		if (s == null)
			return access;
		else if (s.startsWith("^")) {
			return s.substring(1);
		} else
			return "context." + s;
	}

	public String access() {
		return access(type);
	}

	public String access(SourceType type) {

		if (type instanceof OptionalType) {
			type = ((OptionalType) type).getTarget();
			return "context.optional(" + access(type) + ")";
		}

		switch (getPar().in) {
			case body :

				if (type.isArray()) {
					ArrayType arrayType = (ArrayType) type;
					return String.format("context.listBody(%s.class)", arrayType.getComponentType().reference());
				} else
					return String.format("context.body(%s.class)", type.reference());

			case formData :
				if ("file".equals(getPar().type))
					return String.format("context.part(\"%s\")", getPar().name);
				else {
					return doParameter(type, "formData", "formDataArray");
				}

			case header :
				return convert(type, String.format("context.header(\"%s\")", getPar().name));

			case path :
				return convert(type, String.format("context.path(\"%s\")", getPar().name));

			case query :
				return doParameter(type, "parameter", "parameters");

			default :
				throw new UnsupportedOperationException("No such in type: " + getPar().in);
		}
	}

	private String doParameter(SourceType type, String singleAccessFunction, String arrayAccessFunction) {
		if (getType().isArray()) {
			switch (getPar().collectionFormat) {
				case csv :
					return convert(type,
							String.format("context.csv(context.%s(\"%s\"))", arrayAccessFunction, getPar().name));

				case pipes :
					return convert(type,
							String.format("context.pipes(context.%s(\"%s\"))", arrayAccessFunction, getPar().name));

				case ssv :
					return convert(type,
							String.format("context.ssv(context.%s(\"%s\"))", arrayAccessFunction, getPar().name));

				case tsv :
					return convert(type,
							String.format("context.tsv(context.%s(\"%s\"))", arrayAccessFunction, getPar().name));

				default :
				case multi :
				case none :
					return convert(type, String.format("context.%s(\"%s\")", arrayAccessFunction, getPar().name));
			}
		} else
			return convert(type, String.format("context.%s(\"%s\")", singleAccessFunction, getPar().name));
	}

	@Override
	public String toString() {
		return "Argument [par=" + getPar().name + ", type=" + getType().reference() + "]";
	}

	public ParameterObject getParameterObject() {
		return getPar();
	}

	public String getName() {
		return name;
	}

	public SourceType getType() {
		return type;
	}

	public ParameterObject getPar() {
		return par;
	}

}
