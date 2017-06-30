package aQute.openapi.java.generator;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Formatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

import aQute.lib.converter.Converter;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.io.IO;

public class BaseSourceGenerator {
	private Formatter	formatter;
	private final File	output;

	public BaseSourceGenerator(File output) {
		this.output = output;
	}

	protected void generate(String path, Runnable body) throws FileNotFoundException, UnsupportedEncodingException {
		File target = IO.getFile(getOutput(), path);
		target.getParentFile().mkdirs();
		try (Formatter formatter = new Formatter(target, StandardCharsets.UTF_8.toString());) {
			this.setFormatter(formatter);
			body.run();
		}
		this.setFormatter(null);
	}

	protected void doImport(String imp) {
		getFormatter().format("import %s;\n", imp);
	}

	protected void doImport(Class< ? > c) {
		doImport(c.getName());
	}

	protected void doImports(Collection<String> imports) {
		for (String imp : imports) {
			doImport(imp);
		}
	}

	protected void doPackage(String packageName) {
		getFormatter().format("package %s;\n\n", packageName);
	}

	protected void doLicense(String license) {
		copy(license);
	}

	public interface CommentBuilder extends Closeable {
		CommentBuilder para(String text);

		CommentBuilder throws_(String name, String description);

		CommentBuilder retrn(String responseCode, String description);

		CommentBuilder param(String name, String description);

		void close();

		CommentBuilder see(URI uri);

		CommentBuilder visit(Consumer<CommentBuilder> consumer);
	}

	protected CommentBuilder comment() {
		format("/**\n * \n");
		return new CommentBuilder() {

			@Override
			public CommentBuilder para(String text) {
				if (text != null)
					format(" * %s\n * \n", text);
				return this;
			}

			@Override
			public void close() {
				format(" */\n\n");
			}

			@Override
			public CommentBuilder see(URI url) {
				if (url != null)
					format(" * @see %s\n * \n", url);
				return this;
			}

			@Override
			public CommentBuilder param(String name, String description) {
				if (name != null) {
					if (description == null)
						format(" * @param %s\n * \n", name);
					else
						format(" * @param %s – %s\n * \n", name, description);
				}
				return this;
			}

			@Override
			public CommentBuilder throws_(String responseCode, String description) {
				format("   * @throws Response %s / %s\n", responseCode, description);
				return this;
			}

			@Override
			public CommentBuilder retrn(String responseCode, String description) {
				format("   * @returns %s / %s\n", responseCode, description);
				return this;
			}

			@Override
			public CommentBuilder visit(Consumer<CommentBuilder> consumer) {
				consumer.accept(this);
				return this;
			}
		};

	}

	protected void format(String format, Object... args) {
		getFormatter().format(format, args);
	}

	protected void copy(String string) {
		if (string == null)
			return;

		format("%s", string);
	}

	protected void doClass(Runnable classBody, int modifiers, String typeName, String superName,
			String... interfaceNames) {

		format("%s ", Modifier.toString(modifiers));
		format("class %s ", typeName);
		if (superName != null)
			format("extends %s ", superName);

		if (interfaceNames.length > 0) {
			format("implements ");
			for (String interfaceName : interfaceNames) {
				format("%s ", interfaceName);
			}
		}
		format("{\n\n");
		classBody.run();
		format("}\n\n");
	}

	protected String getOrDefault(String value, String deflt) {
		return value == null ? deflt : value;
	}

	protected void doConstant(int model, String type, String name, String value) {
		format("%s %s %s = \"%s\";\n\n", Modifier.toString(model), type, name, value);
	}

	public interface AnnotationBuilder extends Closeable {
		AnnotationBuilder attribute(String name, String type, String deflt);

		void close();
	}

	static class Attribute {
		String	type;
		String	deflt;
	}

	protected AnnotationBuilder doAnnotationInterface(int modifiers, String name) {
		Map<String,Attribute> attributes = new LinkedHashMap<>();

		return new AnnotationBuilder() {

			@Override
			public void close() {
				format("%s @interface %s {\n", Modifier.toString(modifiers), name);
				for (Map.Entry<String,Attribute> e : attributes.entrySet()) {
					String name = e.getKey();
					Attribute attribute = e.getValue();
					format("%s %s()", attribute.type, name);
					if (attribute.deflt != null) {
						format(" default %s", attribute.deflt);
					}
					format(";");
				}
				format("}\n");
			}

			@Override
			public AnnotationBuilder attribute(String name, String type, String deflt) {
				Attribute attr = new Attribute();
				attr.deflt = deflt;
				attr.type = type;
				attributes.put(name, attr);
				return this;
			}

		};
	}

	protected interface Annotate<T> extends AutoCloseable {
		T get();

		Annotate<T> set(Object ignore, String value);

		void close();

		Annotate<T> setQuoted(String ignore, String value);
	}

	protected <T> Annotate<T> annotate(Class<T> type) {
		AtomicReference<Method> last = new AtomicReference<>();

		@SuppressWarnings("unchecked")
		T instance = (T) Proxy.newProxyInstance(type.getClassLoader(), new Class[] {
				type
		}, new InvocationHandler() {

			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				last.set(method);
				return Converter.cnv(method.getReturnType(), null);
			}
		});

		Map<String,String> variables = new LinkedHashMap<>();

		return new Annotate<T>() {

			@Override
			public T get() {
				return instance;
			}

			@Override
			public Annotate<T> set(Object ignore, String value) {
				variables.put(last.get().getName(), value);
				return this;
			}

			@Override
			public Annotate<T> setQuoted(String ignore, String value) {
				variables.put(last.get().getName(), escapeString(value));
				return this;
			}

			@Override
			public void close() {
				try {
					switch (variables.size()) {
						case 0 :
							format("@%s\n", type.getSimpleName());
							break;
						case 1 :
							Map.Entry<String,String> onlyOne = variables.entrySet().iterator().next();
							if (onlyOne.getKey().equals("value")) {
								format("@%s(%s)\n", type.getSimpleName(), onlyOne.getValue());
								break;
							}
							// fall through
						default :
							format("@%s(", type.getSimpleName());
							String del = "";
							for (Map.Entry<String,String> e : variables.entrySet()) {
								format("%s%s=%s", del, e.getKey(), e.getValue());
								del = ", ";
							}
							format(")\n");
					}
				} catch (Exception e) {
					Exceptions.duck(e);
				}
			}

		};
	}

	public interface MethodBuilder {
		MethodBuilder parameter(String type, String name);

		MethodBuilder throws_(String type);

		void body(Runnable r);

		void noBody();
	}

	protected MethodBuilder doMethod(int modifiers, String returnType, String name) {
		Map<String,String> parameters = new LinkedHashMap<>();
		List<String> exceptions = new ArrayList<>();

		return new MethodBuilder() {

			@Override
			public MethodBuilder parameter(String type, String name) {
				parameters.put(name, type);
				return this;
			}

			@Override
			public MethodBuilder throws_(String type) {
				exceptions.add(type);
				return this;
			}

			@Override
			public void body(Runnable r) {
				bodyProlog();
				format("{\n\n");

				r.run();
				format("\n}\n\n");
			}

			private void bodyProlog() {
				format("%s %s %s(", Modifier.toString(modifiers), returnType, name);
				String del = "";
				for (Map.Entry<String,String> par : parameters.entrySet()) {
					format("%s%s %s", del, par.getValue(), par.getKey());
					del = ", ";
				}
				format(")");

				del = " throws ";
				for (String exc : exceptions) {
					format("%s%s", del, exc);
					del = ", ";
				}
			}

			@Override
			public void noBody() {
				bodyProlog();
				format(";\n\n");
			}
		};
	}

	public static String escapeString(String prefix, String middle, String suffix, Iterable< ? > it) {
		StringBuilder sb = new StringBuilder();
		String del = prefix;
		for (Object o : it) {
			sb.append(del).append(escapeString(o));
			del = middle;
		}
		if (del == middle)
			sb.append(suffix);

		return sb.toString();
	}

	public static String escapeString(Object string) {
		if (string == null)
			return null;

		String s = string.toString();

		StringBuilder sb = new StringBuilder();
		sb.append("\"");
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '\\' :
					sb.append("\\\\");
					break;
				case '"' :
					sb.append("\\\"");
					break;
				case '\n' :
					sb.append("\\n");
					break;
				case '\r' :
					sb.append("\\r");
					break;
				case '\t' :
					sb.append("\\t");
					break;
				case '\f' :
					sb.append("\\f");
					break;
				case '\b' :
					sb.append("\\b");
					break;

				default :
					sb.append(c);
					break;
			}

		}
		sb.append("\"");
		return sb.toString();
	}

	public Formatter getFormatter() {
		return formatter;
	}

	public void setFormatter(Formatter formatter) {
		this.formatter = formatter;
	}

	public File getOutput() {
		return output;
	}

}
