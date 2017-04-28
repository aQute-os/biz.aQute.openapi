package aQute.openapi.generator;

import aQute.openapi.generator.SourceType.ArrayType;
import aQute.openapi.v2.api.ParameterObject;

public class SourceArgument {

	private final String			name;
	private final ParameterObject	par;
	private final SourceType		type;

	public SourceArgument(SourceMethod method, ParameterObject par) {
		this.par = par;
		this.name = method.toParameterName(par.name);

		type = par.schema != null ? method.getParent().getGen().getSourceType(par.schema)
				: method.getParent().getGen().getSourceType(par);
		if (getType() == null) {
			throw new IllegalArgumentException("No type for " + par.schema);
		}
	}

	private String convert(String access) {
		String s = getType().conversion(access, getPar().collectionFormat);
		if (s == null)
			return access;
		else if (s.startsWith("^")) {
			return s.substring(1);
		} else
			return "context." + s;
	}

	public String access() {
		switch (getPar().in) {
			case body :
				if (getType().isArray()) {
					ArrayType arrayType = (ArrayType) getType();
					return String.format("context.listBody(%s.class)", arrayType.getComponentType().reference());
				} else
					return String.format("context.body(%s.class)", getType().reference());

			case formData :
				if ("file".equals(getPar().type))
					return String.format("context.part(\"%s\")", getPar().name);
				else {
					return doArray();
				}

			case header :
				return convert(String.format("context.header(\"%s\")", getPar().name));

			case path :
				return convert(String.format("context.path(\"%s\")", getPar().name));

			case query :
				return doArray();

			default :
				throw new UnsupportedOperationException("No such in type: " + getPar().in);
		}
	}

	private String doArray() {
		if (getType().isArray()) {
			switch (getPar().collectionFormat) {
				case csv :
					return convert(String.format("context.csv(context.parameter(\"%s\"))", getPar().name));

				case pipes :
					return convert(String.format("context.pipes(context.parameter(\"%s\"))", getPar().name));

				case ssv :
					return convert(String.format("context.ssv(context.parameter(\"%s\"))", getPar().name));

				case tsv :
					return convert(String.format("context.tsv(context.parameter(\"%s\"))", getPar().name));

				default :
				case multi :
				case none :
					return convert(String.format("context.parameters(\"%s\")", getPar().name));
			}
		} else
			return convert(String.format("context.parameter(\"%s\")", getPar().name));
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
