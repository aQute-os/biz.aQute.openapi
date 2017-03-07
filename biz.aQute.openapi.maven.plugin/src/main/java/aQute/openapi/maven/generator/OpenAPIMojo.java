package aQute.openapi.maven.generator;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import aQute.lib.fileset.FileSet;
import aQute.openapi.generator.Configuration;
import aQute.openapi.generator.OpenAPIGenerator;

/**
 * @goal OpenAPI
 * @phase generate-sources
 * @description generate code files for Open API Files
 */

@Mojo(name = "openapi", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class OpenAPIMojo extends AbstractMojo {

	/**
	 * @parameter property="${project}"
	 * @required
	 */
	@Parameter(property = "project", required = true)
	protected MavenProject	project;

	/**
	 * @parameter property="${project.build.directory}/generated-sources/openapi"
	 * @required
	 */
	@Parameter(defaultValue = "${project.build.directory}/generated-sources/openapi")
	private File			sourceOutput;

	/**
	 * @parameter
	 * @required
	 */
	@Parameter(defaultValue = "${baseDir}/openapi.json")
	private String			sourceSpec;

	@Parameter
	protected Configuration	c	= new Configuration();

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		boolean couldNotEnforceDir = this.sourceOutput == null
				|| (!this.sourceOutput.isDirectory() && !this.sourceOutput.mkdirs());
		if (couldNotEnforceDir) {
			getLog().error("Could not create source directory! " + this.sourceOutput);
			return;
		}

		this.project.addCompileSourceRoot(this.sourceOutput.getAbsolutePath());

		File root = this.project.getBasedir();

		getLog().info("Source specification " + this.sourceSpec);

		FileSet set = new FileSet(root, sourceSpec);

		getLog().info("Set of files   " + set);
		getLog().info("Tags           " + c.tags);

		try {
			Set<File> files = set.getFiles();
			for (File file : files) {
				generate(file, sourceOutput);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void generate(File source, File out) throws Exception {
		try (FileInputStream in = new FileInputStream(source)) {
			Configuration config = new Configuration();
			OpenAPIGenerator generator = new OpenAPIGenerator(in, config);
			generator.generate(out);
		}
	}
}