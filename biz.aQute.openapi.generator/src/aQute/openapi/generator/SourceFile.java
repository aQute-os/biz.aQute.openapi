package aQute.openapi.generator;

import static aQute.openapi.generator.OpenAPIGenerator.getLogger;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import aQute.openapi.generator.SourceRoute.RootSourceRoute;
import aQute.openapi.v2.api.HeaderObject;
import aQute.openapi.v2.api.OperationObject;
import aQute.openapi.v2.api.ParameterObject;
import aQute.openapi.v2.api.ResponseObject;

public class SourceFile {
	private final Map<String,SourceMethod>	methods	= new HashMap<>();
	final Map<String,SourceType>	types	= new HashMap<>();
	final RootSourceRoute			root	= new RootSourceRoute();
	final private String			name;
	private final OpenAPIGenerator			gen;

	public SourceFile(OpenAPIGenerator gen, String name) {
		this.gen = gen;
		this.name = name;
	}

	void addOperation(String path, String method, OperationObject operation, List<ParameterObject> parameters) {

		SourceMethod m = getSourceMethod(path, method, operation);

		for (Map.Entry<String,ResponseObject> e : operation.responses.entrySet()) {

			ResponseObject responseObject = getResponseObject(path, m, e);

			addType(getGen().getSourceType(responseObject.schema));

			for (HeaderObject header : responseObject.headers.values()) {
				addType(getGen().getSourceType(header));
			}
		}

		getLogger().info("        Return {}", m.getReturnType());

		for (ParameterObject par : parameters) {
			SourceArgument sourceArgument = new SourceArgument(m, par);
			OpenAPIGenerator.getLogger().info("        Par (base) {}", sourceArgument);
			m.prototype.put(par.name, sourceArgument);
			addType(sourceArgument.getType());
		}
		for (ParameterObject par : operation.parameters) {
			SourceArgument sourceArgument = new SourceArgument(m, par);
			OpenAPIGenerator.getLogger().info("        Par (op) {}", sourceArgument);
			m.prototype.put(par.name, sourceArgument);
			addType(sourceArgument.getType());
		}

		root.add(path, method, m);
	}

	private ResponseObject getResponseObject(String path, SourceMethod m, Map.Entry<String,ResponseObject> e) {
		ResponseObject responseObject = e.getValue();
		String responseCode = e.getKey();
		if (responseCode.equals("200")) {
			m.setReturnType(getGen().getSourceType(responseObject.schema));
			if (m.getReturnType() == null) {
				getGen().warning("no return type for %s", path);
			}
			addType(m.getReturnType());
		}
		return responseObject;
	}

	private SourceMethod getSourceMethod(String path, String method, OperationObject operation) {
		SourceMethod m = new SourceMethod(this, path, method, operation);

		while (getMethods().containsKey(m.name)) {

			SourceMethod duplicate = getMethods().get(m.name);
			getGen().error("Duplicate operation id: %s, current=%s, previous=%s", operation.operationId, path,
					duplicate.getPath());

			operation.operationId = operation.operationId + "_";
			m = new SourceMethod(this, path, method, operation);
		}
		getMethods().put(m.name, m);
		return m;
	}

	public void addType(SourceType type) {
		if (type == null) {
			System.out.println("No source type");
			return;
		}
		SourceType old = types.put(type.reference(), type);
		if (old == null) {
			type.addTypes(this);
		}
	}

	public String getTypeName() {
		return getGen().getConfig().typePrefix + getGen().toTypeName(name);
	}

	public String getPackageName() {
		return getGen().packagePrefix + name.toLowerCase();
	}

	public String getPath() {
		return getPath(getTypeName()) + ".java";
	}

	public String getPath(String name) {
		return getPackageName().replace('.', '/') + "/" + name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SourceFile [name=" + name + "]";
	}

	public Map<String,SourceMethod> getMethods() {
		return methods;
	}

	public Collection<SourceType> getTypes() {
		return types.values();
	}

	public String[] getSecurities() {
		return getMethods().values()
				.stream()
				.filter(sm -> sm.operation.security != null)
				.flatMap(sm -> sm.operation.security.stream())
				.flatMap(map -> map.keySet().stream())
				.distinct()
				.toArray(String[]::new);
	}

	public RootSourceRoute getRoot() {
		return root;
	}

	public String getFQN() {
		return getPackageName() + "." + getTypeName();
	}

	public OpenAPIGenerator getGen() {
		return gen;
	}

}
