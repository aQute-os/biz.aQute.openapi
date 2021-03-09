package biz.aQute.openapi.validator;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import aQute.lib.io.IO;
import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;

public class GeneratorTest {
	@Rule
	public TemporaryFolder tmp = new TemporaryFolder();

	@Test
	public void testDateGeneration() throws Exception {
		Configuration c = new Configuration();
		c.dateFormat = "yyyy-DDD";
		File f = IO.getFile("resources/gen/modifieddatetime.json");
		OpenAPIGenerator g = new OpenAPIGenerator(f, c);
		File out = tmp.newFolder();
		g.generate(out);
		System.out.println(out.list());
	}

	@Test
	public void testActualType() throws Exception {
		Configuration c = new Configuration();
		File f = IO.getFile("resources/gen/actualtype.json");
		OpenAPIGenerator g = new OpenAPIGenerator(f, c);
		File out = tmp.newFolder();
		g.generate(out);
		File base = IO.getFile(out, "org/example/openapi/GeneratedBase.java");
		String content = IO.collect(base);
		assertThat(content).contains("protected abstract biz.aQute.openapi.validator.Foo references()");
	}
}
