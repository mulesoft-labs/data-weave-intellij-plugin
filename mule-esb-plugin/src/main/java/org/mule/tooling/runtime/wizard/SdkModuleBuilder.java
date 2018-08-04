package org.mule.tooling.runtime.wizard;

import static java.lang.Character.toLowerCase;

import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.wizard.sdk.JavaSdkModuleInitializer;
import org.mule.tooling.runtime.wizard.sdk.SdkProject;

import java.io.File;

import javax.swing.*;

import com.google.common.base.CharMatcher;
import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.wizards.MavenModuleBuilder;

public class SdkModuleBuilder extends MavenModuleBuilder implements SourcePathsBuilder {

    private String muleVersion = "4.1.3-SNAPSHOT";
    private String muleMavenPluginVersion = "1.1.3-SNAPSHOT";
    private String mtfVersion = "1.0.0-SNAPSHOT";

    private SdkProject sdkProject;

    public SdkModuleBuilder() {
        setProjectId(new MavenId("org.mule.connectors", "my-mule-module", "1.0.0-SNAPSHOT"));
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
            switch (sdkProject.getType()) {
                case JAVA:
                    JavaSdkModuleInitializer.configure(project, getProjectId(), root, sdkProject);
                    break;
                case XML:
                    SdkModuleInitializer.configure(project, getProjectId(), muleVersion, muleMavenPluginVersion, mtfVersion, root, parentId, sdkProject);
                    break;
            }
        });
    }

    @Nullable
    @Override
    public ModuleWizardStep getCustomOptionsStep(WizardContext context, Disposable parentDisposable) {
        return new ModuleWizardStep() {

            private SDKModuleBuilderForm sdkModuleBuilderForm;

            @Override
            public JComponent getComponent() {
                sdkModuleBuilderForm = new SDKModuleBuilderForm();
                return sdkModuleBuilderForm.getSdkWizardPanel();
            }

            @Override
            public boolean validate() throws ConfigurationException {
                return !(StringUtils.isEmpty(sdkModuleBuilderForm.getProject().getName()));
            }

            @Override
            public void updateDataModel() {
                sdkProject = sdkModuleBuilderForm.getProject();
                String projectName = dasherize(sdkProject.getName());
                setName(projectName);
                String suffix = "-module";
                if (!projectName.endsWith("connector") && !projectName.endsWith("module")) {
                    projectName = projectName + suffix;
                }
                setProjectId(new MavenId("org.mule.connectors", projectName, "1.0.0-SNAPSHOT"));
            }
        };
    }

    public static String dasherize(String string) {
        string = string.replace(' ', '-');
        StringBuilder builder = new StringBuilder();
        CharMatcher upperMatcher = CharMatcher.inRange('A', 'Z');
        char[] dst = new char[string.length()];
        string.getChars(0, string.length(), dst, 0);

        for (int i = 0; i < dst.length; i++) {
            if(upperMatcher.matches(dst[i])) {
                if(i - 1 >= 0) {
                    if(upperMatcher.matches(dst[i - 1])) {
                        builder.append(toLowerCase(dst[i]));
                    } else {
                        if(dst[i - 1] != '-') {
                            builder.append("-");
                        }
                        builder.append(toLowerCase(dst[i]));
                    }
                } else {
                    builder.append(toLowerCase(dst[i]));
                }
            } else {
                builder.append(dst[i]);
            }
        }
        return builder.toString();
    }

    private VirtualFile createAndGetContentEntry() {
        final String path = FileUtil.toSystemIndependentName(this.getContentEntryPath());
        new File(path).mkdirs();
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public String getName() {
        return "Mule SDK Module";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }


    @Override
    public String getPresentableName() {
        return "Mule SDK Module";
    }

    @Override
    public Icon getNodeIcon() {
        return RuntimeIcons.MuleSdk;
    }

    @Override
    public String getDescription() {
        return "Create a Mule SDK Module.";
    }

    @Override
    public String getParentGroup() {
        return "AnyPoint";
    }

}
