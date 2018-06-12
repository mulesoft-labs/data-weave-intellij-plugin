package org.mule.tooling.runtime.wizard;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.mule.tooling.runtime.template.RuntimeTemplateManager;

import java.io.IOException;
import java.util.Properties;

public class MuleAppModuleInitializer extends BaseModuleInitializer {

    public static final String SRC_MAIN_RESOURCES = "/src/main/resources";
    public static final String SRC_MAIN_MULE = "/src/main/mule";
    public static final String SRC_TEST_MUNIT = "/src/test/munit";
    public static final String SRC_TEST_RESOURCES = "/src/test/resources";

    public static void configure(final Project project, final MavenId projectId, final String muleVersion, final String muleMavenPluginVersion, final String mtfVersion, final VirtualFile root, @Nullable MavenId parentId) {
        try {
            VirtualFile srcResources = VfsUtil.createDirectories(root.getPath() + SRC_MAIN_RESOURCES);
            VirtualFile munit = VfsUtil.createDirectories(root.getPath() + SRC_TEST_MUNIT);
            VfsUtil.createDirectories(root.getPath() + SRC_TEST_RESOURCES);
            VirtualFile muleDirectory = VfsUtil.createDirectories(root.getPath() + SRC_MAIN_MULE);
            try {
                WriteCommandAction.writeCommandAction(project)
                        .withName("Creating Mule App Module")
                        .run(new ThrowableRunnable<Throwable>() {
                            @Override
                            public void run() throws Throwable {
                                final Properties templateProps = createTemplateProperties(projectId, muleVersion, muleMavenPluginVersion, mtfVersion);
                                final FileTemplateManager manager = FileTemplateManager.getInstance(project);

                                runTemplate(templateProps, RuntimeTemplateManager.MULE_APP_POM_FILE, manager,
                                        root.findOrCreateChildData(this, MavenConstants.POM_XML));

                                runTemplate(templateProps, RuntimeTemplateManager.MULE_APP_CONFIG_FILE, manager,
                                        muleDirectory.findOrCreateChildData(this, projectId.getArtifactId() + ".xml"));

                                runTemplate(templateProps, RuntimeTemplateManager.MULE_APP_LOG4J_FILE, manager,
                                        srcResources.findOrCreateChildData(this, "log4j2.xml"));


                                runTemplate(templateProps, RuntimeTemplateManager.MULE_APP_ARTIFACT_JSON_FILE, manager,
                                        root.findOrCreateChildData(this, "mule-artifact.json"));



                                return;
                            }
                        });
            } catch (Throwable throwable) {
                LOG.error(throwable);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
