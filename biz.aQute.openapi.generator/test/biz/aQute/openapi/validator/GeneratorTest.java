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
		assertThat(content).contains(
				"protected abstract biz.aQute.openapi.validator.Foo references(biz.aQute.openapi.validator.Foo request) throws Exception;");
		assertThat(content).doesNotContain("request_.validate(context, \"request_\");");
	}
	@Test
	public void testMissingReference() throws Exception {
		Configuration c = new Configuration();
		File f = IO.getFile("resources/gen/missing-reference.json");
		OpenAPIGenerator g = new OpenAPIGenerator(f, c);
		assertThat(g.check("Ref to #/definitions/Missing")).isTrue();
	}

	@Test
	public void testValidationCompleteness() throws Exception {
		Configuration c = new Configuration();
		File f = IO.getFile("resources/gen/validate_require.json");
		OpenAPIGenerator g = new OpenAPIGenerator(f, c);
		File out = tmp.newFolder();
		g.generate(out);
		File base = IO.getFile(out, "org/example/openapi/demo/GeneratedDemo.java");
		String collect = IO.collect(base);
		assertThat(collect).contains("context.require");
		assertThat(collect).contains("validate(context");
		System.out.println(collect);
	}

	@Test
	public void testNoRequirements() throws Exception {
		Configuration c = new Configuration();
		c.option("norequirement");
		File f = IO.getFile("resources/gen/validate_require.json");
		OpenAPIGenerator g = new OpenAPIGenerator(f, c);
		File out = tmp.newFolder();
		g.generate(out);
		File base = IO.getFile(out, "org/example/openapi/demo/GeneratedDemo.java");
		String collect = IO.collect(base);
		assertThat(collect).doesNotContain("context.require");
		assertThat(collect).contains("validate(context");
		System.out.println(collect);
	}

	@Test
	public void testNoValidationAndRequirement() throws Exception {
		Configuration c = new Configuration();
		c.option("norequirement");
		c.option("novalidation");
		File f = IO.getFile("resources/gen/validate_require.json");
		OpenAPIGenerator g = new OpenAPIGenerator(f, c);
		File out = tmp.newFolder();
		g.generate(out);
		File base = IO.getFile(out, "org/example/openapi/demo/GeneratedDemo.java");
		String collect = IO.collect(base);
		assertThat(collect).doesNotContain("context.require");
		assertThat(collect).doesNotContain("validate(context");
		System.out.println(collect);
	}
}
