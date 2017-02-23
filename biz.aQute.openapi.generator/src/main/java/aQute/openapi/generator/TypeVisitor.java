package aQute.openapi.generator;

public interface TypeVisitor {
	Object visitMember(String key, Object value);
	Object visitMember(int index, Object value);
	Object visitSimple(Object o);
}
