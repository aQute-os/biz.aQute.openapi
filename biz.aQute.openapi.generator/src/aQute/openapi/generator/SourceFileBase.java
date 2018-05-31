package aQute.openapi.generator;

public class SourceFileBase extends SourceFile {

	public SourceFileBase(OpenAPIGenerator gen, String name) {
		super(gen, name);
	}

	@Override
	public String getPackageName() {
		return getGen().packagePrefix.substring(0, getGen().packagePrefix.length() - 1);
	}

}
