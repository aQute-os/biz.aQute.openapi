package aQute.openapi.generator;

public class SourceProperty {

	final String			key;
	final SourceType		type;
	final OpenAPIGenerator	gen;

	public SourceProperty(OpenAPIGenerator gen, String key, SourceType type) {
		this.gen = gen;
		this.key = key;
		this.type = type;
	}

}
