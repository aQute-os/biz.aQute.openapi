package aQute.openapi.java.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import aQute.bnd.annotation.headers.ProvideCapability;
import aQute.bnd.annotation.headers.RequireCapability;
import aQute.lib.io.IO;
import aQute.openapi.generator.OpenAPIGenerator;
import aQute.openapi.generator.SourceArgument;
import aQute.openapi.generator.SourceFile;
import aQute.openapi.generator.SourceFileBase;
import aQute.openapi.generator.SourceMethod;
import aQute.openapi.generator.SourceProperty;
import aQute.openapi.generator.SourceRoute;
import aQute.openapi.generator.SourceType;
import aQute.openapi.generator.SourceType.NummericType;
import aQute.openapi.generator.SourceType.ObjectType;
import aQute.openapi.generator.SourceType.StringEnumType;
import aQute.openapi.generator.VersionHelper;
import aQute.openapi.v2.api.ExternalDocumentationObject;
import aQute.openapi.v2.api.HeaderObject;
import aQute.openapi.v2.api.ItemsObject;
import aQute.openapi.v2.api.OperationObject;
import aQute.openapi.v2.api.ParameterObject;
import aQute.openapi.v2.api.ResponseObject;
import aQute.openapi.v2.api.SecuritySchemeObject;
import aQute.openapi.v2.api.TagObject;

public class JavaGenerator extends BaseSourceGenerator {

	final OpenAPIGenerator	gen;
	protected SourceFile	sourceFile;
	private SourceMethod	method;

	public JavaGenerator(OpenAPIGenerator gen, File output) {
		super(output);
		this.gen = gen;
	}

	public void generate(SourceFile sourceFile) throws FileNotFoundException {
		this.sourceFile = sourceFile;

		String javaClassPath = sourceFile.getPath();
		String packageName = sourceFile.getPackageName();

		generate(javaClassPath, () -> {

			doPackage(packageName);

			doLicense();
			doAbstractClassImportSection();

			doTagComment(gen.getTag(sourceFile.getName()));

			doClass(sourceFile.getTypeName(), () -> {

				doBasePathConstant(gen.getSwagger().basePath);

				doAbstractMethods(sourceFile);

				doDeclareTypes(sourceFile);

				doEndPublicPart();

				doConstructor(sourceFile.getTypeName());
				doInitialize(sourceFile);
				doDispatch();

				doConverterMethods(sourceFile);
			});
			doGeneratorVersion();
		});

		if (needsImplementation()) {
			String requireName = "Require" + sourceFile.getTypeName();
			String requirePath = sourceFile.getPath(requireName) + ".java";

			generate(requirePath, () -> {
				doRequireProvideProlog(packageName);
				doImport(RequireCapability.class);
				doRequireAnnotation(requireName);
			});

			String provideName = "Provide" + sourceFile.getTypeName();
			String providePath = sourceFile.getPath(provideName) + ".java";

			generate(providePath, () -> {
				doRequireProvideProlog(packageName);
				doImport(ProvideCapability.class);
				doProvideAnnotation(provideName);
			});
		}

		String packageInfoPath = sourceFile.getPath("package-info.java");
		generate(packageInfoPath, () -> {
			doVersionAnnotation();
			doPackage(packageName);
			doLicense();
			doImport(org.osgi.annotation.versioning.Version.class);
		});
	}

	public void doGeneratorVersion() {
		format("\n// aQute OpenAPI generator version %s\n", OpenAPIGenerator.getGeneratorVersion());
	}

	public void doRequireProvideProlog(String packageName) {
		doPackage(packageName);
		doLicense();
		doImport(Target.class);
		doImport(ElementType.class);
		doImport(RetentionPolicy.class);
		doImport(Retention.class);
	}

	protected void doVersionAnnotation() {
		try (Annotate<org.osgi.annotation.versioning.Version> v = annotate(
				org.osgi.annotation.versioning.Version.class);) {
			v.set(v.get().value(), escapeString(gen.getVersion()));
		}
	}

	protected void doLicense() {
		doLicense(gen.getConfig().license);
	}

	protected void doRequireAnnotation(String name) {
		doRequireMetaAnnotations();
		doAnnotationInterface(Modifier.PUBLIC, name).close();
	}

	protected void doProvideAnnotation(String name) {
		doProvideMetaAnnotations();
		doAnnotationInterface(Modifier.PUBLIC, name).close();
	}

	protected void doRequireMetaAnnotations() {
		doRetentionAndTarget();
		try (Annotate<RequireCapability> rc = annotate(RequireCapability.class);) {
			rc.setQuoted(rc.get().ns(), "aQute.openapi");
			rc.setQuoted(rc.get().effective(), "active");
			String mv = gen.getVersion();
			VersionHelper r = new VersionHelper(mv);
			String filter = String.format("(&(aQute.openapi=%s)%s)", sourceFile.getFQN(), r.getFilter());
			rc.setQuoted(rc.get().filter(), filter);
		}
	}

	protected void doProvideMetaAnnotations() {
		doRetentionAndTarget();
		try (Annotate<ProvideCapability> rc = annotate(ProvideCapability.class);) {
			rc.setQuoted(rc.get().ns(), "aQute.openapi");
			rc.setQuoted(rc.get().effective(), "active");
			rc.setQuoted(rc.get().name(), sourceFile.getFQN());
			String mv = gen.getVersion();
			VersionHelper v = new VersionHelper(mv);
			rc.setQuoted(rc.get().version(), v.getWithoutQualifier().toString());
		}
	}

	protected void doRetentionAndTarget() {
		try (Annotate<Target> target = annotate(Target.class);) {
			target.set(target.get().value(), "ElementType.TYPE");
		}
		try (Annotate<Retention> retention = annotate(Retention.class);) {
			retention.set(retention.get().value(), "RetentionPolicy.RUNTIME");
		}
	}

	private void doConverterMethods(SourceFile sourceFile) {
		for (SourceMethod m : sourceFile.getMethods().values()) {
			this.method = m;
			doConverterMethodAnnotatons();
			doConverterMethod();
		}
		this.method = null;
	}

	protected void doDeclareTypes(SourceFile sourceFile) {
		for (SourceType t : sourceFile.getTypes()) {
			doDeclareType(t);
		}
	}

	protected void doAbstractMethods(SourceFile sourceFile) {
		for (SourceMethod m : sourceFile.getMethods().values()) {
			this.method = m;
			doAbstractMethodAnnotatons(m);
			doAbstractMethod(m);
		}
		this.method = null;
	}

	protected void doConverterMethodAnnotatons() {

	}

	protected void doAbstractClassImportSection() {
		doImport("aQute.openapi.provider.OpenAPIBase");
		doImport("aQute.openapi.provider.OpenAPIContext");
		doImport(DateTimeFormatter.class);
		String dateTimeClass = gen.getDateTimeClass();
		doImport(dateTimeClass);
		doImport(LocalDate.class);
		doImports(gen.getConfig().importsExtra);
	}

	protected void doConverterMethod() {

		String methodName = method.getName() + "_" + method.getMethod() + "_";
		doMethod(Modifier.PRIVATE, "void", methodName).parameter("OpenAPIContext", "context")
				.throws_("Exception")
				.body(() -> {
					OperationObject operation = method.getOperation();
					format("    context.setOperation(%s);\n", escapeString(operation.operationId));

					doMethodSecurity();

					boolean hasValidator = doDeclareLocalVariables();

					format("\n");

					if (hasValidator) {
						doValidators(operation);
					}

					doAbstractMethodInvocation();

					int defaultResultCode = method.getDefaultResultCode();

					doSetResult(defaultResultCode);
				});
	}

	private void doSetResult(int defaultResultCode) {
		if (!method.getReturnType().isVoid())
			format("    context.setResult(result, %s);\n", defaultResultCode);
		else
			format("    context.setResult(null, %s);\n", defaultResultCode);
	}

	private void doAbstractMethodInvocation() {
		if (!method.getReturnType().isVoid())
			format("    Object result = %s(", method.getName());
		else
			format("    %s(", method.getName());

		String del = "";
		for (SourceArgument a : method.getArguments()) {
			String name = a.getName() + "_";
			format("%s%s", del, name);
			del = ", ";
		}

		format(");\n");
	}

	private void doValidators(OperationObject operation) {
		format("\n    //  VALIDATORS \n\n");
		format("    context.begin(%s);\n", escapeString(operation.operationId));

		for (SourceArgument a : method.getArguments()) {
			String name = a.getName() + "_";
			if (a.getType().hasValidator()) {
				if (a.getPar().required) {
					doValidators(a.getType(), name);
				} else {
					doOptionalValidator(a, name);
				}
			}
			if (a.getPar().required) {
				format("    context.require(%s,%s);\n", name, escapeString(a.getPar().name));
			}
		}
		format("    context.end();\n\n");
	}

	private boolean doDeclareLocalVariables() {
		boolean hasValidator = false;
		for (SourceArgument a : method.getArguments()) {
			doLocalVariable(a, a.getName() + "_");
			hasValidator |= a.getType().hasValidator() || a.getPar().required;
		}
		return hasValidator;
	}

	private void doOptionalValidator(SourceArgument a, String name) {
		format("    if(%s.isPresent()) {\n", name);
		doValidators(a.getType(), name + ".get()");
		format("    }\n", a.getName());
	}

	protected void doLocalVariable(SourceArgument a, String name) {
		if (!a.getPar().required) {
			format("java.util.Optional<%s> %s = context.optional(%s);\n", a.getType().wrapper().reference(), name,
					a.access());
		} else
			format("%s %s_ = %s;\n", a.getType().wrapper().reference(), a.getName(), a.access());
	}

	protected void doMethodSecurity() {
		OperationObject operation = method.getOperation();
		if (operation.security == null)
			return;

		for (Map<String,List<String>> sec : operation.security) {

			for (Map.Entry<String,List<String>> e : sec.entrySet()) {

				SecuritySchemeObject sso = gen.getSwagger().securityDefinitions.get(e.getKey());

				if (sso == null) {
					gen.error("Method %s referred to security %s but not present", method.getName(), e.getKey());
					continue;
				}

				switch (sso.type) {
					case api_key :
						String access;
						switch (sso.in) {
							default :
								OpenAPIGenerator.getLogger()
										.error("Invalid type for the security Api Key header/path {}", sso.in);
							case header :
								access = String.format("context.header(%s)", escapeString(sso.name));
								break;
							case query :
								access = String.format("toString(context.parameter(%s))", escapeString(sso.name));
								break;
						}
						format("    context.verify(%s, %s);\n", e.getKey(), access);
						break;
					case basic :
						format("    context.verify(%s);\n", e.getKey());

						break;
					case oauth2 :

						format("    context.verify(%s", e.getKey());
						for (String scope : e.getValue()) {
							format(",%s", escapeString(scope));
						}
						format(");\n");
						break;
					default :
						break;
				}
			}
		}

		format("\n");
	}

	protected void doDispatch() {
		format("  public boolean dispatch_(OpenAPIContext context, String segments[], int index ) throws Exception {\n");

		SourceRoute.RootSourceRoute root = sourceFile.getRoot();
		root.generate(gen, getFormatter(), "  ");

		doOpenAPIFileSupport();

		format("    return false;\n");
		format("  }\n\n");
	}

	private void doOpenAPIFileSupport() {
		String openAPIFile = gen.getConfig().openapiFile;
		if (openAPIFile != null) {
			openAPIFile = escapeString(openAPIFile);
			SourceFileBase base = gen.getBaseSourceFile();
			format("    if ( segments.length == 1 && %s.equals(segments[0])) {\n", openAPIFile);
			format("        getOpenAPIContext().copy( %s.class.getResourceAsStream(%s), \"application/json\");\n",
					base.getFQN(), openAPIFile);
			format("        return true;\n");
			format("    }\n");

			File output = IO.getFile(this.getOutput(), sourceFile.getPath(gen.getConfig().openapiFile));
			output.getParentFile().mkdirs();
			try {
				IO.copy(gen.getInputFile(), output);
			} catch (Exception e) {
				gen.exception(e, "Could not copy the openapi input file %s to the output %s", gen.getInputFile(),
						output);
			}
		}
	}

	private void doInitialize(SourceFile source) {

		String[] securities = source.getSecurities();

		for (String security : securities) {

			SecuritySchemeObject so = gen.getSwagger().securityDefinitions.get(security);

			if (so == null) {
				OpenAPIGenerator.getLogger().error("Reference to unknown security scheme. From {} to {}", this,
						security);
				continue;
			}

			switch (so.type) {
				case api_key :
					newField(Modifier.PROTECTED, "aQute.openapi.security.apikey.api.APIKeyDTO", security);
					break;
				case basic :
					newField(Modifier.PROTECTED, "aQute.openapi.security.basic.api.BasicDTO", security);
					break;
				case oauth2 :
					newField(Modifier.PROTECTED, "aQute.openapi.security.oauth2.api.OAuth2DTO", security);
					break;

				default :
					OpenAPIGenerator.getLogger().error(
							"Security Scheme Object refers to unknown scheme type {}, name = {}", so.type, security);
			}
		}
		doTypeConversions();

		format("\n");
	}

	protected void doTypeConversions() {

		List<String> conversions = gen.getConversions();

		doDateTimeConversions(conversions);

		if (conversions.isEmpty())
			return;

		if (sourceFile instanceof SourceFileBase) {

			format("\n");
			format("    static final public OpenAPIBase.Codec CODEC = OpenAPIBase.createOpenAPICodec();\n");
			format("    static {\n");


			for (String conversion : conversions) {
				format("      %s\n", conversion);
			}
			format("    }\n\n");
		}
		SourceFileBase base = gen.getBaseSourceFile();
		format("    public OpenAPIBase.Codec codec_() {\n");
		format("        return %s.CODEC;\n", base.getFQN());
		format("    }\n\n");
	}

	protected void doDateTimeConversions(List<String> conversions) {
		String dateFormat = gen.getConfig().dateFormat;
		if (dateFormat != null)
			conversions.add(String.format("         addDateTimeHandler(CODEC, LocalDate.class, %s);\n",
					escapeString(dateFormat)));

		String dateTimeFormat = gen.getConfig().dateTimeFormat;
		if (dateTimeFormat != null) {
			conversions.add(String.format("         addDateTimeHandler(CODEC, %s.class, %s);\n", gen.getDateTimeClass(),
					escapeString(gen.getConfig().dateTimeFormat)));
		}
	}

	protected void doConstructor(String className) {

		format("  public %s() {\n", className);
		format("    super(BASE_PATH");

		for (SourceMethod oo : sourceFile.getMethods().values()) {
			format(",\n         %s", escapeString(oo.toString()));
		}
		format(");\n");

		String[] securities = this.sourceFile.getSecurities();

		for (String security : securities) {

			SecuritySchemeObject so = gen.getSwagger().securityDefinitions.get(security);

			if (so == null) {
				continue;
			}

			format("\n\n");
			format("     %s.name = %s;\n", security, escapeString(security));
			if (so.description != null)
				format("     %s.description = %s;\n", security, escapeString(so.description));

			switch (so.type) {
				case api_key :
					break;
				case basic :
					break;
				case oauth2 :
					format("     %s.flow = %s.%s;\n", security, "aQute.openapi.security.oauth2.api.Flow", so.flow);
					format("     %s.authorizationURL = %s;\n", security, escapeString(so.authorizationUrl));
					if (so.tokenUrl != null)
						format("     %s.tokenURL = %s;\n", security, escapeString(so.tokenUrl));
					break;
			}
		}
		format("  }\n");
	}

	protected void doEndClass() {
		format("}\n");
	}

	protected void doEndPublicPart() {
		format("  /*****************************************************************/\n\n");
	}

	protected void doAbstractMethodAnnotatons(SourceMethod m) {}

	protected void doDeclareType(SourceType type) {
		if (type instanceof StringEnumType) {
			doDeclareEnumType((StringEnumType) type);
		} else if (type.isObject()) {
			doDeclareObjectType((SourceType.ObjectType) type);
		}
	}

	protected void doDeclareEnumType(StringEnumType enumType) {
		CommentBuilder comment = comment();
		comment.para(enumType.getName());
		comment.para(enumType.getSchema().description);
		comment.close();

		format("  public enum %s {\n", enumType.getName());
		String del = "";
		for (Object member : enumType.getSchema().enum$) {
			String name = member == null ? "null" : member.toString();
			String memberName = gen.makeSafe(name, gen.RESERVED, "$");
			format("%s    %s(%s)", del, memberName, escapeString(member));
			del = ",\n";
		}
		format(";\n\n", enumType.getName());
		format("    public final String value;\n\n", enumType.getName());
		format("    %s(String value) {\n", enumType.getName());
		format("      this.value = value;\n", enumType.getName());
		format("    }\n", enumType.getName());
		format("  }\n\n");
	}

	protected void doDeclareObjectType(ObjectType t) {

		CommentBuilder comment = comment();
		comment.para(t.getClassName());
		comment.para(t.getSchema().description);
		comment.close();

		doDataTypeAnnotations(t.getClassName());
		doTypeHeader(t.getClassName(), () -> {

			for (SourceProperty property : t.getProperties()) {
				String access = gen.getConfig().privateFields ? "private" : "public";
				format("    %s %s %s;\n", access, property.getType().reference(), property.getKey());

			}

			format("\n");

			if (t.hasValidator()) {
				format("    protected void validate(OpenAPIContext context, String name) {\n");
				format("       context.begin(name);\n");

				for (SourceProperty property : t.getProperties()) {

					doValidators(property.getType(), property.getKey());
				}

				format("       context.end();\n");
				format("    }\n");
			}
			if (gen.getConfig().beans) {
				for (SourceProperty property : t.getProperties()) {
					String propertyName = gen.firstCharacter(property.getKey(), true);
					format("    public %s set%s(%s %s){ this.%s=%s; return this; }\n", t.getClassName(), propertyName,
							property.getType().reference(), property.getKey(), property.getKey(), property.getKey());
					format("    public %s get%s(){ return this.%s; }\n\n", property.getType().reference(), propertyName,
							property.getKey());
				}
			} else {
				for (SourceProperty property : t.getProperties()) {
					String propertyName = property.getKey();
					format("    public %s %s(%s %s){ this.%s=%s; return this; }\n", t.getClassName(), propertyName,
							property.getType().reference(), property.getKey(), property.getKey(), property.getKey());
					format("    public %s %s(){ return this.%s; }\n\n", property.getType().reference(), propertyName,
							property.getKey());
				}
			}
		});
	}

	private void doValidators(SourceType type, String reference) {
		if (!type.hasValidator())
			return;

		if (type instanceof SourceType.NummericType) {
			doNummericValidator((SourceType.NummericType) type, reference);
		} else if (type instanceof SourceType.SimpleType) {
			doSimpleTypeValidator((SourceType.SimpleType) type, reference);
		} else if (type instanceof SourceType.ObjectType) {
			format("       %s.validate(context, %s);\n", reference, escapeString(reference));
		} else if (type instanceof SourceType.ArrayType) {
			doArrayTypeValidator((SourceType.ArrayType) type, reference);
		} else
			gen.error("Unknown type %s with a validator?", type);
	}

	protected void doValidate(String expression, String reference) {
		format("    context.validate(%s, %s, \"%2$s\", %s);\n", expression, reference, escapeString(expression));
	}

	public void doArrayTypeValidator(SourceType.ArrayType type, String reference) {
		ItemsObject schema = type.getSchema();
		if (schema.maxItems >= 0) {
			doValidate(reference + ".length <= " + schema.maxItems, reference);
		}

		if (schema.minItems >= 0) {
			doValidate(reference + ".length >= " + schema.minItems, reference);
		}

		if (type.getComponentType().hasValidator()) {
			format("    int %s_counter=0;\n", reference);
			format("    for( %s %s_item : %2$s) {\n", type.getComponentType().reference(), reference);
			format("        context.begin(%s_counter++);\n", reference);
			doValidators(type.getComponentType(), reference + "_item");
			format("        context.end();\n");
			format("    }\n");
		}
	}

	public void doSimpleTypeValidator(SourceType.SimpleType type, String reference) {
		ItemsObject schema = type.getSchema();

		if (schema.pattern != null) {
			doValidate(reference + ".matches(" + escapeString(schema.pattern) + ")", reference);
		}
		if (schema.minLength >= 0) {
			doValidate(reference + ".length() >= " + schema.minLength, reference);
		}
		if (schema.maxLength >= 0) {
			doValidate(reference + ".length() <= " + schema.maxLength, reference);
		}

		if (schema.enum$ != null && !schema.enum$.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Object o : schema.enum$) {
				sb.append(", ").append(escapeString(o));
			}
			doValidate("context.in(" + reference + sb + ")", reference);
		}
	}

	protected void doNummericValidator(NummericType type, String reference) {
		ItemsObject schema = type.getSchema();

		if (!Double.isNaN(type.getSchema().minimum)) {
			if (schema.exclusiveMinimum)
				doValidate(reference + " > " + type.toString(schema.minimum), reference);
			else
				doValidate(reference + " >= " + type.toString(schema.minimum), reference);
		}
		if (!Double.isNaN(schema.maximum)) {
			if (schema.exclusiveMaximum)
				doValidate(reference + " < " + type.toString(schema.maximum), reference);
			else
				doValidate(reference + " <= " + type.toString(schema.maximum), reference);
		}

		if (schema.multipleOf > 0) {
			doValidate("(" + reference + " % " + schema.multipleOf + ") == 0", reference);
		}

		if (schema.enum$ != null && !schema.enum$.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			for (Object o : schema.enum$) {
				if (o instanceof Double || o instanceof Float)
					sb.append(", ").append(o);
				else
					sb.append(", ").append(o);
			}
			doValidate("in_(" + reference + sb + ")", reference);
		}
	}

	protected void doDataTypeAnnotations(String className) {

	}

	protected void doTypeHeader(String typeName, Runnable body) {
		doClass(body, Modifier.PUBLIC + Modifier.STATIC, typeName,
				getOrDefault(gen.getConfig().dtoType, "OpenAPIBase.DTO"));
	}

	protected void doAbstractMethod(SourceMethod m) {

		OperationObject operation = m.getOperation();
		if (operation == null)
			return;

		doAbstractMethodComment(m, operation);

		MethodBuilder mb = doMethod(Modifier.ABSTRACT + Modifier.PROTECTED, m.getReturnType().asReturnType(),
				m.getName());
		for (SourceArgument argument : m.getSourceArguments()) {
			if (!argument.getPar().required) {
				mb.parameter(String.format("java.util.Optional<%s>", argument.getType().wrapper().reference()),
						argument.getName());
			} else {
				mb.parameter(argument.getType().reference(), argument.getName());
			}
		}
		mb.throws_("Exception");

		for (Entry<String,ResponseObject> r : operation.responses.entrySet()) {
			if (r.getKey().equals("200"))
				continue;

			String exception = SourceMethod.getResponses().get(r.getKey());
			if (exception != null) {
				mb.throws_("OpenAPIBase." + exception);
			}
		}
		mb.noBody();
	}

	private void doAbstractMethodComment(SourceMethod m, OperationObject operation) {
		try (CommentBuilder comment = comment();) {

			comment.para(
					operation.method.toString().toUpperCase() + " " + operation.path + " = " + operation.operationId);
			comment.para(operation.summary);
			comment.para(operation.description);

			for (SourceArgument argument : m.getSourceArguments()) {
				ParameterObject parameterObject = argument.getParameterObject();

				StringBuilder extended = new StringBuilder();
				if (parameterObject.description != null) {
					extended.append(parameterObject.description);
				}
				extended.append(" (").append(parameterObject.in).append(")");

				if (parameterObject.collectionFormat != null)
					extended.append(" collectionFormat=%s").append(parameterObject.collectionFormat);

				if (parameterObject.uniqueItems)
					extended.append(" (unique items) ");

				comment.param(parameterObject.name, extended.toString());
			}

			boolean hadOk = false;
			for (Entry<String,ResponseObject> r : operation.responses.entrySet()) {
				String responseCode = r.getKey();
				ResponseObject responseObject = r.getValue();

				boolean isOk = responseCode.equals("200") || responseCode.equals("201") || responseCode.equals("203");
				if (hadOk || isOk) {
					hadOk = true;
					comment.retrn(responseCode, responseObject.description);
					continue;
				}

				String exception = SourceMethod.getResponses().get(r.getKey());
				if (exception != null) {
					comment.throws_(exception, responseObject.description);
				} else {
					comment.throws_(responseCode, responseObject.description);
				}
			}

			for (Entry<String,ResponseObject> r : operation.responses.entrySet()) {
				String resultCode = r.getKey();
				comment.para(resultCode);
				for (Map.Entry<String,HeaderObject> e : r.getValue().headers.entrySet()) {
					HeaderObject headerObject = e.getValue();
					SourceType type = m.getParent().getGen().getSourceType(headerObject);
					comment.para(e.getKey() + " - " + type.reference());
					if (headerObject.description != null) {
						comment.para(headerObject.description);
					}
				}
			}
		}
	}

	protected void doBasePathConstant(String basePath) {
		doConstant(Modifier.PUBLIC + Modifier.FINAL + Modifier.STATIC, "String", "BASE_PATH", basePath);
	}

	protected void doClass(String name, Runnable body) {
		doClassAnnotations(name);
		doClass(body, Modifier.ABSTRACT + Modifier.PUBLIC, name, "OpenAPIBase");
	}

	protected void doClassAnnotations(String name) {
		if (!needsImplementation())
			return;

		format("@Require%s\n", name);
	}

	private boolean needsImplementation() {
		return !sourceFile.getMethods().isEmpty();
	}

	protected void doTagComment(Optional<TagObject> optional) {
		CommentBuilder comment = comment();
		if (optional.isPresent()) {
			TagObject tagObject = optional.get();

			comment. //
					para(tagObject.name.toUpperCase()). //
					para(tagObject.description). //
					visit((c) -> para(c, tagObject.externalDocs));
		}
		comment.para("<ul>");
		for (SourceMethod o : sourceFile.getMethods().values()) {
			String path = o.getPath();
			path = path.replaceAll("\\{", "<b>[");
			path = path.replace("}", "]</b>");
			comment.para("<li>{@link " + o.getLink() + " " + o.getMethod().toString().toUpperCase() + " " + path
					+ " =  " + o.getOperation().operationId + "}");

		}
		comment.para("</ul>");
		comment.close();
	}

	protected String access(SourceArgument arg) {
		switch (arg.getPar().in) {
			case body :
				return String.format("context.body(%s.class)", arg.getType().reference());

			case formData :
				if ("file".equals(arg.getPar().type))
					return String.format("context.part(\"%s\")", arg.getPar().name);
				else {
					return doArray(arg);
				}

			case header :
				return convert(String.format("context.header(\"%s\")", arg.getPar().name), arg);

			case path :
				return convert(String.format("context.path(\"%s\")", arg.getPar().name), arg);

			case query :
				return doArray(arg);

			default :
				throw new UnsupportedOperationException("No such in type: " + arg.getPar().in);
		}
	}

	String convert(String access, SourceArgument arg) {
		String s = arg.getType().conversion(access, arg.getPar().collectionFormat);
		if (s == null)
			return access;
		else
			return "context." + s;
	}

	private String doArray(SourceArgument arg) {
		if (arg.getType().isArray()) {
			switch (arg.getPar().collectionFormat) {
				case csv :
					return convert(String.format("context.csv(context.parameter(\"%s\"))", arg.getPar().name), arg);

				case pipes :
					return convert(String.format("context.pipes(context.parameter(\"%s\"))", arg.getPar().name), arg);

				case ssv :
					return convert(String.format("context.ssv(context.parameter(\"%s\"))", arg.getPar().name), arg);

				case tsv :
					return convert(String.format("context.tsv(context.parameter(\"%s\"))", arg.getPar().name), arg);

				default :
				case multi :
				case none :
					return convert(String.format("context.parameters(\"%s\")", arg.getPar().name), arg);
			}
		} else
			return convert(String.format("context.parameter(\"%s\")", arg.getPar().name), arg);
	}

	protected void para(CommentBuilder comment, ExternalDocumentationObject externalDocs) {
		if (externalDocs != null) {
			if (externalDocs.description != null)
				comment.para(externalDocs.description);
			if (externalDocs.url != null)
				comment.see(externalDocs.url);
		}
	}

}
