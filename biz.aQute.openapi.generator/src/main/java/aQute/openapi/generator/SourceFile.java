package aQute.openapi.generator;

import static aQute.openapi.generator.OpenAPIGenerator.logger;

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
	final Map<String, SourceMethod>	methods	= new HashMap<>();
	final Map<String, SourceType>	types	= new HashMap<>();
	final RootSourceRoute			root	= new RootSourceRoute();
	final private String					name;
	final OpenAPIGenerator			gen;

	public SourceFile(OpenAPIGenerator gen, String name) {
		this.gen = gen;
		this.name = name;
	}

	void addOperation(String path, String method, OperationObject operation,
			List<ParameterObject> parameters) {

		SourceMethod m = getSourceMethod(path, method, operation);

		for (Map.Entry<String, ResponseObject> e : operation.responses
				.entrySet()) {

			ResponseObject responseObject = getResponseObject(path, m, e);

			addType(gen.getSourceType(responseObject.schema));

			for (HeaderObject header : responseObject.headers.values()) {
				addType(gen.getSourceType(header));
			}
		}

		logger.info("        Return {}", m.returnType);

		for (ParameterObject par : parameters) {
			SourceArgument sourceArgument = new SourceArgument(m, par);
			OpenAPIGenerator.logger.info("        Par (base) {}",
					sourceArgument);
			m.prototype.put(par.name, sourceArgument);
			addType(sourceArgument.type);
		}
		for (ParameterObject par : operation.parameters) {
			SourceArgument sourceArgument = new SourceArgument(m, par);
			OpenAPIGenerator.logger.info("        Par (op) {}", sourceArgument);
			m.prototype.put(par.name, sourceArgument);
			addType(sourceArgument.type);
		}

		root.add(path, method, m);
	}

	private ResponseObject getResponseObject(String path, SourceMethod m, Map.Entry<String, ResponseObject> e) {
		ResponseObject responseObject = e.getValue();
		String responseCode = e.getKey();
		if (responseCode.equals("200")) {
			m.returnType = gen.getSourceType(responseObject.schema);
			if (m.returnType == null) {
				gen.warning("no return type for %s", path);
			}
			addType(m.returnType);
		}
		return responseObject;
	}

	private SourceMethod getSourceMethod(String path, String method, OperationObject operation) {
		SourceMethod m = new SourceMethod(this, path, method, operation);

		while (methods.containsKey(m.name)) {

			SourceMethod duplicate = methods.get(m.name);
			gen.error(
					"Duplicate operation id: %s, current=%s, previous=%s",
					operation.operationId, path, duplicate.path);

			operation.operationId = operation.operationId + "_";
			m = new SourceMethod(this, path, method, operation);
		}
		methods.put(m.name, m);
		return m;
	}

	public void addType(SourceType type) {
		if (type == null) {
			System.out.println("No source type");
		}
		SourceType old = types.put(type.reference(), type);
		if (old == null) {
			type.addTypes(this);
		}
	}


	public String getTypeName() {
		return gen.config.typePrefix + gen.toTypeName(name);
	}

	public String getPackageName() {
		return gen.packagePrefix + name.toLowerCase();
	}

	public String getPath() {
		return getPath(getTypeName());
	}
	
	public String getPath(String name) {
		return getPackageName().replace('.', '/') + "/" + name + ".java";
	}


	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SourceFile [name=" + name + "]";
	}

	public Collection<SourceMethod> getMethods() {
		return methods.values();
	}

	public Collection<SourceType> getTypes() {
		return types.values();
	}

	public String[] getSecurities() {
		return methods.values().stream()
				.filter(sm -> sm.operation.security != null)
				.flatMap(sm -> sm.operation.security.stream())
				.flatMap(map -> map.keySet().stream()).distinct()
				.toArray(String[]::new);
	}

	public RootSourceRoute getRoot() {
		return root;
	}

	public String getFQN() {
		return getPackageName() +"."+getName();
	}

}
