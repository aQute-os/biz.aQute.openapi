package aQute.openapi.gradle.plugin;

import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.SourceDirectorySet;
import org.gradle.api.tasks.SourceSet;
import org.gradle.api.tasks.SourceSetContainer;

public class OpenAPIPlugin implements Plugin<Project> {

	private static final String OPENAPI = "openapi";

	@Override
	public void apply(Project project) {

		project.getPlugins().apply("java");
		OpenAPITask task = project.getTasks().create(OPENAPI,
				OpenAPITask.class);

		Task compileJava = project.getTasks().getByPath("compileJava");
		compileJava.dependsOn(task);

		project.afterEvaluate(p -> {
			SourceSetContainer set = (SourceSetContainer) p.getProperties()
					.get("sourceSets");
			SourceSet main = set.getByName("main");
			SourceDirectorySet java = main.getJava();
			java.srcDir(task.getDestinationDir());
		});
	}

}
