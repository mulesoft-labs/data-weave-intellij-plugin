package org.mule.tooling.runtime.wizard;

import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.mule.tooling.runtime.template.RuntimeTemplateManager;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;
import org.mule.tooling.runtime.wizard.sdk.SdkProject;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class SdkModuleInitializer extends BaseModuleInitializer {

    private static final Logger LOG = Logger.getInstance(SdkModuleInitializer.class.getName());

    public static void configure(final Project project, final MavenId projectId, final String muleVersion, final String muleMavenPluginVersion, final String mtfVersion, final VirtualFile root, @Nullable MavenId parentId, SdkProject sdkProject) {
        try {
            VirtualFile srcResources = VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_MAIN_RESOURCES);
            VirtualFile munit = VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_TEST_MUNIT);
            VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_TEST_RESOURCES);
            try {
                WriteCommandAction.writeCommandAction(project)
                        .withName("Creating Mule Module")
                        .run(new ThrowableRunnable<Throwable>() {
                            @Override
                            public void run() throws Throwable {
                                final Properties templateProps = createTemplateProperties(projectId, muleVersion, muleMavenPluginVersion, mtfVersion);
                                final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                                runTemplate(templateProps, RuntimeTemplateManager.SMART_CONNECTOR_POM_FILE, manager,
                                        root.findOrCreateChildData(this, MavenConstants.POM_XML));
                                runTemplate(templateProps, RuntimeTemplateManager.SMART_CONNECTOR_CONFIG_FILE, manager,
                                        srcResources.findOrCreateChildData(this, projectId.getArtifactId() + ".xml"));
                                runTemplate(templateProps, RuntimeTemplateManager.MUNIT_CONFIG_FILE, manager,
                                        munit.findOrCreateChildData(this, projectId.getArtifactId() + "-munit" + ".xml"));
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
