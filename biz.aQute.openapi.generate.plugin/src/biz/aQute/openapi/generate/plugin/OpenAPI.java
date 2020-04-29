package biz.aQute.openapi.generate.plugin;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

@ExternalPlugin(name = "openapi", objectClass = Generator.class, subtype = OpenAPIOptions.class)
public class OpenAPI implements Generator<OpenAPIOptions> {
	final static JSONCodec codec = new JSONCodec();

	@Override
	public Optional<String> generate(BuildContext context, OpenAPIOptions options) throws Exception {

		File output = options.output();
		if (output == null) {
			return Optional.of("No output directory specified");
		}
		output.mkdirs();
		if (!output.isDirectory())
			return Optional.of("Cannot create output directory " + output);

		List<String> arguments = options._arguments();
		arguments.remove(0);
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

					OpenAPIGenerator gen = new OpenAPIGenerator(f, configuration);
					gen.generate(output);

					context.getInfo(gen, path);
				} catch (Exception e) {
					context.exception(e, "generate failed for %s : %s", path, Exceptions.causes(e));
				}
		}
		return Optional.empty();
	}

	private String getConfiguration(OpenAPIOptions options, Configuration c, BuildContext context) {

		c.baseName = options.basename(c.baseName);
		c.beans = options.beans();
		c.conversions = options.conversions();
		c.dateFormat = options.dateformat();
		c.dateTimeClass = options.datetimeclass();
		c.dateTimeFormat = options.dateTimeformat();
		c.dtoType = options.dtotype(c.dtoType);
//		c.importsExtra = options.imports();
		c.license = options.license();
		c.tagsMustBeSet = options.mandatorytags();
		c.packagePrefix = options.packageprefix(c.packagePrefix);
		c.privateFields = options.privatefields();
		c.tags = options.tags();
		c.typePrefix = options.typeprefix(c.typePrefix);
		c.uisupport = options.uisupport();
		c.versionSources = options.versionsources();

		return null;
	}

}
