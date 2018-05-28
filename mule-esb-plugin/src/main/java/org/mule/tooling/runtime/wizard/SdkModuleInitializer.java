package org.mule.tooling.runtime.wizard;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.mule.tooling.runtime.template.RuntimeTemplateManager;

import java.io.IOException;
import java.util.Properties;

public class SdkModuleInitializer {
    public static void configure(final Project project, final MavenId projectId, final String weaveVersion, final String muleMavenPluginVersion, final String mtfVersion, final VirtualFile root, @Nullable MavenId parentId) {
        try {
            VfsUtil.createDirectories(root.getPath() + "/src/main/resources");
            VfsUtil.createDirectories(root.getPath() + "/src/test/munit");
            VfsUtil.createDirectories(root.getPath() + "/src/test/resources");
            createPomFile(project, projectId, weaveVersion, muleMavenPluginVersion, mtfVersion, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void createPomFile(final Project project, final MavenId projectId, final String muleVersion, final String muleMavenPluginVersion, final String mtfVersion, final VirtualFile root) {
        try {
            WriteCommandAction.writeCommandAction(project).withName("Create Mule Module").run(new ThrowableRunnable<Throwable>() {
                @Override
                public void run() throws Throwable {
                    VirtualFile pomFile = root.findOrCreateChildData(this, MavenConstants.POM_XML);
                    final Properties templateProps = new Properties();
                    templateProps.setProperty("GROUP_ID", projectId.getGroupId());
                    templateProps.setProperty("ARTIFACT_ID", projectId.getArtifactId());
                    templateProps.setProperty("VERSION", projectId.getVersion());
                    templateProps.setProperty("MULE_VERSION", muleVersion);
                    templateProps.setProperty("MULE_MAVEN_PLUGIN_VERSION", muleMavenPluginVersion);
                    templateProps.setProperty("MTF_VERSION", mtfVersion);
                    final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                    final FileTemplate template = manager.getInternalTemplate(RuntimeTemplateManager.SMART_CONNECTOR_MODULE_FILE);
                    final Properties defaultProperties = manager.getDefaultProperties();
                    defaultProperties.putAll(templateProps);
                    final String text = template.getText(defaultProperties);
                    VfsUtil.saveText(pomFile, text);
                }
            });
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
