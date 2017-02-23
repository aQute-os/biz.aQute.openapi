package aQute.openapi.generator;

import java.util.Collection;
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

	final static IdentityHashMap<ItemsObject, SourceType>	types	= new IdentityHashMap<>();
	final static AtomicInteger								counter	= new AtomicInteger(
			1000);

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

	public static SourceType getSourceType(OpenAPIGenerator gen,
			ItemsObject schema) {
		if (schema == null)
			return SourceType.VOID;

		if (types.containsKey(schema))
			return types.get(schema);

		if (schema.type == null) {
			schema.type = "object";
			gen.error("Missing type specification, assuming object");
		}

		if (schema.format == null)
			schema.format = "default";

		switch (schema.type) {
		case "integer":
			switch (schema.format) {
			case "int32":
				return new NummericType(gen, "int", schema, "Integer");

			default:
				gen.error("Unknon format for integer %s", schema.format);
			case "default":
			case "int64":
				return new NummericType(gen, "long", schema, "Long");
			}

		case "number":
			switch (schema.format) {
			case "float":
				return new DoubleType(gen, "float", schema, "Float");

			default:
			case "default":
			case "double":
				return new DoubleType(gen, "double", schema, "Double");

			}

		case "string":
			switch (schema.format) {
			case "byte":
				return new Base64Encoded(gen);

			case "binary":
				return new BinaryType(gen, schema);

			case "date":
				return new DateType(gen);

			case "date-time":
				return new DateTimeType(gen, schema);

			case "password":
				return new PasswordType(gen, schema);

			default:
			case "default":
			case "string":
				return new StringType(gen, schema);
			}

		case "boolean":
			return new BooleanType(gen, schema);

		case "object":
			if ("object".equals(schema.type) && schema instanceof SchemaObject) {
				return object(gen, (SchemaObject) schema);
			}
			return VOID;

		case "array":
			return array(gen, schema);

		case "file":
			return new FileType(gen, schema);
		}

		return VOID;
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
			return "java.util.LocalDate";
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
			return "java.time.OffsetDateTime";
		}

		@Override
		public String conversion(String name) {
			return String.format("toDateTime(%s)", name);
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

	static class NummericType extends SimpleType {
		final NummericType wrapper;

		public NummericType(OpenAPIGenerator gen, String name,
				ItemsObject schema) {
			super(gen, name, schema);
			wrapper = this;
		}

		public NummericType(OpenAPIGenerator gen, String name,
				ItemsObject schema, String wrapper) {
			super(gen, name, schema);
			this.wrapper = new NummericType(gen, wrapper, schema);
		}

		@Override
		public boolean hasValidator() {
			return !Double.isNaN(schema.minimum)
					|| !Double.isNaN(schema.maximum) || schema.multipleOf > 0
					|| (schema.enum$ != null && !schema.enum$.isEmpty());
		}

		@Override
		public SourceType wrapper() {
			return wrapper;
		}


		@Override
		public String reference() {
			return name;
		}

		@Override
		public String conversion(String variable) {
			return String.format("to%s(%s)",
					Character.toUpperCase(this.name.charAt(0))
							+ this.name.substring(1),
					variable);
		}

		protected String toString(double v) {
			if ((v % 1) != 0) {
				OpenAPIGenerator.logger.warn(
						"A minimum/maximum for an integer value in {} is not an int but is {}",
						this, v);
			}
			return Long.toString(Math.round(v));
		}
	}

	static class DoubleType extends NummericType {

		public DoubleType(OpenAPIGenerator gen, String name, ItemsObject schema,
				String string) {
			super(gen, name, schema);
		}

		protected String toString(double v) {
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
			return schema.pattern != null || schema.minLength > 0
					|| schema.maxLength >= 0;
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
			if ( schema.__extra != null && schema.__extra.containsKey("enum")) {
				schema.enum$ = (List<Object>) schema.__extra.get("enum");
			}
			if ( schema.enum$ == null || schema.enum$.isEmpty()) {
				return null;
			}
			
			return new StringEnumType(gen,typeName,schema);
			
		}
	}
	
	static class StringEnumType extends SimpleType {

		StringEnumType(OpenAPIGenerator gen, String typeName, ItemsObject schema) {
			super(gen,typeName,schema);
		}
		
		@Override
		public String conversion(String access) {
			return String.format("toEnumMember(%s.class,%s)", reference(), name);
		}
	}
	
	static class ArrayType extends SourceType {
		ItemsObject	schema;
		SourceType	componentType;
		private Boolean		hasValidator;

		public ArrayType(OpenAPIGenerator gen, ItemsObject schema) {
			super(gen);
			this.schema = schema;
		}

		public void build() {
			componentType = getSourceType(gen, schema.items);
		}

		@Override
		public void addTypes(SourceFile sourceFile) {
			sourceFile.addType(componentType);
		}

		@Override
		public boolean hasValidator() {
			if (hasValidator == null) {
				hasValidator = Boolean.FALSE;
				hasValidator = schema.maxItems > 0 || schema.minItems > 0
						|| componentType.hasValidator();
			}

			return hasValidator;
		}

		@Override
		public String asReturnType() {
			if (componentType.isPrimitive())
				return reference();

			return String.format("Iterable<? extends %s>",
					componentType.reference());
		}


		@Override
		public String reference() {
			return componentType.reference() + "[]";
		}

		@Override
		public String conversion(String variable) {
			return String.format("toArray(%s.class, %s)",
					componentType.reference(), variable);
		}

		@Override
		public boolean isArray() {
			return true;
		}
		
		@Override
		public SourceType getEnum( String typeName) { 
			SourceType enum1 = componentType.getEnum(typeName);
			if ( enum1 == null)
				return null;
			
			ArrayType arrayType = new ArrayType(gen, schema);
			arrayType.componentType = enum1;
			return arrayType;
		}
	}

	static abstract class SimpleType extends SourceType {

		final ItemsObject	schema;
		final String		name;

		public SimpleType(OpenAPIGenerator gen, String name,
				ItemsObject schema) {
			super(gen);
			this.schema = schema;
			this.name = name;
		}

		@Override
		public String reference() {
			return name;
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

	static class ObjectType extends SourceType {
		private static int					index		= 1000;
		final SchemaObject					schema;
		final String						className;
		final Map<String, SourceProperty>	properties	= new HashMap<>();
		private Boolean						hasValidator;

		ObjectType(OpenAPIGenerator gen, SchemaObject schema) {
			super(gen);
			this.schema = schema;
			this.className = toClassName(this.schema.$ref);
		}

		public void build() {
			doProperties(schema, this.schema.properties, true);
		}

		private void doProperties(SchemaObject schema,
				Map<String, SchemaObject> properties, boolean mandatory) {
			if (schema.properties == null)
				return;

			for (Entry<String, SchemaObject> e : schema.properties.entrySet()) {
				SourceType type = SourceType.getSourceType(gen, e.getValue());
				
				// It is hard to decide for a name for an enum type so they 
				// are generally represented as strings. However, if a type
				// has enums and can be converted to a Java enum then we do
				// that.
				
				SourceType enumType = type.getEnum( gen.toTypeName(e.getKey() + "Enum"));
				if ( enumType != null) {
					type = enumType;
				}
				
				String memberName = gen.toMemberName(e.getKey());
				SourceProperty property = new SourceProperty(gen, memberName,
						type);
				
				this.properties.put(e.getKey(), property);
			}
		}

		private String toClassName(String ref) {
			if (ref == null) {
				OpenAPIGenerator.logger.info("No ref for a type ");
				return "Anonymous_" + index++;
			}
			int n = ref.lastIndexOf('/');
			String name = ref.substring(n + 1);
			return gen.toTypeName(name);
		}

		public void addTypes(SourceFile sourceFile) {
			for (SourceProperty property : properties.values()) {
				sourceFile.addType(property.type);
			}
		}

		@Override
		public boolean hasValidator() {
			if (hasValidator == null) {
				hasValidator = Boolean.FALSE;
				hasValidator = properties.values().stream()
						.filter(p -> p.type.hasValidator()).findAny()
						.isPresent();
			}
			return hasValidator;
		}


		@Override
		public String reference() {
			return className;
		}

		@Override
		public String conversion(String name) {
			return String.format("to(%s.class,%s)", className, name);
		}

		@Override
		public boolean isObject() {
			return true;
		}

		public Collection<SourceProperty> getProperties() {
			return properties.values();
		}

	}

	private static SourceType object(OpenAPIGenerator gen,
			SchemaObject schema) {
		ObjectType objectType = new ObjectType(gen, schema);
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

	private static SourceType array(OpenAPIGenerator gen, ItemsObject schema) {
		ArrayType arrayType = new ArrayType(gen, schema);
		types.put(schema, arrayType);
		arrayType.build();
		return arrayType;
	}

	public void addTypes(SourceFile sourceFile) {
	}

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
}
