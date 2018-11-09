package org.mule.tooling.runtime.wizard;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.facet.ModifiableFacetModel;
import com.intellij.ide.util.projectWizard.ModuleBuilderListener;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.Library;
import com.intellij.openapi.roots.libraries.LibraryTable;
import com.intellij.openapi.roots.libraries.LibraryTablesRegistrar;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilderHelper;
import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.framework.MuleLibraryKind;
import org.mule.tooling.runtime.framework.facet.MuleFacet;
import org.mule.tooling.runtime.framework.facet.MuleFacetConfiguration;
import org.mule.tooling.runtime.framework.facet.MuleFacetType;
import org.mule.tooling.runtime.sdk.MuleSdk;
import org.mule.tooling.runtime.sdk.MuleSdkManager;
import org.mule.tooling.runtime.sdk.MuleSdkManagerStore;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class MuleAppModuleBuilder extends AbstractMuleModuleBuilder {

    public MuleAppModuleBuilder() {
        setProjectId(new MavenId("com.mycompany.myproject", "my-mule-app", "1.0.0-SNAPSHOT"));
    }

    protected void invokeInitializer(Project project, VirtualFile root, MavenId parentId) {

        MavenUtil.invokeLater(project, (DumbAwareRunnable) () -> {
            if (this.getEnvironmentForm() != null) {
                this.getEnvironmentForm().setData(MavenProjectsManager.getInstance(project).getGeneralSettings());
            }
            (new MuleAppModuleInitializer(getProjectId(), getAggregatorProject(), getParentProject(), isInheritGroupId(),
                    isInheritVersion(), getArchetype(), getPropertiesToCreateByArtifact(),
                    "Create new Maven module")).configure(project, getProjectId(), getMuleVersion(),
                                                                        getMuleMavenPluginVersion(), getMtfVersion(), root,
                                                                        parentId);

        });
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
    public String getDescription() {
        return "Creates a Mule Runtime Application Maven Based Project. Maven modules are used for developing <b>JVM-based</b> applications with dependencies managed by <b>Maven</b>. ";
    }
}
