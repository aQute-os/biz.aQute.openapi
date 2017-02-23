package aQute.openapi.gradle.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;

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
		System.out.println("Version          " +  1.1);

		System.out.println("Input file       " + input);
		System.out.println("Output file      " + output);
		System.out.println("Destination Dir  " + getDestinationDir());
		System.out.println("Configuration    " + config);

		try (FileInputStream in = new FileInputStream(input);) {
			System.out.println("Processing " + input);
			OpenAPIGenerator gen = new OpenAPIGenerator(in, config);
			System.out.println("Processing2 " + getDestinationDir());
			gen.generate(getDestinationDir());
			System.out.println("Processing2 " + Arrays.toString(getDestinationDir().listFiles()));
		} catch( Exception e) {
			e.printStackTrace();
		}
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
