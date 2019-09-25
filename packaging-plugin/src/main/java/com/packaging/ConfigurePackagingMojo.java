package com.packaging;

import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;

import com.google.common.collect.ImmutableSet;

@Mojo(name = "configure-packaging")
public class ConfigurePackagingMojo extends AbstractMojo {
  private static final Set<String> DEPLOYABLES_REQUIRING_FAT_JAR = ImmutableSet.of(
      "spark-job", "aws-lambda-function"
  );

  @Component
  private MavenProject project;

  @Override
  public void execute() {
    if (DEPLOYABLES_REQUIRING_FAT_JAR.contains(deployableType()) || forceFatJarFlagSet()) {
      useShadePlugin();
    } else if (buildingLocally()) {
      useSlimfastCopy();
    } else {
      useSlimfastUpload();
    }
  }

  private static boolean buildingLocally() {
    return !"true".equals(System.getenv("CI_ENVIRONMENT"));
  }

  private boolean forceFatJarFlagSet() {
    return "true".equals(project.getProperties().getProperty("basepom.force.fat.jar"));
  }

  private String deployableType() {
    return project.getProperties().getProperty("basepom.deployable.type", "unknown");
  }

  private void useShadePlugin() {
    disable("maven.shade.skip");
    enable("slimfast.copy.skip");
    enable("slimfast.upload.skip");
  }

  private void useSlimfastCopy() {
    enable("maven.shade.skip");
    disable("slimfast.copy.skip");
    enable("slimfast.upload.skip");
  }

  private void useSlimfastUpload() {
    enable("maven.shade.skip");
    enable("slimfast.copy.skip");
    disable("slimfast.upload.skip");
  }

  private void enable(String key) {
    project.getProperties().setProperty(key, "true");
  }

  private void disable(String key) {
    project.getProperties().setProperty(key, "false");
  }
}
