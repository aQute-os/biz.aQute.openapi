package aQute.openapi.generator;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import aQute.lib.io.IO;
import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;
import junit.framework.TestCase;

public class GenTest extends TestCase {
	File tmp = IO.getFile("test");

	public void setUp() {
		tmp.mkdirs();
	}

	public void testAll() throws Exception {
		File files = IO.getFile("src/test/java/aQute/openapi/generator/files");
		Configuration c = new Configuration();
		File output = IO.getFile("target/gen-sources");
		for (File f : files.listFiles()) {
			if (f.getName().endsWith(".json")) {
				System.out.println("______________________________________________________");
				System.out.println("        " + f);
				System.out.println("______________________________________________________");
				String pack = f.getName().substring(0, f.getName().length() - 5);
				try (InputStream in = new FileInputStream(f);) {
					c.packagePrefix = pack;
					OpenAPIGenerator g = new OpenAPIGenerator(in,
							c);
					g.report(System.out);
					g.generate(output);
				}
			}
		}
	}

	public void testOne() throws Exception {
		File file = IO.getFile("src/test/java/aQute/openapi/generator/files/de.sma.igana.rest.api.v1.json");
		Configuration c = new Configuration();
		File output = IO.getFile("target/gen-sources");
		String pack = file.getName().substring(0, file.getName().length() - 5);
		try (InputStream in = new FileInputStream(file);) {
			c.packagePrefix = pack;
			c.tags = new String[]{"AccessTokenApi:*", "*:OnlyOne", "TermsOfUse:!"};
			OpenAPIGenerator g = new OpenAPIGenerator(in,
					c);
			g.report(System.out);
			g.generate(output);
		}
	}
}
