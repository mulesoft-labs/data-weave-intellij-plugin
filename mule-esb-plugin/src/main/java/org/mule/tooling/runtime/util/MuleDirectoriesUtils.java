package org.mule.tooling.runtime.util;

import com.intellij.openapi.module.Module;

import java.io.File;

public class MuleDirectoriesUtils {

  public static final String MULE_APP_PATH = "src/main/mule";

  public static File getMavenTooling() {
    File maven_repository = new File(new File(getHomeDirectory(), ".m2"), "repository");
    if (!maven_repository.exists()) {
      maven_repository.mkdirs();
    }
    return maven_repository;
  }

  public static File getRuntimeSchemaDirectory(String muleVersion) {
    File schemas = new File(getMuleHomeDirectory(muleVersion), "schemas");
    if (!schemas.exists()) {
      schemas.mkdirs();
    }
    return schemas;
  }

  public static File getMuleHomeDirectory(String muleVersion) {
    File globalMuleIde = new File(getHomeDirectory(), ".mule_ide");
    if (!globalMuleIde.exists()) {
      globalMuleIde.mkdirs();
    }
    return new File(globalMuleIde, muleVersion);
  }

  public static File getMuleRuntimesHomeDirectory() {
    File runtimes = new File(getHomeDirectory(), ".mule_runtimes");
    if (!runtimes.exists()) {
      runtimes.mkdirs();
    }
    return runtimes;
  }

  public static File getMuleWorkingDirectory(Module module) {
    File moduleFile = new File(module.getModuleFilePath());
    if (moduleFile.exists()) {
      File moduleHome = moduleFile.getParentFile();
      File app = new File(new File(moduleHome, ".mule_ide"), "app");
      if (!app.exists()) {
        app.mkdirs();
      }
      return app;
    } else {
      File projectBasePath = new File(module.getProject().getBasePath());
      File app = new File(new File(projectBasePath, ".mule_ide"), "app");
      if (!app.exists()) {
        app.mkdirs();
      }
      return app;
    }
  }

  public static File getSettingsXml() {
    return new File(new File(getHomeDirectory(), ".m2"), "settings.xml");
  }

  public static File getHomeDirectory() {
    final String currentUsersHomeDir = System.getProperty("user.home");
    return new File(currentUsersHomeDir);
  }
}
