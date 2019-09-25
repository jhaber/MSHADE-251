package com.packaging;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

@Mojo(name = "configure-packaging")
public class ConfigurePackagingMojo extends AbstractMojo {

  @Component
  private MavenProject project;

  @Override
  public void execute() throws MojoExecutionException, MojoFailureException {
    switch (project.getProperties().getProperty("basepom.deployable.type", "unknown")) {
      case "rest-api":
      case "spark-job":
      default:
        getLog().info("Found deployable type: " + project.getProperties().getProperty("basepom.deployable.type", "unknown"));
    }
  }
}
