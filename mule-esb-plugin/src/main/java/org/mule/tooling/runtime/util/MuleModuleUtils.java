package org.mule.tooling.runtime.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;

public class MuleModuleUtils {

    public static final String MULE_APPLICATION_PACKAGING = "mule-application";
    public static final String MULE_EXTENSION_PACKAGING = "mule-extension";

    public static boolean isMuleModule(Module module) {
        //Check the pom if this is a mule module
        MavenProject mavenProject = getMavenProject(module);
        if (mavenProject != null) {
            String packaging = mavenProject.getPackaging();
            return MULE_APPLICATION_PACKAGING.equalsIgnoreCase(packaging) || MULE_EXTENSION_PACKAGING.equalsIgnoreCase(packaging);
        } else {
            VirtualFile pom = getPom(module);
            try {
                String xml = new String(pom.contentsToByteArray(), pom.getCharset());
                return xml.contains(MULE_APPLICATION_PACKAGING) || xml.contains(MULE_EXTENSION_PACKAGING);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static MavenProject getMavenProject(Module module) {
        VirtualFile pom = getPom(module);
        if (pom != null) {
            return MavenProjectsManager.getInstance(module.getProject()).findProject(pom);
        }

        return null;
    }

    public static VirtualFile getPom(Module module) {
        VirtualFile moduleFile = module.getModuleFile();
        if (moduleFile != null) {
            VirtualFile parent = moduleFile.getParent();
            if (parent != null) {
                return parent.findChild("pom.xml");
            }
        }
        return null;
    }
}
