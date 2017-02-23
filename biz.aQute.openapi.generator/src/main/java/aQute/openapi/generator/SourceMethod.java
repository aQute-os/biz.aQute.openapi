package aQute.openapi.generator;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import aQute.openapi.v2.api.OperationObject;

public class SourceMethod {
	static final Map<String, String>	responses	= new LinkedHashMap<>();
	static int id = 1000;
	final SourceFile					parent;
	final String						path;
	final Map<String, SourceArgument>	prototype	= new LinkedHashMap<>();
	final OperationObject				operation;
	final String						method;
	final String						name;
	SourceType							returnType	= SourceType.VOID;

	static {
		responses.put("301", "MovedPermanentlyResponse");
		responses.put("302", "MovedTemporarilyResponse");
		responses.put("303", "SeeOtherResponse");
		responses.put("304", "NotModifiedResponse");
		responses.put("307", "TemporaryRedirectResponse");
		responses.put("400", "BadRequestResponse");
		responses.put("401", "UnauthorizedResponse");
		responses.put("403", "ForbiddenResponse");
		responses.put("409", "ConflictResponse");
		responses.put("410", "GoneResponse");
		responses.put("417", "ExpectationFailedResponse");
		responses.put("501", "NotImplementedResponse");

	}

	public SourceMethod(SourceFile parent, String path, String method,
			OperationObject operation) {
		this.parent = parent;
		this.path = path;
		this.method = method;
		this.operation = operation;
		if ( operation.operationId == null) {
			operation.operationId = "operation_" + id++;
			parent.gen.error("No operationId specified for path %s", path);
			this.name = parent.gen.toMemberName(path.substring(1).replace('/', '_'));
		} else 
			this.name = parent.gen.toMemberName(operation.operationId);
		
	}



	public void gatherTypes(SourceFile sourceFile) {
		sourceFile.addType(returnType);
		for (SourceArgument e : prototype.values()) {
			sourceFile.addType(e.type);
		}
	}

	int getDefaultResultCode() {
		int defaultResultCode = 200;
		for (String response : operation.responses.keySet()) {
			int resultCode;
			if ("default".equals(response))
				resultCode = 200;
			else
				resultCode = Integer.parseInt(response);

			boolean isOk = resultCode / 100 == 2;
			if (isOk) {
				defaultResultCode = resultCode;
			}
		}
		return defaultResultCode;
	}

	public String toString() {
		return operation.operationId;
	}

	public String toParameterName(String name) {
		return parent.gen.toMemberName(name);
	}

	public OperationObject getOperation() {
		return operation;
	}

	public Collection<SourceArgument> getSourceArguments() {
		return prototype.values();
	}

	public String getName() {
		return name;
	}

	public Collection<SourceArgument> getArguments() {
		return prototype.values();
	}



	public String getLink() {
		StringBuilder sb = new StringBuilder();
		sb.append("#");
		sb.append(name);
		sb.append("(");
		String del = "";
		for ( SourceArgument a : prototype.values()) {
			sb.append(del);
			sb.append( a.type.reference());
			del = ",";
		}
		sb.append(")");
		return sb.toString();
	}
}
