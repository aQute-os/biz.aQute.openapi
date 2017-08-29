package aQute.openapi.gradle.plugin;

import java.io.File;

import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;

import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;

public class OpenAPITask extends DefaultTask {
	private File			input	= getProject().file("openapi.json");
	private String			output	= "gen-src";
	private Configuration	config	= new Configuration();

	@OutputDirectory
	public File getDestinationDir() {
		File f = new File(output);
		if (f.isAbsolute())
			return f;

		return new File(getProject().getBuildDir(), "openapi/" + output);
	}

	@TaskAction
	void action() throws Exception {
		System.out.println("Version          " + 1.2);

		System.out.println("Input file       " + input);
		System.out.println("Output file      " + output);
		System.out.println("Destination Dir  " + getDestinationDir());
		System.out.println("Configuration    " + config);

		OpenAPIGenerator gen = new OpenAPIGenerator(input, config);
		gen.generate(getDestinationDir());

		gen.report(System.out);
	}

	public void setInput(File f) {
		this.input = f;
	}

	@InputFile
	public File getInput() {
		return input;
	}

	@Input
	public String getOutput() {
		return output;
	}

	public void setOutput(String output) {
		this.output = output;
	}

	public Configuration getConfiguration() {
		return config;
	}

}
