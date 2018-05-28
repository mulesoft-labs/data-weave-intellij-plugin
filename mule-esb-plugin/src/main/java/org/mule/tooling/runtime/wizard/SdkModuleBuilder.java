package org.mule.tooling.runtime.wizard;

import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.mule.tooling.runtime.RuntimeIcons;

import javax.swing.*;
import java.io.File;

public class SdkModuleBuilder extends MavenModuleBuilder implements SourcePathsBuilder {

    private String muleVersion = "4.1.3-SNAPSHOT";
    private String muleMavenPluginVersion = "1.1.3-SNAPSHOT";
    private String mtfVersion = "1.0.0-SNAPSHOT";

    public SdkModuleBuilder() {
        setProjectId(new MavenId("org.mule.connectors", "sdk-module", "1.0.0-SNAPSHOT"));
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
            SdkModuleInitializer.configure(project, getProjectId(), muleVersion, muleMavenPluginVersion, mtfVersion, root, parentId);
        });
    }

    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public String getName() {
        return "Mule Module";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }


    @Override
    public String getPresentableName() {
        return "Mule Module";
    }

    @Override
    public Icon getNodeIcon() {
        return RuntimeIcons.MuleSdk;
    }

    @Override
    public String getDescription() {
        return "Create a Mule Module.";
    }

}
