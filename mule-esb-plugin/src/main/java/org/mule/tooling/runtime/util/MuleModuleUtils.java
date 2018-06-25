package org.mule.tooling.runtime.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;

public class MuleModuleUtils {

  public static final String MULE_APPLICATION_PACKAGING = "mule-application";
  public static final String MULE_EXTENSION_PACKAGING = "mule-extension";
  public static final String ARTIFACT_JSON = "mule-artifact.json";
  public static final String POM_XML = "pom.xml";

  public static boolean isMuleModule(Module module) {
    //Check the pom if this is a mule module
    MavenProject mavenProject = getMavenProject(module);
    if (mavenProject != null) {
      String packaging = mavenProject.getPackaging();
      return MULE_APPLICATION_PACKAGING.equalsIgnoreCase(packaging) || MULE_EXTENSION_PACKAGING.equalsIgnoreCase(packaging);
    } else {
      VirtualFile pom = getPom(module);
      if (pom != null) {
        try {
          String xml = new String(pom.contentsToByteArray(), pom.getCharset());
          return xml.contains(MULE_APPLICATION_PACKAGING) || xml.contains(MULE_EXTENSION_PACKAGING);
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
    return false;
  }

  @Nullable
  public static MavenProject getMavenProject(Module module) {
    return MavenProjectsManager.getInstance(module.getProject()).findProject(module);
  }

  @Nullable
  public static MavenProject getMavenProject(Project project) {
    final VirtualFile child = project.getBaseDir().findChild(POM_XML);
    if (child != null) {
      return MavenProjectsManager.getInstance(project).findProject(child);
    } else {
      return null;
    }
  }

  @Nullable
  public static VirtualFile getPom(Module module) {
    return getModuleBaseDir(module).findChild(POM_XML);
  }

  private static VirtualFile getModuleBaseDir(Module module) {
    VirtualFile moduleFile = module.getModuleFile();
    VirtualFile baseDir = null;
    if (moduleFile != null) {
      baseDir = moduleFile.getParent();
    }
    if (baseDir == null) {
      baseDir = module.getProject().getBaseDir();
    }
    return baseDir;
  }

  @Nullable
  public static VirtualFile getMuleArtifactJson(Module module) {
    return getModuleBaseDir(module).findChild(ARTIFACT_JSON);
  }

  public static VirtualFile getMuleArtifactJson(Project project) {
    return project.getBaseDir().findChild(ARTIFACT_JSON);
  }


}
