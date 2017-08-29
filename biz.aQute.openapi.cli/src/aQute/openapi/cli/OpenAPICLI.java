package aQute.openapi.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aQute.json.codec.JSONCodec;
import aQute.json.codec.TypeReference;
import aQute.lib.collections.ExtList;
import aQute.lib.env.Env;
import aQute.lib.getopt.Arguments;
import aQute.lib.getopt.CommandLine;
import aQute.lib.getopt.Description;
import aQute.lib.getopt.Options;
import aQute.lib.io.IO;
import aQute.lib.justif.Justif;
import aQute.lib.strings.Strings;
import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;
import aQute.openapi.v2.api.OperationObject;

public class OpenAPICLI extends Env {
	Logger	logger	= LoggerFactory.getLogger(OpenAPICLI.class);
	boolean exceptions;

	Appendable	out		= System.out;

	public static void main(String[] args) {
		OpenAPICLI cli = new OpenAPICLI();
		cli.start(args);
	}

	public void start(String args[]) {
		CommandLine cl = new CommandLine(this);

		List<String> line = new ExtList<>(args);
		try {
			cl.execute(this, "openapi", line);
		} catch (Exception e) {
			error("oops, exception thrown (set -e to see the trace)", e);
			if (exceptions)
				e.printStackTrace();
		}
		this.report(System.out);
		if (isOk()) {
			return;
		} else
			System.exit(1);
	}

	interface BasicOptions extends Options {
		@Description("Print out exceptions when they unfortunately happen")
		boolean exceptions();

		@Description("Trace progress")
		boolean trace();

		@Description("Set log level")
		String logLevel();

		@Description("Base dir")
		String base();
	}

	@Description("Options for generating source files from a OpenAPI (formerly Swagger) input definitions")
	public void _openapi(BasicOptions options) throws Exception {
		exceptions = options.exceptions();

		if (options.logLevel() != null) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", options.logLevel());
		} else {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "WARN");
		}

		if (options.base() != null) {
			this.setBase(IO.getFile(options.base()));
		}

		CommandLine handler = options._command();
		List<String> arguments = options._arguments();

		// Rewrite command line to match jar commands and
		// handle commands that provide file names

		try {
			if (arguments.isEmpty()) {
				Justif f = new Justif(80, 20, 22, 72);
				handler.help(f.formatter(), this);
				out.append(f.wrap());
			} else {
				String cmd = arguments.remove(0);
				String help = handler.execute(this, cmd, arguments);
				if (help != null) {
					out.append(help);
				}
				report(System.out);
			}
		} catch (Exception e) {
			if (exceptions)
				e.printStackTrace();
			else
				try (Formatter f = new Formatter(out);) {
					f.format("Exception %s\n", e.getMessage());
				}
		}
	}

	public void _help(Options options) {
		CommandLine cl = new CommandLine(this);
		Formatter f = new Formatter(out);
		cl.help(f, this, "openapi");
		return;
	}

	@Description("Options for generating source files from a OpenAPI configuration")
	@Arguments(arg = "<source>.json...")
	interface GenerateOptions extends Options {
		String to();
	}

	@Description("Options for generating source files from a OpenAPI (formerly Swagger) input definitions")
	public void _generate(GenerateOptions options) throws Exception {

		List<Configuration> configurations = new ArrayList<>();
		List<String> arguments = options._arguments();
		if (arguments.isEmpty()) {
			logger.info("using default bnd.bnd");
			arguments.add("bnd.bnd");
		}

		File to = options.to() == null ? getFile("gen-src") : getFile(options.to());
		to.mkdirs();
		if (!to.isDirectory()) {
			error("Cannot create target directory %s", to);
		}

		if (!isOk())
			return;

		for (String path : arguments) {


			File f = IO.getFile(path);
			logger.info("using path {}", f);

			if (!f.isFile()) {
				error("Not a configuration file %s", f.getAbsoluteFile());
				continue;
			}

			if (f.getName().endsWith(".bnd")) {
				logger.info("bnd file {}", f);
				Env env = new Env();
				env.setProperties(f);
				List<Configuration> from = Configuration.from(env);
				configurations.addAll(from);
			} else if (f.getName().endsWith(".json")) {
				logger.info("json file {}", f);
				JSONCodec codec = new JSONCodec();
				String json = IO.collect(f).trim();

				if ( json.startsWith("[")) {
					List<Configuration> cs = codec.dec().from(json).get(
							new TypeReference<List<Configuration>>() {});
					configurations.addAll(cs);
				} else {
					Configuration c = codec.dec().from(json).get(Configuration.class);
					configurations.add(c);
				}
			}
		}

		if (!isOk())
			return;

		for (Configuration c : configurations) {
			for (String p : c.openapiFile.split(",")) {

				File openAPIFile = getFile(p);
				logger.info("openapi file {}", openAPIFile);

				if (!openAPIFile.isFile()) {
					error("Not an OpenAPI input file %s", openAPIFile.getAbsoluteFile());
					continue;
				}

				logger.info("generating ");
				OpenAPIGenerator gen = new OpenAPIGenerator(openAPIFile, c);
				gen.generate(to);

				getInfo(gen, p);
			}
		}
	}

	@Description("Print version")
	public void _version(Options options) throws IOException {
		String v = OpenAPIGenerator.getGeneratorVersion();
		if (v != null)
			System.out.println(v);
		else
			out.append("Could not find version %n");
	}

	@Arguments(arg = {
			"swagger.json..."
	})
	interface TagOptions extends Options {

	}

	@Description("Show all the operation tags")
	public void _tags(TagOptions options) throws Exception {
		List<String> tags = new ArrayList<String>();

		parse(options, (gen) -> tags.addAll(gen.tags()));
		print(tags);
	}

	@Arguments(arg = {
			"swagger.json..."
	})
	interface PathsOptions extends Options {

	}

	@Description("Show all the operations")
	public void _paths(PathsOptions options) throws Exception {
		List<String> paths = new ArrayList<String>();

		parse(options, (gen) -> paths.addAll(gen.paths()));
		print(paths);
	}

	@Arguments(arg = {
			"swagger.json..."
	})
	interface OperationsOptions extends Options {

	}

	@Description("Show all the operations")
	public void _operations(OperationsOptions options) throws Exception {
		List<OperationObject> operations = new ArrayList<>();
		parse(options, (gen) -> {
			operations.addAll(gen.operations());
		});

		try (Formatter f = new Formatter(out);) {
			operations.forEach(op -> {
				f.format("%-60s %-10s %-30s %s\n", op.path, op.method, op.operationId, Arrays.toString(op.tags));
			});
		}

	}

	private void print(List<String> paths) {
		String join = Strings.join("\n", new TreeSet<>(paths));
		System.out.println(join);
	}

	private void parse(Options options, Consumer<OpenAPIGenerator> callback) throws Exception {
		for (String fileString : options._arguments()) {
			File file = IO.getFile(fileString);
			if (!file.isFile()) {
				error("No such input file: %s", file);
			} else {
				Configuration c = new Configuration();
				trace("File %s", fileString);
				OpenAPIGenerator gen = new OpenAPIGenerator(file, c);
				callback.accept(gen);
			}
		}
	}

}
