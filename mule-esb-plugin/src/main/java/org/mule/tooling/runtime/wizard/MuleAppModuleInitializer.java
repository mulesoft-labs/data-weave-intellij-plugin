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
import org.jetbrains.idea.maven.model.MavenArchetype;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilderHelper;
import org.mule.tooling.runtime.template.RuntimeTemplateManager;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

public class MuleAppModuleInitializer extends MavenModuleBuilderHelper {

    protected static final Logger LOG = Logger.getInstance(MuleAppModuleInitializer.class.getName());


    public MuleAppModuleInitializer(@NotNull MavenId projectId, MavenProject aggregatorProject, MavenProject parentProject, boolean inheritGroupId, boolean inheritVersion, MavenArchetype archetype, Map<String, String> propertiesToCreateByArtifact, String commaneName) {
        super(projectId, aggregatorProject, parentProject, inheritGroupId, inheritVersion, archetype, propertiesToCreateByArtifact, commaneName);
    }

    public void configure(final Project project, final MavenId projectId, final String muleVersion, final String muleMavenPluginVersion, final String mtfVersion, final VirtualFile root, @Nullable MavenId parentId) {
        super.configure(project, root, false);

        try {
            VirtualFile srcResources = VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_MAIN_RESOURCES);
            VirtualFile srcApi = VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_MAIN_API);
            VirtualFile munit = VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_TEST_MUNIT);
            VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_TEST_RESOURCES);
            VirtualFile muleDirectory = VfsUtil.createDirectories(root.getPath() + File.separator + MuleDirectoriesUtils.SRC_MAIN_MULE);
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

    private void runTemplate(Properties templateProps, String templateName, FileTemplateManager manager, VirtualFile pomFile) throws IOException {
        final FileTemplate template = manager.getInternalTemplate(templateName);
        final Properties defaultProperties = manager.getDefaultProperties();
        defaultProperties.putAll(templateProps);
        final String text = template.getText(defaultProperties);
        VfsUtil.saveText(pomFile, text);
    }

    @NotNull
    private Properties createTemplateProperties(MavenId projectId, String muleVersion, String muleMavenPluginVersion, String mtfVersion) {
        final Properties templateProps = new Properties();
        templateProps.setProperty("GROUP_ID", projectId.getGroupId());
        templateProps.setProperty("ARTIFACT_ID", projectId.getArtifactId());
        templateProps.setProperty("VERSION", projectId.getVersion());
        templateProps.setProperty("MULE_VERSION", muleVersion);
        templateProps.setProperty("MULE_MAVEN_PLUGIN_VERSION", muleMavenPluginVersion);
        templateProps.setProperty("MTF_VERSION", mtfVersion);
        templateProps.setProperty("FILE_PATTERN", "${sys:mule.home}${sys:file.separator}logs${sys:file.separator}" + projectId.getArtifactId() + "-%i.log");
        templateProps.setProperty("FILE_NAME", "${sys:mule.home}${sys:file.separator}logs${sys:file.separator}" + projectId.getArtifactId() + ".log");
        return templateProps;
    }

}
