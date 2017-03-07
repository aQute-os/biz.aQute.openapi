package aQute.openapi.cli;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.function.Consumer;

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

/**
 * Hello world!
 */
public class OpenAPICLI extends Env {
	boolean exceptions;

	public static void main(String[] args) {
		OpenAPICLI cli = new OpenAPICLI();
		cli.start(args);
	}

	private Appendable out = System.out;

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
		this.report(out);
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

	}

	@Description("Options for generating source files from a OpenAPI (formerly Swagger) input definitions")
	public void _openapi(BasicOptions options) throws Exception {
		exceptions = options.exceptions();
		if (options.logLevel() != null) {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", options.logLevel());
		} else {
			System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "WARN");
		}
		setTrace(options.trace());

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

	@Description("Options for generating source files from a OpenAPI (formerly Swagger) input definitions")
	@Arguments(arg = "<source>.json...")
	interface GenerateOptions extends Options {
		@Description("The prefix for all packages, for example `com.example.openapi`. The next directory is the tag turned into a package name, and then the type in there is also the tag.")
		String packagePrefix();

		@Description("The root directory for the packages/types relative to the working directory. For example 'target/generated-sources'")
		String dir(String deflt);

		@Description("The default directory for the input files. For example 'openapi'")
		String from(String deflt);

		@Description("The class name used for any entries not tagged")
		String baseName();

		@Description("The prefix for the generated classes")
		String classPrefix();

		@Description("A glob expression on tags followed by a replacement when matched. For example: `foo*:foo`. "
				+ "This can be used to merge operations from list with different tags. If the value is '*' then the "
				+ "input is matched, if the value is '!' then the input is ignored. If only the glob is specified it means the value defaults to '*'")
		String[] mapTags();

		@Description("Add imports to the source files")
		String[] importsExtra();

	}

	@Description("Options for generating source files from a OpenAPI (formerly Swagger) input definitions")
	public void _generate(GenerateOptions options) throws Exception {

		if (options._arguments().isEmpty()) {

		}
		Configuration c = new Configuration();
		c.packagePrefix = set(c.packagePrefix, options::packagePrefix);
		c.baseName = set(c.baseName, options::baseName);
		c.typePrefix = set(c.typePrefix, options::classPrefix);

		String[] importsExtra = options.importsExtra();
		doImportsExtra(c, importsExtra);

		c.tags = options.mapTags();

		File dir = IO.getFile(options.dir("."));

		dir.mkdirs();
		if (!dir.isDirectory()) {
			error("Cannot create target directory %s", dir);
		}

		File from = IO.getFile(options.from("."));
		if (!from.isDirectory()) {
			error("No such source directory %s", from);
		}

		if (!isOk())
			return;

		for (String fileString : options._arguments()) {
			File file = IO.getFile(from, fileString);
			if (!file.isFile()) {
				error("No such input file: %s", file);
			} else {

				trace("File %s", fileString);
				OpenAPIGenerator gen = new OpenAPIGenerator(file, c);
				gen.generate(dir);
			}
		}
	}

	private void doImportsExtra(Configuration c, String[] importsExtra) {
		if (importsExtra != null) {
			for (String s : importsExtra) {
				c.importsExtra.add(s.trim());
			}
		}
	}

	private <T> T set(T deflt, Callable<T> callable) throws Exception {
		T result = callable.call();
		if (result == null)
			return deflt;

		return result;
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
