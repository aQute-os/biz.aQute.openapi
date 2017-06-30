package aQute.openapi.generator;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import aQute.openapi.v2.api.CollectionFormat;
import aQute.openapi.v2.api.ItemsObject;
import aQute.openapi.v2.api.SchemaObject;

public abstract class SourceType {

	final static IdentityHashMap<ItemsObject,SourceType>	types	= new IdentityHashMap<>();
	final static AtomicInteger								counter	= new AtomicInteger(1000);

	static final SourceType									VOID	= new VoidType();

	final int												id;
	final OpenAPIGenerator									gen;

	protected SourceType(OpenAPIGenerator gen) {
		this.id = counter.getAndIncrement();
		this.gen = gen;
	}

	public abstract String reference();

	public String conversion(String access, CollectionFormat collectionFormat) {
		return conversion(access);
	}

	public abstract String conversion(String access);

	public boolean hasValidator() {
		return false;
	}

	public void assertNotNumber(ItemsObject schema) {
		assert Double.isNaN(schema.maximum);
		assert Double.isNaN(schema.minimum);
		assert schema.exclusiveMaximum == false;
		assert schema.exclusiveMinimum == false;
	}

	public void assertNotArray(ItemsObject schema) {
		assert schema.uniqueItems == false;
		assert schema.collectionFormat == null;
		assert schema.items == null;
		assert schema.maxItems < 0;
		assert schema.minItems < 0;
	}

	public void assertNotString(ItemsObject schema) {
		assert schema.pattern == null;
		assert schema.maxLength < 0;
		assert schema.minLength < 0;
	}

	public void assertNotObject(ItemsObject schema) {
		if (schema instanceof SchemaObject) {
			assert ((SchemaObject) schema).properties == null;
		}
	}

	public void assertNoValidation(ItemsObject schema) {
		assertNotString(schema);
		assertNotArray(schema);
		assertNotObject(schema);
		assertNotNumber(schema);
	}

	public static SourceType getSourceType(OpenAPIGenerator gen, ItemsObject schema, String contextName) {
		if (schema == null)
			return SourceType.VOID;

		String n = getRefName(schema.$ref);
		if (n != null) {
			contextName = n;
		}

		if (types.containsKey(schema))
			return types.get(schema);

		if (schema.type == null) {
			schema.type = "object";
			gen.error("Missing type specification, assuming object");
		}

		if (schema.format == null)
			schema.format = "default";

		switch (schema.type) {
			case "integer" :
				switch (schema.format) {
					case "int32" :
						return new NummericType(gen, "int", schema, "Integer");

					default :
						gen.error("Unknon format for integer %s", schema.format);
					case "default" :
					case "int64" :
						return new NummericType(gen, "long", schema, "Long");
				}

			case "number" :
				switch (schema.format) {
					case "float" :
						return new DoubleType(gen, "float", schema, "Float");

					default :
					case "default" :
					case "double" :
						return new DoubleType(gen, "double", schema, "Double");

				}

			case "string" :

				switch (schema.format) {
					case "byte" :
						return new Base64Encoded(gen);

					case "binary" :
						return new BinaryType(gen, schema);

					case "date" :
						return new DateType(gen);

					case "date-time" :
						return new DateTimeType(gen, schema);

					case "password" :
						return new PasswordType(gen, schema);

					default :
					case "default" :
					case "string" :
						StringType stringType = new StringType(gen, schema);
						if (schema.$ref != null) {
							if (contextName != null) {
								SourceType enum1 = stringType.getEnum(contextName);
								if (enum1 != null) {
									return enum1;
								}
							}
						}
						return stringType;
				}

			case "boolean" :
				return new BooleanType(gen, schema);

			case "object" :
				if ("object".equals(schema.type) && schema instanceof SchemaObject) {
					return object(gen, (SchemaObject) schema, contextName);
				}
				return VOID;

			case "array" :
				return array(gen, schema, contextName);

			case "file" :
				return new FileType(gen, schema);
		}

		return VOID;
	}

	private static String getRefName(String ref) {
		if (ref == null) {
			return null;
		}
		int n = ref.lastIndexOf('/');
		String name = ref.substring(n + 1);

		return name;
	}

	static class VoidType extends SimpleType {
		final SourceType wrapper;

		protected VoidType() {
			super(null, "void", null);
			this.wrapper = new SimpleType(null, "Void", null) {
				@Override
				public boolean isVoid() {
					return true;
				}

				@Override
				public String conversion(String access) {
					return null;
				}
			};
		}

		@Override
		public boolean isVoid() {
			return true;
		}

		@Override
		public String reference() {
			return "void";
		}

		@Override
		public String conversion(String access) {
			return null;
		}

		@Override
		public SourceType wrapper() {
			return wrapper;
		}
	}

	static class Base64Encoded extends SourceType {

		protected Base64Encoded(OpenAPIGenerator gen) {
			super(gen);
		}

		@Override
		public String reference() {
			return "byte[]";
		}

		@Override
		public String conversion(String name) {
			return String.format("toBase64Encoded(%s)", name);
		}

	}

	static class DateType extends SourceType {
		protected DateType(OpenAPIGenerator gen) {
			super(gen);
		}

		@Override
		public String reference() {
			return "LocalDate";
		}

		@Override
		public String conversion(String name) {
			return String.format("toDate(%s)", name);
		}
	}

	static class BooleanType extends SimpleType {
		SourceType wrapper;

		public BooleanType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen, "boolean", schema);
			wrapper = new BooleanType(gen, "Boolean", schema);
		}

		BooleanType(OpenAPIGenerator gen, String string, ItemsObject schema) {
			super(gen, string, schema);
			wrapper = this;
		}

		@Override
		public String conversion(String name) {
			return String.format("toBoolean(%s)", name);
		}

		public SourceType wrapper() {
			return wrapper;
		}
	}

	static class BinaryType extends SimpleType {

		public BinaryType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen, "binary", schema);
		}

		@Override
		public String reference() {
			return "byte[]";
		}

		@Override
		public String conversion(String name) {
			return String.format("toBinary(%s)", name);
		}

	}

	static class DateTimeType extends SimpleType {

		public DateTimeType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen, "date-time", schema);
		}

		@Override
		public String reference() {
			return gen.getDateTimeClass();
		}

		/**
		 * GeneratedBase.to
		 */
		@Override
		public String conversion(String name) {
			return String.format("^%s.toDateTime(%s)", gen.getBaseSourceFile().getFQN(), name);
		}

	}

	static class PasswordType extends StringType {

		public PasswordType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen, schema);
		}

		@Override
		public String reference() {
			return "char[]";
		}

		@Override
		public String conversion(String name) {
			return String.format("toPassword(%s)", name);
		}
	}

	public static class NummericType extends SimpleType {
		final NummericType wrapper;

		public NummericType(OpenAPIGenerator gen, String name, ItemsObject schema) {
			this(gen, name, schema, (NummericType) null);
		}

		public NummericType(OpenAPIGenerator gen, String name, ItemsObject schema, String wrapper) {
			super(gen, name, schema);
			this.wrapper = new NummericType(gen, wrapper, schema);
		}

		public NummericType(OpenAPIGenerator gen, String name, ItemsObject schema, NummericType wrapper) {
			super(gen, name, schema);
			this.wrapper = wrapper == null ? this : wrapper;
		}

		@Override
		public boolean hasValidator() {
			return !Double.isNaN(getSchema().minimum) || !Double.isNaN(getSchema().maximum)
					|| getSchema().multipleOf > 0 || (getSchema().enum$ != null && !getSchema().enum$.isEmpty());
		}

		@Override
		public SourceType wrapper() {
			return wrapper;
		}

		@Override
		public String reference() {
			return getName();
		}

		@Override
		public String conversion(String variable) {
			return String.format("to%s(%s)",
					Character.toUpperCase(this.getName().charAt(0)) + this.getName().substring(1), variable);
		}

		public String toString(double v) {
			if ((v % 1) != 0) {
				OpenAPIGenerator.getLogger()
						.warn("A minimum/maximum for an integer value in {} is not an int but is {}", this, v);
			}
			return Long.toString(Math.round(v));
		}
	}

	static class DoubleType extends NummericType {

		public DoubleType(OpenAPIGenerator gen, String name, ItemsObject schema, String wrapper) {
			super(gen, name, schema, new DoubleType(gen, wrapper, schema));
		}

		public DoubleType(OpenAPIGenerator gen, String wrapper, ItemsObject schema) {
			super(gen, wrapper, schema);
		}

		public String toString(double v) {
			return Double.toString(v);
		}
	}

	static class StringType extends SimpleType {

		public StringType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen, "String", schema);

			assertNotNumber(schema);
			assertNotArray(schema);

		}

		@Override
		public boolean hasValidator() {
			return getSchema().pattern != null || getSchema().minLength > 0 || getSchema().maxLength >= 0;
		}

		@Override
		public String reference() {
			return "String";
		}

		@Override
		public String conversion(String name) {
			return String.format("toString(%s)", name);
		}

		@Override
		public SourceType getEnum(String typeName) {
			if (getSchema().__extra != null && getSchema().__extra.containsKey("enum")) {
				// TODO fix, json should support reserved names
				getSchema().enum$ = (List<Object>) getSchema().__extra.get("enum");
			}
			if (getSchema().enum$ == null || getSchema().enum$.isEmpty()) {
				return null;
			}

			return new StringEnumType(gen, typeName, getSchema());

		}
	}

	public static class StringEnumType extends SimpleType {

		StringEnumType(OpenAPIGenerator gen, String typeName, ItemsObject schema) {
			super(gen, typeName, schema);
		}

		@Override
		public String conversion(String access) {
			return String.format("toEnumMember(%s.class,%s)", reference(), getName());
		}
	}

	public static class ArrayType extends SourceType {
		private ItemsObject	schema;
		private SourceType	componentType;
		private Boolean		hasValidator;
		private String		contextName;

		public ArrayType(OpenAPIGenerator gen, ItemsObject schema, String name) {
			super(gen);
			this.contextName = name;
			this.setSchema(schema);
		}

		public void build() {
			setComponentType(getSourceType(gen, getSchema().items, contextName));
		}

		@Override
		public void addTypes(SourceFile sourceFile) {
			sourceFile.addType(getComponentType());
		}

		@Override
		public boolean hasValidator() {
			if (hasValidator == null) {
				hasValidator = Boolean.FALSE;
				hasValidator = getSchema().maxItems > 0 || getSchema().minItems > 0
						|| getComponentType().hasValidator();
			}

			return hasValidator;
		}

		@Override
		public String asReturnType() {
			if (getComponentType().isPrimitive())
				return reference();

			return String.format("Iterable<? extends %s>", getComponentType().reference());
		}

		@Override
		public String reference() {
			return "List<" + getComponentType().reference() + ">";
		}

		@Override
		public String conversion(String variable) {
			return String.format("toArray(%s.class, %s)", getComponentType().reference(), variable);
		}

		@Override
		public boolean isArray() {
			return true;
		}

		@Override
		public SourceType getEnum(String typeName) {
			SourceType enum1 = getComponentType().getEnum(typeName);
			if (enum1 == null)
				return null;

			ArrayType arrayType = new ArrayType(gen, getSchema(), typeName);
			arrayType.setComponentType(enum1);
			return arrayType;
		}

		public ItemsObject getSchema() {
			return schema;
		}

		public void setSchema(ItemsObject schema) {
			this.schema = schema;
		}

		public SourceType getComponentType() {
			return componentType;
		}

		public void setComponentType(SourceType componentType) {
			this.componentType = componentType;
		}
	}

	public static abstract class SimpleType extends SourceType {

		private final ItemsObject	schema;
		private final String		name;

		public SimpleType(OpenAPIGenerator gen, String name, ItemsObject schema) {
			super(gen);
			this.schema = schema;
			this.name = name;
		}

		@Override
		public String reference() {
			return getName();
		}

		public String getName() {
			return name;
		}

		public ItemsObject getSchema() {
			return schema;
		}

	}

	static class FileType extends SimpleType {

		public FileType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen, "OpenAPIBase.Part", schema);
		}

		@Override
		public String reference() {
			return "OpenAPIBase.Part";
		}

		@Override
		public String conversion(String name) {
			return null;
		}

	}

	public static class ObjectType extends SourceType {
		private static int					index		= 1000;
		private final SchemaObject			schema;
		private final String				className;
		final Map<String,SourceProperty>	properties	= new HashMap<>();
		private Boolean						hasValidator;

		ObjectType(OpenAPIGenerator gen, SchemaObject schema, String contextName) {
			super(gen);
			this.schema = schema;
			this.className = gen.toTypeName(contextName);
		}

		void build() {
			doProperties(getSchema(), this.getSchema().properties, true);
		}

		private void doProperties(SchemaObject schema, Map<String,SchemaObject> properties, boolean mandatory) {
			if (schema.properties == null)
				return;

			List<String> required = schema.required == null ? Collections.emptyList() : schema.required;

			for (Entry<String,SchemaObject> e : schema.properties.entrySet()) {
				SourceType type = SourceType.getSourceType(gen, e.getValue(), e.getKey());

				boolean optional = !required.contains(e.getKey());

				if (optional) {
					type = new OptionalType(type);
				}

				// It is hard to decide for a name for an enum type so they
				// are generally represented as strings. However, if a type
				// has enums and can be converted to a Java enum then we do
				// that.

				SourceType enumType = type.getEnum(gen.toTypeName(e.getKey() + "Enum"));
				if (enumType != null) {
					type = enumType;
				}

				String memberName = gen.toMemberName(e.getKey());
				SourceProperty property = new SourceProperty(gen, memberName, type);

				this.properties.put(e.getKey(), property);
			}
		}

		String toClassName(String ref) {
			if (ref == null) {
				OpenAPIGenerator.getLogger().info("No ref for a type ");
				return "Anonymous_" + index++;
			}
			int n = ref.lastIndexOf('/');
			String name = ref.substring(n + 1);
			return gen.toTypeName(name);
		}

		public void addTypes(SourceFile sourceFile) {
			for (SourceProperty property : properties.values()) {
				sourceFile.addType(property.getType());
			}
		}

		@Override
		public boolean hasValidator() {
			if (hasValidator == null) {
				hasValidator = Boolean.FALSE;
				hasValidator = properties.values()
						.stream()
						.filter(p -> p.getType().hasValidator())
						.findAny()
						.isPresent();
			}
			return hasValidator;
		}

		@Override
		public String reference() {
			return getClassName();
		}

		@Override
		public String conversion(String name) {
			return String.format("to(%s.class,%s)", getClassName(), name);
		}

		@Override
		public boolean isObject() {
			return true;
		}

		public Collection<SourceProperty> getProperties() {
			return properties.values();
		}

		public String getClassName() {
			return className;
		}

		public SchemaObject getSchema() {
			return schema;
		}

	}

	private static SourceType object(OpenAPIGenerator gen, SchemaObject schema, String contextName) {
		ObjectType objectType = new ObjectType(gen, schema, contextName);
		types.put(schema, objectType);
		objectType.build();
		return objectType;
	}

	public SourceType getEnum(String typeName) {
		return null;
	}

	public boolean isPrimitive() {
		return this != wrapper();
	}

	private static SourceType array(OpenAPIGenerator gen, ItemsObject schema, String contextName) {
		ArrayType arrayType = new ArrayType(gen, schema, contextName);
		types.put(schema, arrayType);
		arrayType.build();
		return arrayType;
	}

	public void addTypes(SourceFile sourceFile) {}

	public boolean isVoid() {
		return false;
	}

	public SourceType wrapper() {
		return this;
	}

	public boolean isArray() {
		return false;
	}

	public String asReturnType() {
		return reference();
	}

	@Override
	public String toString() {
		return reference();
	}

	public boolean isObject() {
		return false;
	}

	public static class OptionalType extends SourceType {

		private SourceType target;

		OptionalType(SourceType parent) {
			super(parent.gen);
			this.target = parent;
		}

		@Override
		public SourceType getEnum(String typeName) {
			SourceType enum1 = target.getEnum(typeName);
			if (enum1 == null)
				return null;

			return new OptionalType(enum1);
		}

		@Override
		public String reference() {
			return "Optional<" + target.wrapper().reference() + ">";
		}

		@Override
		public String conversion(String name) {
			return target.conversion(name + ".get()");
		}

		public void addTypes(SourceFile sourceFile) {
			sourceFile.addType(target);
			target.addTypes(sourceFile);
		}

		public boolean isOptional() {
			return true;
		}

		public SourceType getTarget() {
			return target;
		}

		public boolean hasValidator() {
			return target.hasValidator();
		}
	}

	public boolean isOptional() {
		return false;
	}
}
