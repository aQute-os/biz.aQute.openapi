package aQute.openapi.generator;

public class SourceProperty {

	private final String			key;
	private final SourceType		type;
	final OpenAPIGenerator	gen;

	public SourceProperty(OpenAPIGenerator gen, String key, SourceType type) {
		this.gen = gen;
		this.key = key;
		this.type = type;
	}

	public SourceType getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

}
