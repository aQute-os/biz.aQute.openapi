package aQute.openapi.generator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.jar.Manifest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.bnd.version.MavenVersion;
import aQute.bnd.version.Version;
import aQute.json.util.JSONCodec;
import aQute.lib.env.Env;
import aQute.lib.hex.Hex;
import aQute.libg.glob.Glob;
import aQute.openapi.v2.api.ItemsObject;
import aQute.openapi.v2.api.MethodEnum;
import aQute.openapi.v2.api.OperationObject;
import aQute.openapi.v2.api.ParameterObject;
import aQute.openapi.v2.api.PathItemObject;
import aQute.openapi.v2.api.SwaggerObject;
import aQute.openapi.v2.api.TagObject;

public class OpenAPIGenerator extends Env {
	private static final String	DEFAULT_TAG	= "base";
	SwaggerObject				swagger;
	String						packagePrefix;
	Map<String, SourceFile>		sources		= new HashMap<>();
	static Logger				logger		= LoggerFactory.getLogger(OpenAPIGenerator.class);
	final Configuration			config;
	final Map<Glob, String>		tags		= new LinkedHashMap<Glob, String>();
	final String				defaultTag;

	public OpenAPIGenerator(InputStream in, Configuration config) throws Exception {
		System.out.println("in opg");
		this.config = config;
		logger.info("Create Open Api generator with base package {}", config);
		this.packagePrefix = config.packagePrefix.endsWith(".") ? config.packagePrefix : config.packagePrefix + ".";
		initializeTagMap(config);
		this.defaultTag = mapTag(DEFAULT_TAG);

		swagger = new JSONCodec().dec().resolve().from(in).get(SwaggerObject.class);

		System.out.println("Parsed " + swagger.paths.keySet());
		fixup(swagger);
		for (Map.Entry<String, PathItemObject> entry : swagger.paths.entrySet()) {
			pathItem(entry.getKey(), entry.getValue());
		}

	}

	private void fixup(SwaggerObject swagger) {
		fixupOperations(swagger);
	}

	/*
	 * OpenAPI maps the method -> operation as a field in PathItem. Since
	 * PathItem also holds other objects it seems unwise to make it a map. So
	 * here we fixup the data structure
	 */
	private void fixupOperations(SwaggerObject swagger) {
		swagger.paths.entrySet()
				.stream()
				.forEach(e -> {
					String path = e.getKey();
					PathItemObject pathItem = e.getValue();
					fixupOperation(pathItem, path, MethodEnum.delete, pathItem.delete);
					fixupOperation(pathItem, path, MethodEnum.get, pathItem.get);
					fixupOperation(pathItem, path, MethodEnum.head, pathItem.head);
					fixupOperation(pathItem, path, MethodEnum.options, pathItem.options);
					fixupOperation(pathItem, path, MethodEnum.patch, pathItem.patch);
					fixupOperation(pathItem, path, MethodEnum.post, pathItem.post);
					fixupOperation(pathItem, path, MethodEnum.put, pathItem.put);
				});

	}

	private void fixupOperation(PathItemObject pathItem, String path, MethodEnum method, OperationObject operation) {
		pathItem.path = path;
		if (operation == null)
			return;

		operation.path = path;
		pathItem.operations.put(method, operation);
		operation.method = method;
	}

	private String mapTag(String tag) {
		for (Map.Entry<Glob, String> e : tags.entrySet()) {
			Glob g = e.getKey();
			String value = e.getValue();
			if (value.equals("*"))
				value = tag;

			if (g.matcher(tag).matches()) {

				if (value.equals("!"))
					return null;

				return value;
			}
		}
		return null;
	}

	private void initializeTagMap(Configuration config) {
		if (config.tags == null)
			tags.put(Glob.ALL, "*");
		else {
			for (String e : config.tags) {
				String[] kv = e.trim().split("\\s*:\\s*");
				String matchers[] = kv[0].trim().split("\\s*,\\s*");
				for (String matcher : matchers) {
					Glob glob = new Glob(matcher);
					if (kv.length > 2) {
						error("Invalid tag specification %s. Use either 'a:b' or just 'a' which is mapped to 'a:*'", e);
					} else {
						if (kv.length == 1) {
							tags.put(glob, "*");
						} else {
							tags.put(glob, kv[1]);
						}
					}
				}
			}
			System.out.println("Matchers " + tags);
		}
	}

	private void pathItem(String path, PathItemObject item) {
		logger.info("  Path {}", path);
		operation(path, "get", item.get, item.parameters);
		operation(path, "post", item.post, item.parameters);
		operation(path, "delete", item.delete, item.parameters);
		operation(path, "patch", item.patch, item.parameters);
		operation(path, "put", item.put, item.parameters);
		operation(path, "head", item.head, item.parameters);
		operation(path, "options", item.options, item.parameters);
	}

	private void operation(String path, String method, OperationObject operation, List<ParameterObject> parameters) {
		if (operation == null)
			return;

		String tag = getTag(operation);
		if (tag == null)
			return;

		SourceFile source = getOrCreateSourceFile(tag);
		source.addOperation(path, method, operation, parameters);
	}

	private String getTag(OperationObject operation) {
		if (operation.tags == null)
			return defaultTag;

		for (String candidate : operation.tags) {
			String mappedTag = mapTag(candidate);
			if (mappedTag != null)
				return mappedTag;
		}

		return null;
	}

	private SourceFile getOrCreateSourceFile(String name) {
		name = toTypeName(name);
		SourceFile sf = sources.get(name);
		if (sf == null) {
			sf = new SourceFile(this, name);
			sources.put(name, sf);
		}
		return sf;
	}

	final static String[] JAVA_LANG_UTIL = new String[] { "AbstractMethodError", "AbstractMethodError",
			"AbstractStringBuilder", "Appendable", "ApplicationShutdownHooks", "ArithmeticException",
			"ArrayIndexOutOfBoundsException", "ArrayStoreException", "AssertionError", "AssertionStatusDirectives",
			"AutoCloseable", "Boolean", "BootstrapMethodError", "Byte", "Character", "CharacterData", "CharacterName",
			"CharSequence", "Class", "ClassCastException", "ClassCircularityError", "ClassFormatError", "ClassLoader",
			"ClassLoaderHelper", "ClassNotFoundException", "ClassValue", "Cloneable", "CloneNotSupportedException",
			"Comparable", "Compiler", "ConditionalSpecialCasing", "Deprecated", "Double", "Enum",
			"EnumConstantNotPresentException", "Error", "Exception", "ExceptionInInitializerError", "Float",
			"FunctionalInterface", "IllegalAccessError", "IllegalAccessException", "IllegalArgumentException",
			"IllegalMonitorStateException", "IllegalStateException", "IllegalThreadStateException",
			"IncompatibleClassChangeError", "IndexOutOfBoundsException", "InheritableThreadLocal", "InstantiationError",
			"InstantiationException", "Integer", "InternalError", "InterruptedException", "Iterable", "LinkageError",
			"Long", "Math", "NegativeArraySizeException", "NoClassDefFoundError", "NoSuchFieldError",
			"NoSuchFieldException", "NoSuchMethodError", "NoSuchMethodException", "NullPointerException", "Number",
			"NumberFormatException", "Object", "OutOfMemoryError", "Override", "Package", "Process", "ProcessBuilder",
			"ProcessEnvironment", "ProcessImpl", "Readable", "ReflectiveOperationException", "Runnable", "Runtime",
			"RuntimeException", "RuntimePermission", "SafeVarargs", "SecurityException", "SecurityManager", "Short",
			"Shutdown", "StackOverflowError", "StackTraceElement", "StrictMath", "String", "StringBuffer",
			"StringBuilder", "StringCoding", "StringIndexOutOfBoundsException", "SuppressWarnings", "System",
			"SystemClassLoaderAction", "Terminator", "Thread", "ThreadDeath", "ThreadGroup", "ThreadLocal", "Throwable",
			"TypeNotPresentException", "UNIXProcess", "UnknownError", "UnsatisfiedLinkError",
			"UnsupportedClassVersionError", "UnsupportedOperationException", "VerifyError", "VirtualMachineError",
			"Void", };

	public String toTypeName(String name) {
		return makeSafe(firstCharacter(name, true), JAVA_LANG_UTIL, "_");
	}

	public String firstCharacter(String name, boolean b) {
		if (b && name.length() > 0)
			return Character.toUpperCase(name.charAt(0)) + name.substring(1);
		return Character.toLowerCase(name.charAt(0)) + name.substring(1);
	}

	public String makeSafe(String name, String[] reserved, String extra) {
		assert name != null;

		name = name.trim();
		assert !name.isEmpty();

		StringBuilder sb = new StringBuilder();

		char c = name.charAt(0);

		if (!Character.isJavaIdentifierStart(c)) {
			encodeCharacter(sb, c);
			warning("Member name starts with non-java char: %s", name);
		}

		sb.append(c);

		for (int i = 1; i < name.length(); i++) {
			c = name.charAt(i);
			if (!Character.isJavaIdentifierPart(c)) {
				encodeCharacter(sb, c);
				warning("Member name contains non-java char: %s[%s]", name, i);
			} else
				sb.append(c);
		}
		String result = sb.toString();
		if (result.contains("OpenAPI")) {
			warning("A member name contains 'OpenAPI' which is reserved for the aQute.rest package: %s", name);
		}
		if (Arrays.binarySearch(reserved, result) >= 0)
			return result + extra;

		return result;

	}

	static void encodeCharacter(StringBuilder sb, char c) {
		sb.append("$").append(Hex.nibble(c / 16)).append(Hex.nibble(c % 16));
	}

	public void generate(File output) throws FileNotFoundException {
		JavaGenerator g = new JavaGenerator(this, output);
		for (SourceFile source : sources.values()) {
			g.generate(source);
		}
	}

	public String getPackage(String packageName) {
		return packagePrefix + "." + packageName;
	}

	public Optional<TagObject> getTag(String name) {
		if (swagger.tags == null)
			return Optional.empty();

		return swagger.tags.stream().filter(p -> name.equals(p.name)).findFirst();
	}

	public SourceType getSourceType(ItemsObject schema) {
		return SourceType.getSourceType(this, schema);
	}

	final String[]	RESERVED	= { "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class",
			"const", "continue", "default", "do", "double", "else", "enum", "extends", "false", "final", "finally",
			"float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native",
			"new", "null", "package", "private", "protected", "public", "return", "short", "static", "strictfp",
			"super", "switch", "synchronized", "this", "throw", "throws", "transient", "true", "try", "void",
			"volatile", "while" };
	public Object	dtoType;

	public String toMemberName(String name) {
		return makeSafe(firstCharacter(name, false), RESERVED, "$");
	}

	public Version getVersion() {
		String v = swagger.info.version;
		if (v.startsWith("v"))
			v = v.substring(1);

		MavenVersion cleanupVersion = MavenVersion.parseMavenString(v);

		return cleanupVersion.getOSGiVersion();
	}

	public List<String> tags() {
		List<String> tags = new ArrayList<String>();

		swagger.paths.values().forEach(pi -> {
			add(tags, pi.get);
		});
		return tags;
	}

	private void add(List<String> tags, OperationObject oo) {
		if (oo == null)
			return;

		if (oo.tags == null || oo.tags.length == 0)
			return;

		for (String tag : oo.tags)
			tags.add(tag);
	}

	public Collection<? extends String> paths() {
		return swagger.paths.keySet();
	}

	public Collection<? extends OperationObject> operations() {
		List<OperationObject> operations = new ArrayList<>();
		for (Entry<String, PathItemObject> path : swagger.paths.entrySet()) {
			PathItemObject value = path.getValue();
			operations.addAll(value.operations.values());
		}
		return operations;
	}

	public static Version getGeneratorVersion() {
		try {
			Enumeration<URL> resource = OpenAPIGenerator.class.getClassLoader().getResources("META-INF/MANIFEST.MF");
			while (resource.hasMoreElements()) {
				URL url = resource.nextElement();
				Manifest m = new Manifest(url.openStream());
				String bsn = m.getMainAttributes().getValue("Bundle-SymbolicName");
				if (bsn != null && bsn.startsWith("biz.aQute.openapi")) {
					String value = m.getMainAttributes().getValue("Bundle-Version");
					if (value != null) {
						return new Version(value);
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
