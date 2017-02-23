package aQute.openapi.generator;

import aQute.openapi.v2.api.ParameterObject;

public class SourceArgument {

	final String			name;
	final ParameterObject	par;
	final SourceType		type;

	public SourceArgument(SourceMethod method, ParameterObject par) {
		this.par = par;
		this.name = method.toParameterName(par.name);

		type = par.schema != null ? method.parent.gen.getSourceType(par.schema)
				: method.parent.gen.getSourceType(par);
		if (type == null) {
			throw new IllegalArgumentException("No type for " + par.schema);
		}
	}


	private String convert(String access) {
		String s = type.conversion(access, par.collectionFormat);
		if (s == null)
			return access;
		else
			return "context." + s;
	}

	public String access() {
		switch (par.in) {
		case body:
			return String.format("context.body(%s.class)", type.reference());

		case formData:
			if ("file".equals(par.type))
				return String.format("context.part(\"%s\")", par.name);
			else {
				return doArray();
			}

		case header:
			return convert(String.format("context.header(\"%s\")", par.name));

		case path:
			return convert(String.format("context.path(\"%s\")", par.name));

		case query:
			return doArray();

		default:
			throw new UnsupportedOperationException(
					"No such in type: " + par.in);
		}
	}

	private String doArray() {
		if (type.isArray()) {
			switch (par.collectionFormat) {
			case csv:
				return convert(String.format(
						"context.csv(context.parameter(\"%s\"))", par.name));

			case pipes:
				return convert(String.format(
						"context.pipes(context.parameter(\"%s\"))", par.name));

			case ssv:
				return convert(String.format(
						"context.ssv(context.parameter(\"%s\"))", par.name));

			case tsv:
				return convert(String.format(
						"context.tsv(context.parameter(\"%s\"))", par.name));

			default:
			case multi:
			case none:
				return convert(
						String.format("context.parameters(\"%s\")", par.name));
			}
		} else
			return convert(
					String.format("context.parameter(\"%s\")", par.name));
	}

	@Override
	public String toString() {
		return "Argument [par=" + par.name + ", type=" + type.reference() + "]";
	}

	public ParameterObject getParameterObject() {
		return par;
	}

}
