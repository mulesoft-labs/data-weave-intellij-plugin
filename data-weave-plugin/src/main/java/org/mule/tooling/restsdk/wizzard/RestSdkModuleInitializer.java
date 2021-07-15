package org.mule.tooling.restsdk.wizzard;

import com.intellij.ide.fileTemplates.FileTemplate;
import com.intellij.ide.fileTemplates.FileTemplateManager;
import com.intellij.ide.util.EditorHelper;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.idea.maven.model.MavenConstants;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.mule.tooling.lang.dw.templates.WeaveFilesTemplateManager;

import java.util.Properties;

public class RestSdkModuleInitializer {

    public static final String DESCRIPTOR_YAML = "descriptor.yaml";

    public static void configure(final Project project, final MavenId projectId, final RestSdkConfigurationModel model, final VirtualFile root) {
        try {
            VfsUtil.createDirectories(root.getPath() + "/src/main/resources");
            VfsUtil.createDirectories(root.getPath() + "/src/main/override");
            final VirtualFile apiDirectory = VfsUtil.createDirectories(root.getPath() + "/src/main/resources/api");
            final VirtualFile descriptorsDirectory = VfsUtil.createDirectories(root.getPath() + "/src/main/resources/descriptor");
            VfsUtil.createDirectories(root.getPath() + "/src/test/resources");
            createDescriptorFile(project, projectId, model, descriptorsDirectory);
            createApiFile(project, projectId, model, apiDirectory);
            createPomFile(project, projectId, model, root);
            // execute when current dialog is closed (e.g. Project Structure)
            MavenUtil.invokeLater(project, ModalityState.NON_MODAL, () -> {
                VirtualFile child = descriptorsDirectory.findChild(DESCRIPTOR_YAML);
                if (child != null && child.exists()) {
                    EditorHelper.openInEditor(getPsiFile(project, child));
                }
            });
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static PsiFile getPsiFile(Project project, VirtualFile pom) {
        return PsiManager.getInstance(project).findFile(pom);
    }

    private static void createDescriptorFile(final Project project,
                                             final MavenId projectId,
                                             final RestSdkConfigurationModel model,
                                             final VirtualFile targetFolder) throws Throwable {
        runTemplate(project,
                projectId,
                model,
                targetFolder,
                "Create Connector Descriptor",
                DESCRIPTOR_YAML,
                WeaveFilesTemplateManager.CONNECTOR_DESCRIPTOR);

    }

    private static void createApiFile(final Project project,
                                      final MavenId projectId,
                                      final RestSdkConfigurationModel model,
                                      final VirtualFile targetFolder) throws Throwable {
        switch (model.getApiKind()) {

            case RAML:
                runTemplate(project,
                        projectId,
                        model,
                        targetFolder,
                        "Creating Api Spec",
                        "api.raml",
                        WeaveFilesTemplateManager.RAML_API_TEMPLATE);
                break;
            case OPEN_API:
                runTemplate(project,
                        projectId,
                        model,
                        targetFolder,
                        "Creating Api Spec",
                        "api.yaml",
                        WeaveFilesTemplateManager.OPEN_API_TEMPLATE);
                break;
        }

    }


    private static void createPomFile(final Project project,
                                      final MavenId projectId,
                                      final RestSdkConfigurationModel model,
                                      final VirtualFile root) throws Throwable {
        runTemplate(project,
                projectId,
                model,
                root,
                "Create Weave Module",
                MavenConstants.POM_XML,
                WeaveFilesTemplateManager.REST_SDK_MAVEN_MODULE);
    }

    private static void runTemplate(Project project,
                                    MavenId projectId,
                                    RestSdkConfigurationModel model,
                                    VirtualFile targetFolder,
                                    String actionDescription,
                                    String targetFileName,
                                    String templateName) throws Throwable {


        WriteCommandAction.writeCommandAction(project).withName(actionDescription).run(new ThrowableRunnable<Throwable>() {
            @Override
            public void run() throws Throwable {
                VirtualFile pomFile = targetFolder.findChild(targetFileName);
                if (pomFile != null && pomFile.exists()) {
                    pomFile.delete(this);
                }
                pomFile = targetFolder.findOrCreateChildData(this, targetFileName);

                final Properties templateProps = new Properties();
                templateProps.setProperty("GROUP_ID", projectId.getGroupId());
                templateProps.setProperty("ARTIFACT_ID", projectId.getArtifactId());
                templateProps.setProperty("VERSION", projectId.getVersion());
                templateProps.setProperty("REST_SDK_VERSION", model.getRestSdkVersion());
                templateProps.setProperty("REST_SDK_CONNECTOR_NAME", model.getConnectorName());
                templateProps.setProperty("REST_SDK_SHADE_PACKAGE", projectId.getArtifactId().replaceAll("-", ""));
                final FileTemplateManager manager = FileTemplateManager.getInstance(project);
                final FileTemplate template = manager.getInternalTemplate(templateName);
                final Properties defaultProperties = manager.getDefaultProperties();
                defaultProperties.putAll(templateProps);
                final String text = template.getText(defaultProperties);
                VfsUtil.saveText(pomFile, text);
            }
        });

    }


}
