package org.mule.tooling.restsdk.wizzard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.mule.tooling.commons.wizard.maven.AbstractMavenBasedProjectBuilder;
import org.mule.tooling.commons.wizard.maven.MavenCreatorUtils;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.*;
import java.io.File;
import java.util.Objects;

public class RestSdkModuleBuilder extends AbstractMavenBasedProjectBuilder implements SourcePathsBuilder {

    public static final String REST_SDK_DEFAULT_VERSION = "0.8.0-SNAPSHOT";
    private RestSdkConfigurationModel restSdkModel;

    public RestSdkModuleBuilder() {
        this.restSdkModel = new RestSdkConfigurationModel(REST_SDK_DEFAULT_VERSION, "", ApiKind.OPEN_API);
        setProjectId(new MavenId("com.mulesoft.connectors", "mule4-connector-connector", "1.0.0-SNAPSHOT"));
    }

    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{
                createMavenStep(),
                new RestSdkConfigurationStep(this.restSdkModel, getMavenModel())
        };
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);
        final Project project = rootModel.getProject();
        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

        //Check if this is a module and has parent
//   From ->      AbstractMavenModuleBuilder
//        if (myEnvironmentForm != null) {
//            myEnvironmentForm.setData(MavenProjectsManager.getInstance(project).getGeneralSettings());
//        }
//
//        new MavenModuleBuilderHelper(myProjectId, myAggregatorProject, myParentProject, myInheritGroupId,
//                myInheritVersion, myArchetype, myPropertiesToCreateByArtifact,
//                MavenProjectBundle.message("command.name.create.new.maven.module")).configure(project, root, false);
        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            MavenCreatorUtils.createMavenStructure(root);
            RestSdkModuleInitializer.configure(project, getProjectId(), this.restSdkModel, root);
            MavenProjectsManager.getInstance(project).forceUpdateAllProjectsOrFindAllAvailablePomFiles();
        });

    }

    private VirtualFile createAndGetContentEntry() {
        final String path = FileUtil.toSystemIndependentName(Objects.requireNonNull(this.getContentEntryPath()));
        boolean mkdirs = new File(path).mkdirs();
        if (!mkdirs) {
            System.out.println("Unable to create " + path);
        }
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public String getName() {
        return "RestSdk Project";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getPresentableName() {
        return "RestSdk Project";
    }

    @Override
    public Icon getNodeIcon() {
        return WeaveIcons.RestSdkIcon;
    }

    @Override
    public String getDescription() {
        return "Create a RestSdk project.";
    }

}
