package biz.aQute.openapi.generate.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.util.List;

import org.junit.Test;

import aQute.bnd.osgi.Processor;
import aQute.lib.io.IO;
import aQute.lib.specinterface.SpecInterface;
import aQute.lib.strings.Strings;

public class GeneratorTest {

	@Test
	public void runOpenAPIGeneration() throws Exception {
		File gen = IO.getFile("generated/gen");
		IO.delete(gen);
		gen.mkdirs();
		File testproject = IO.getFile("../biz.aQute.openapi.runtime.test").getCanonicalFile();
		assertThat(testproject).isDirectory();
		
		List<String> arguments = Strings.splitQuoted("openapi -a -o generated/gen openapi/"," \t");
		SpecInterface<OpenAPIOptions> spec = SpecInterface.getOptions(OpenAPIOptions.class, arguments, IO.work);
		assertThat(spec.isFailure()).isFalse();

		OpenAPI	g = new OpenAPI();
		Processor p = new Processor();
		p.setBase(testproject);
		g.generate(p, spec.instance());
		
		assertThat(p.check()).isTrue();
	}
}
