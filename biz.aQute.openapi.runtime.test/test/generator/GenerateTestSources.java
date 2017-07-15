package generator;

import java.io.File;
import java.io.InputStream;

import aQute.lib.io.IO;
import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;

public class GenerateTestSources {


	public static void non200status() throws Exception {
		GenerateTestSources.generate("non200status", "non200status.json", new Configuration());
	}

	public static void oauth2() throws Exception {
		GenerateTestSources.generate("oauth2", "oauth2.json", new Configuration());
	}

	public static void references() throws Exception {
		GenerateTestSources.generate("references", "references.json", new Configuration());
	}

	public static void primitives() throws Exception {
		GenerateTestSources.generate("primitives", "primitives.json", new Configuration());
	}

	public static void basicauth() throws Exception {
		GenerateTestSources.generate("basicauth", "basicauth.json", new Configuration());
	}

	public static void simple() throws Exception {
		GenerateTestSources.generate("simple", "simple.json", new Configuration());
	}

	public static void validation() throws Exception {
		GenerateTestSources.generate("validation", "validation.json", new Configuration());
	}

	public static void createNestedObjects() throws Exception {
		GenerateTestSources.generate("nestedobject", "nestedobject.json", new Configuration());
	}

	public static void createNakedEnum() throws Exception {
		GenerateTestSources.generate("nakedenum", "nakedenum.json", new Configuration());
	}

	public static void createCasing() throws Exception {
		GenerateTestSources.generate("casing", "casing.json", new Configuration());
	}

	public static void createEnums() throws Exception {
		GenerateTestSources.generate("enums", "enums.json", new Configuration());
	}


	static void createApiKeySecurity() throws Exception {
		Configuration c = new Configuration();
		generate("apikey", "apikey.json", c);
	}

	static void createDefaultDateTimeEncodingTest() throws Exception {
		Configuration c = new Configuration();
		generate("defaultdatetime", "datetimeencoding.json", c);
	}

	static void createDateTimeEncodingTest() throws Exception {
		Configuration c = new Configuration();
		c.dateFormat = "yyyy-DDD";
		c.dateTimeClass = "java.time.Instant";
		c.dateTimeFormat = "yyyy-MM-dd'T'HH:mm:ss[.SSS]X";
		generate("modifieddatetime", "datetimeencoding.json", c);
	}

	static void createInstantFormattingTest() throws Exception {
		Configuration c = new Configuration();
		c.dateTimeClass = "java.time.Instant";
		c.dateTimeFormat = "y";
		generate("instantformatting", "datetimeencoding.json", c);
	}

	static void createDateTimeEncodingTestWithManualConversion() throws Exception {
		Configuration c = new Configuration();
		c.dateTimeClass = "java.time.Instant";
		c.conversions = new String[] {
				"       DateTimeFormatter idtf = DateTimeFormatter.ofPattern(\"yyyy-MM-dd'T'HH:mm:ss[.SSS]X\").withZone(java.time.ZoneId.of(\"UTC\"));\n"
						+ "       CODEC.addStringHandler(Instant.class, (i) -> idtf.format(i), (s)-> Instant.from(idtf.parse(s)));\n"
						+ "" };
		generate("manualconversion", "datetimeencoding.json", c);
	}

	static void overrideInstantiationTest() throws Exception {
		Configuration c = new Configuration();
		generate("instantiation", "datetimeencoding.json", c);
	}

	static void parameterSourceTest() throws Exception {
		Configuration c = new Configuration();
		generate("parameters", "parameters.json", c);
	}

	public static void generate(String name, String file, Configuration c) throws Exception {
		System.out.println("*** " + name);
		c.packagePrefix = "gen." + name;
		c.typePrefix = Character.toUpperCase(name.charAt(0)) + name.substring(1);
		File output = IO.getFile("gen-sources");
		InputStream in = GenerateTestSources.class.getResourceAsStream(file);
		File tmp = File.createTempFile("openapi", ".json");
		try {
			IO.copy(in, tmp);
			OpenAPIGenerator g = new OpenAPIGenerator(tmp, c);
			g.generate(output);
		} finally {
			IO.delete(tmp);
		}
	}

	public static void main(String args[]) throws Exception {
		non200status();
		oauth2();
		references();
		primitives();
		basicauth();
		simple();
		validation();
		createNakedEnum();
		createNestedObjects();
		createCasing();
		createEnums();
		createApiKeySecurity();
		createDateTimeEncodingTest();
		createDefaultDateTimeEncodingTest();
		overrideInstantiationTest();
		parameterSourceTest();
		createDateTimeEncodingTestWithManualConversion();
		createInstantFormattingTest();
	}
}
