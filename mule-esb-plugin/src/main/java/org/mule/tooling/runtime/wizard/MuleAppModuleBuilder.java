package org.mule.tooling.runtime.wizard;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.mule.tooling.runtime.RuntimeIcons;

import javax.swing.*;
import java.io.File;

public class MuleAppModuleBuilder extends MavenModuleBuilder implements SourcePathsBuilder {


    private String muleVersion = "4.1.3-SNAPSHOT";
    private String muleMavenPluginVersion = "1.1.3-SNAPSHOT";
    private String mtfVersion = "1.0.0-SNAPSHOT";

    public MuleAppModuleBuilder() {
        setProjectId(new MavenId("org.mule.app", "my-mule-app", "1.0.0-SNAPSHOT"));
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) {
        super.setupRootModel(rootModel);
        final Project project = rootModel.getProject();
        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);
        //Check if this is a module and has parent
        final MavenId parentId = (this.getParentProject() != null ? this.getParentProject().getMavenId() : null);
        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () -> {
            MuleAppModuleInitializer.configure(project, getProjectId(), muleVersion, muleMavenPluginVersion, mtfVersion, root, parentId);
        });
    }

    private VirtualFile createAndGetContentEntry() {
        final String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return null;
    }

    @Override
    public String getName() {
        return "Mule Application";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }


    @Override
    public String getPresentableName() {
        return "Mule Application";
    }

    @Override
    public Icon getNodeIcon() {
        return RuntimeIcons.MuleRunConfigIcon;
    }

    @Override
    public String getDescription() {
        return "Create a Mule Application.";
    }

    @Override
    public String getParentGroup() {
        return "AnyPoint";
    }
}
