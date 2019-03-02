package org.mule.tooling.runtime.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class MuleModuleUtils {

    public static final String MULE_APPLICATION_PACKAGING = "mule-application";
    public static final String MULE_EXTENSION_PACKAGING = "mule-extension";
    public static final String MULE_DOMAIN_PACKAGING = "mule-domain";

    public static final String ARTIFACT_JSON = "mule-artifact.json";
    public static final String POM_XML = "pom.xml";

    public static boolean isMuleModule(Module module) {
        //Check the pom if this is a mule module
        MavenProject mavenProject = getMavenProject(module);
        if (mavenProject != null) {
            String packaging = mavenProject.getPackaging();
            return MULE_APPLICATION_PACKAGING.equalsIgnoreCase(packaging) || MULE_EXTENSION_PACKAGING.equalsIgnoreCase(packaging) || MULE_DOMAIN_PACKAGING.equalsIgnoreCase(packaging);
        } else {
            VirtualFile pom = getPom(module);
            if (pom != null) {
                try {
                    String xml = new String(pom.contentsToByteArray(), pom.getCharset());
                    return xml.contains(MULE_APPLICATION_PACKAGING) || xml.contains(MULE_EXTENSION_PACKAGING) || xml.contains(MULE_EXTENSION_PACKAGING);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static boolean isMuleDomainModule(Module module) {
        //Check the pom if this is a mule module

        MuleModuleUtils.waitForMavenProjectsManager(module);

        MavenProject mavenProject = getMavenProject(module);
        if (mavenProject != null) {
            String packaging = mavenProject.getPackaging();
            return MULE_DOMAIN_PACKAGING.equalsIgnoreCase(packaging);
        } else {
            VirtualFile pom = getPom(module);
            if (pom != null) {
                try {
                    String xml = new String(pom.contentsToByteArray(), pom.getCharset());
                    //TODO this should be done by parsing the XML and using xpath or xquery
                    return xml.contains("<packaging>" + MULE_DOMAIN_PACKAGING + "</packaging>");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public static Optional<ProjectType> getProjectTypeOfFile(PsiFile file) {
        Module moduleForFile = ModuleUtil.findModuleForFile(file);
        if (moduleForFile != null) {
            MavenProject mavenProject = getMavenProject(moduleForFile);
            if (mavenProject != null) {
                String packaging = mavenProject.getPackaging();
                return Stream.of(ProjectType.values()).filter((projectType -> projectType.getPackaging().equals(packaging))).findFirst();
            }
        }
        return Optional.empty();
    }

    @Nullable
    public static MavenProject getMavenProject(Module module) {
        final VirtualFile pom = getPom(module);
        if (pom != null) {
            return MavenProjectsManager.getInstance(module.getProject()).findProject(pom);
        } else {
            return null;
        }

    }

    @Nullable
    public static MavenProject getMavenProject(Project project) {
        final VirtualFile pom = project.getBaseDir().findChild(POM_XML);
        if (pom != null) {
            return MavenProjectsManager.getInstance(project).findProject(pom);
        } else {
            return null;
        }
    }

    @Nullable
    public static VirtualFile getPom(Module module) {
        return getModuleBaseDir(module).findChild(POM_XML);
    }

    private static VirtualFile getModuleBaseDir(@NotNull Module module) {
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
    public static VirtualFile getMuleArtifactJson(@NotNull Module module) {
        return getModuleBaseDir(module).findChild(ARTIFACT_JSON);
    }

    public static VirtualFile getMuleArtifactJson(Project project) {
        return project.getBaseDir().findChild(ARTIFACT_JSON);
    }

    public static List<Module> getDomainModules(Project project) {
        List<Module> domainModules = new ArrayList<>();

        for (Module m : ModuleManager.getInstance(project).getModules()) {
            if (MuleModuleUtils.isMuleDomainModule(m))
                domainModules.add(m);
        }
        return domainModules;
    }

    public static void waitForMavenProjectsManager(Module module) {
        //TODO This makes no sense but apparently there's a race condition and maven project must be initialized before it can be accessed
        while (!MavenProjectsManager.getInstance(module.getProject()).isMavenizedProject()) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
