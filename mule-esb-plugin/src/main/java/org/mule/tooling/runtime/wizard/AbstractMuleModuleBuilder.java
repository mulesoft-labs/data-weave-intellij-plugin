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
import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.framework.MuleLibraryKind;
import org.mule.tooling.runtime.framework.facet.MuleFacet;
import org.mule.tooling.runtime.framework.facet.MuleFacetConfiguration;
import org.mule.tooling.runtime.framework.facet.MuleFacetType;
import org.mule.tooling.runtime.sdk.MuleSdk;
import org.mule.tooling.runtime.sdk.MuleSdkManagerStore;

import javax.swing.*;
import java.io.File;
import java.util.List;

public abstract class AbstractMuleModuleBuilder extends MavenModuleBuilder implements SourcePathsBuilder {

    public static final String DEFAULT_MULE_VERSION = "4.1.1";

    private String muleVersion = DEFAULT_MULE_VERSION;
    private String muleMavenPluginVersion = "1.1.3-SNAPSHOT";
    private String mtfVersion = "1.0.0-SNAPSHOT";

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) {
        addListener(new ModuleBuilderListener() {
            @Override
            public void moduleCreated(@NotNull Module module) {
                setMuleFramework(module);
            }
        });

        setMuleFacet(rootModel.getModule());

        final Project project = rootModel.getProject();

        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

        if (this.myJdk != null) {
            rootModel.setSdk(this.myJdk);
        } else {
            rootModel.inheritSdk();
        }

        final MavenId parentId = (this.getParentProject() != null ? this.getParentProject().getMavenId() : null);

        invokeInitializer(project, root, parentId);
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        MuleVersionConfiguration step = new MuleVersionConfiguration(this, muleVersion);
        Disposer.register(parentDisposable, step);
        return step;
    }

    public void setMuleFacet(Module module) {
        MuleFacetType type = (MuleFacetType) FacetTypeRegistry.getInstance().findFacetType(MuleFacet.ID);
        MuleFacetConfiguration configuration = type.createDefaultConfiguration();
        MuleSdk sdk = MuleSdkManagerStore.getInstance().findFromVersion(muleVersion);
        configuration.setPathToSdk(sdk.getMuleHome());
        Facet facet = type.createFacet(module, type.getPresentableName(), configuration, null);
        ModifiableFacetModel model = FacetManager.getInstance(module).createModifiableModel();
        model.addFacet(facet);
        model.commit();
    }

    public void setMuleFramework(Module module) {
        ApplicationManager.getApplication().runWriteAction(() ->
        {
            Library muleLibrary = null;
            //MuleSdk sdk = MuleSdkManager.getInstance().getSdkByVersion(muleVersion);
            MuleSdk sdk = MuleSdkManagerStore.getInstance().findFromVersion(muleVersion);
            if (sdk != null) {
                String libraryName = MuleLibraryKind.MULE_LIBRARY_KIND.getKindId() + "-" + muleVersion;
                LibraryTable table = LibraryTablesRegistrar.getInstance().getLibraryTableByLevel(LibraryTablesRegistrar.APPLICATION_LEVEL, module.getProject());

                Library[] libs = table.getLibraries();
                for (final Library lib : libs) {
                    if (lib.getName().equalsIgnoreCase(libraryName)) {
                        muleLibrary = lib;
                        break;
                    }
                }

                if (muleLibrary == null) {//No global Mule library found, create one
                    final LibraryTable.ModifiableModel projectTableModel = table.getModifiableModel();
                    muleLibrary = projectTableModel.createLibrary(libraryName, MuleLibraryKind.MULE_LIBRARY_KIND);
                    final Library.ModifiableModel libraryModel = muleLibrary.getModifiableModel();
                    List<File> entries = sdk.getLibraryEntries();
                    for (File nextEntry : entries) {
                        String pathUrl = VirtualFileManager.constructUrl(LocalFileSystem.PROTOCOL, nextEntry.getAbsolutePath());
                        VirtualFile file = VirtualFileManager.getInstance().findFileByUrl(pathUrl);

                        if (file != null) {
                            libraryModel.addRoot(file, OrderRootType.CLASSES);
                        }
                    }
                    libraryModel.commit();
                    projectTableModel.commit();
                }

                final ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
                model.addLibraryEntry(muleLibrary);
                model.commit();
            }
        });
    }

    private VirtualFile createAndGetContentEntry() {
        String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        (new File(path)).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public Icon getNodeIcon() {
        return RuntimeIcons.MuleRunConfigIcon;
    }

    @Override
    public String getParentGroup() {
        return "AnyPoint";
    }

    public String getMuleVersion() {
        return muleVersion;
    }

    public void setMuleVersion(String muleVersion) {
        this.muleVersion = muleVersion;
    }

    public String getMuleMavenPluginVersion() {
        return muleMavenPluginVersion;
    }

    public void setMuleMavenPluginVersion(String muleMavenPluginVersion) {
        this.muleMavenPluginVersion = muleMavenPluginVersion;
    }

    public String getMtfVersion() {
        return mtfVersion;
    }

    public void setMtfVersion(String mtfVersion) {
        this.mtfVersion = mtfVersion;
    }

    protected abstract void invokeInitializer(Project project, VirtualFile root, MavenId parentId);
}
