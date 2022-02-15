package biz.aQute.openapi.generate.plugin;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import aQute.bnd.osgi.Processor;
import aQute.bnd.service.externalplugin.ExternalPlugin;
import aQute.bnd.service.generate.BuildContext;
import aQute.bnd.service.generate.Generator;
import aQute.json.codec.JSONCodec;
import aQute.lib.exceptions.Exceptions;
import aQute.lib.fileset.FileSet;
import aQute.lib.io.IO;
import aQute.lib.strings.Strings;
import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;

@ExternalPlugin(name = "openapi", objectClass = Generator.class)
public class OpenAPI implements Generator<OpenAPIOptions> {
	final static JSONCodec codec = new JSONCodec();

	@Override
	public Optional<String> generate(BuildContext context, OpenAPIOptions options) throws Exception {

		return generate((Processor)context, options);
	}
	
	Optional<String> generate(Processor context, OpenAPIOptions options) {
		System.out.println("Generate OPENAPI");
		File output = options.output();
		if (output == null) {
			return Optional.of("No output directory specified");
		}
		output.mkdirs();
		if (!output.isDirectory())
			return Optional.of("Cannot create output directory " + output);

		List<String> arguments = options._arguments();

		for (String path : arguments) {
			Set<File> fs = new FileSet(context.getBase(), path).getFiles();
			if (fs.isEmpty()) {
				context.error("No files %s", path);
				continue;
			}
			for (File f : fs)
				try {
					String name = f.getName();

					Configuration configuration;

					if (f.getName().endsWith(".oapi")) {
						configuration = codec.dec().from(f).get(Configuration.class);
						if (configuration.openapiFile != null) {
							f = IO.getFile(f.getParentFile(), configuration.openapiFile);
						} else {
							context.error("configuration file %s does not specify `openapiFile` file, skipping", f);
							continue;
						}
					} else {
						configuration = new Configuration();
					}

					String result = getConfiguration(options, configuration, context);
					if (result != null) {
						context.error(result);
						continue;
					}

					if (options.autoname()) {
						String[] parts = Strings.extension(name);
						configuration.typePrefix = parts[0];
						configuration.typePrefix = Character.toUpperCase(configuration.typePrefix.charAt(0))
								+ configuration.typePrefix.substring(1);

						configuration.packagePrefix += "." + parts[0].toLowerCase();
					}

					File preprocessed = IO.createTempFile(f.getParentFile(), "openapi", ".json");
					try {
						String content = IO.collect(f);
						content = context.getReplacer().process(content);
						IO.store(content, preprocessed);
						OpenAPIGenerator gen = new OpenAPIGenerator(preprocessed, configuration);
						gen.generate(output);

						if ( !gen.isOk()) {
							String s = Strings.join("\n",gen.getErrors());
							return Optional.of(s);
						}
					} finally {
						IO.delete(preprocessed);
					}
				} catch (Exception e) {
					context.exception(e, "generate failed for %s : %s", path, Exceptions.causes(e));
					return Optional.of(e.getMessage());
				}
		}
		return Optional.empty();
	}

	private String getConfiguration(OpenAPIOptions options, Configuration c, Processor context) {

		c.baseName = options.basename(c.baseName);
		c.beans = options.beans(c.beans);
		c.conversions = options.conversions(c.conversions);
		c.dateFormat = options.dateformat(c.dateFormat);
		c.dateTimeClass = options.datetimeclass(c.dateTimeClass);
		c.dateTimeFormat = options.dateTimeformat(c.dateTimeFormat);
		c.dtoType = options.dtotype(c.dtoType);
		// c.importsExtra = options.imports();
		c.license = options.license();
		c.tagsMustBeSet = options.mandatorytags(c.tagsMustBeSet);
		c.packagePrefix = options.packageprefix(c.packagePrefix);
		c.privateFields = options.privatefields(c.privateFields);
		c.tags = options.tags(c.tags);
		c.typePrefix = options.typeprefix(c.typePrefix);
		c.uisupport = options.uisupport(c.uisupport);
		c.versionSources = options.versionsources(c.versionSources);

		return null;
	}

}
