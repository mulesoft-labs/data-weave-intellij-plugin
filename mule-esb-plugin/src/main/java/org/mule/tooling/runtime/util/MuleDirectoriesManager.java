package org.mule.tooling.runtime.util;

import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;

import java.io.File;

public class MuleDirectoriesManager extends AbstractProjectComponent {

  protected MuleDirectoriesManager(Project project) {
    super(project);
  }

  public File getMavenTooling(String muleVersion) {
    return new File(getMuleHomeDirectory(muleVersion), "maven-repository");
  }

  public File getGlobalSchemaDirectory(String muleVersion) {
    return new File(getMuleHomeDirectory(muleVersion), "schemas");
  }

  public File getMuleHomeDirectory(String muleVersion) {
    File globalMuleIde = new File(getHomeDirectory(), ".mule_ide");
    return new File(globalMuleIde, muleVersion);
  }

  public File getMuleDisto() {
    return new File(getHomeDirectory(), ".mule-distro");
  }

  public File getSettingsXml() {
    return new File(new File(getHomeDirectory(), ".m2"), "settings.xml");
  }

  public File getHomeDirectory() {
    final String currentUsersHomeDir = System.getProperty("user.home");
    return new File(currentUsersHomeDir);
  }
}
