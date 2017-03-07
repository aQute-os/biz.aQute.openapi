package aQute.openapi.generator;

import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.Map;

import aQute.openapi.v2.api.In;
import aQute.openapi.v2.api.OperationObject;

public class SourceMethod {
	private static final Map<String,String>		responses	= new LinkedHashMap<>();
	static int							id			= 1000;
	private final SourceFile					parent;
	private final String						path;
	final Map<String,SourceArgument>	prototype	= new LinkedHashMap<>();
	final OperationObject				operation;
	private final String						method;
	final String						name;
	private SourceType							returnType	= SourceType.VOID;

	static {
		getResponses().put("301", "MovedPermanentlyResponse");
		getResponses().put("302", "MovedTemporarilyResponse");
		getResponses().put("303", "SeeOtherResponse");
		getResponses().put("304", "NotModifiedResponse");
		getResponses().put("307", "TemporaryRedirectResponse");
		getResponses().put("400", "BadRequestResponse");
		getResponses().put("401", "UnauthorizedResponse");
		getResponses().put("403", "ForbiddenResponse");
		getResponses().put("409", "ConflictResponse");
		getResponses().put("410", "GoneResponse");
		getResponses().put("417", "ExpectationFailedResponse");
		getResponses().put("501", "NotImplementedResponse");

	}

	public SourceMethod(SourceFile parent, String path, String method, OperationObject operation) {
		this.parent = parent;
		this.path = path;
		this.method = method;
		this.operation = operation;
		if (operation.operationId == null) {
			operation.operationId = "operation_" + id++;
			parent.getGen().error("No operationId specified for path %s", path);
			this.name = parent.getGen().toMemberName(path.substring(1).replace('/', '_'));
		} else
			this.name = parent.getGen().toMemberName(operation.operationId);

	}

	public void gatherTypes(SourceFile sourceFile) {
		sourceFile.addType(getReturnType());
		for (SourceArgument e : prototype.values()) {
			sourceFile.addType(e.getType());
		}
	}

	public int getDefaultResultCode() {
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
		try (Formatter f = new Formatter();) {

			f.format("%-20s %-6s %s", operation.operationId, operation.method.toString().toUpperCase(), operation.path);

			String del = "?";
			SourceArgument body = null;
			for (SourceArgument sa : getArguments()) {
				if (sa.getPar().in == In.query) {
					f.format("%s%s", del, sa.getName());
					del = "&";
				} else if (sa.getPar().in == In.body) {
					body = sa;
				}
			}

			if (body != null && body.getType() != null && !body.getType().isVoid())
				f.format("  PAYLOAD %s", body.getType().reference());

			if (!getReturnType().isVoid())
				f.format("  RETURN %s", getReturnType().reference());
			return f.toString();
		}
	}

	public String toParameterName(String name) {
		return getParent().getGen().toMemberName(name);
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
		for (SourceArgument a : prototype.values()) {
			sb.append(del);
			sb.append(a.getType().reference());
			del = ",";
		}
		sb.append(")");
		return sb.toString();
	}

	public String getMethod() {
		return method;
	}

	public SourceType getReturnType() {
		return returnType;
	}

	public void setReturnType(SourceType returnType) {
		this.returnType = returnType;
	}

	public static Map<String,String> getResponses() {
		return responses;
	}

	public SourceFile getParent() {
		return parent;
	}

	public String getPath() {
		return path;
	}
}
