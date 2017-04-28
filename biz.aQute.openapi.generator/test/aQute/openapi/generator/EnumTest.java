package aQute.openapi.generator;

import java.io.File;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import aQute.lib.io.IO;

public class EnumTest extends Assert {

	class EnumImpl extends test.enums.GeneratedBase {

		@Override
		protected void enum$(AllEnumVariations body) throws Exception {
			assertTrue(body.simple instanceof SimpleEnum);
			assertTrue(body.array instanceof List);
		}

	}

	@Test
	public void testSimple() throws Exception {
		File file = IO.getFile("test/aQute/openapi/generator/files/enums.json");
		Configuration c = new Configuration();
		File output = IO.getFile("gen-sources");
		c.packagePrefix = "test.enums";
		OpenAPIGenerator g = new OpenAPIGenerator(file, c);
		g.report(System.out);
		g.generate(output);


	}
}
