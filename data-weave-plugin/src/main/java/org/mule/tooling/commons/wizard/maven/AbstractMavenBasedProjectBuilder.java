// Copyright 2000-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package org.mule.tooling.commons.wizard.maven;

import com.intellij.ide.util.projectWizard.*;
import com.intellij.openapi.externalSystem.service.project.manage.ExternalProjectsManagerImpl;
import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.StdModuleTypes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.SdkTypeId;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;

import javax.swing.*;
import java.io.File;
import java.util.Collections;
import java.util.List;

import static icons.OpenapiIcons.RepositoryLibraryLogo;

public abstract class AbstractMavenBasedProjectBuilder extends ModuleBuilder implements SourcePathsBuilder {

    private MavenInfoModel myModel;

    public AbstractMavenBasedProjectBuilder() {
        this.myModel = new MavenInfoModel(new MavenId("", "", ""));
    }

    public void setProjectId(MavenId myProjectId) {
        this.myModel.setMavenId(myProjectId);
    }

    public MavenModuleWizardStep createMavenStep() {
        return new MavenModuleWizardStep(myModel);
    }

    public MavenId getProjectId() {
        return myModel.getMavenId();
    }

    @Override
    public void setupRootModel(@NotNull ModifiableRootModel rootModel) {
        final Project project = rootModel.getProject();
        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

        // todo this should be moved to generic ModuleBuilder
        if (myJdk != null) {
            rootModel.setSdk(myJdk);
        } else {
            rootModel.inheritSdk();
        }
    }

    @Override
    public @NonNls String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getParentGroup() {
        return JavaModuleType.JAVA_GROUP;
    }

    @Override
    public int getWeight() {
        return JavaModuleBuilder.BUILD_SYSTEM_WEIGHT;
    }

    @Override
    public Icon getNodeIcon() {
        return RepositoryLibraryLogo;
    }

    @Override
    public ModuleType getModuleType() {
        return StdModuleTypes.JAVA;
    }

    @Override
    public boolean isSuitableSdkType(SdkTypeId sdk) {
        return sdk == JavaSdk.getInstance();
    }

    @Override
    public abstract ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider);

    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(getContentEntryPath());
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public List<Pair<String, String>> getSourcePaths() {
        return Collections.emptyList();
    }

    @Override
    public void setSourcePaths(List<Pair<String, String>> sourcePaths) {
    }

    @Override
    public void addSourcePath(Pair<String, String> sourcePathInfo) {
    }


    @Nullable
    @Override
    public Project createProject(String name, String path) {
        return ExternalProjectsManagerImpl.setupCreatedProject(super.createProject(name, path));
    }
}
