package org.mule.tooling.lang.dw.launcher.configuration.ui.test;


import com.intellij.execution.Executor;
import com.intellij.execution.configurations.*;
import com.intellij.execution.runners.ExecutionEnvironment;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizerUtil;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveTestRunnerCommandLine;
import org.mule.tooling.lang.dw.util.WeaveUtils;

import java.util.*;

import static java.util.Optional.ofNullable;

public class WeaveIntegrationTestConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule, RunProfileState> implements ModuleRunProfile, RunConfigurationWithSuppressedDefaultDebugAction, WeaveTestBaseRunnerConfig {

    public static final String PREFIX = "DataWeaveIntegrationTestConfig-";
    public static final String UPDATE_RESULT = PREFIX + "UpdateResult";
    public static final String TEST_TO_RUN = PREFIX + "TestToRun";
    public static final String TEST_KIND = PREFIX + "TestKind";
    public static final String JVM_OPTIONS = PREFIX + "VmOptions";
    public static final String WORKING_DIR = PREFIX + "WorkingDirectory";

    private final Project project;
    private boolean updateResult;
    private String testToRun = "";
    private String vmOptions = "";
    private String workingDirectory = "";
    private IntegrationTestKind kind = IntegrationTestKind.ALL;


    protected WeaveIntegrationTestConfiguration(String name, @NotNull ConfigurationFactory factory, Project project) {
        super(name, new JavaRunConfigurationModule(project, true), factory);
        this.project = project;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new WeaveIntegrationTestRunnerEditor(this);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new WeaveTestRunnerCommandLine(executionEnvironment, this);
    }

    @Override
    public void readExternal(@NotNull Element element) throws InvalidDataException {
        super.readExternal(element);
        getConfigurationModule().readExternal(element);
        updateResult = Boolean.parseBoolean(JDOMExternalizerUtil.readField(element, UPDATE_RESULT));
        vmOptions = JDOMExternalizerUtil.readField(element, JVM_OPTIONS);
        testToRun = JDOMExternalizerUtil.readField(element, TEST_TO_RUN, "");
        workingDirectory = JDOMExternalizerUtil.readField(element, WORKING_DIR, ofNullable(project.getBasePath()).orElse(""));
        kind = IntegrationTestKind.valueOf(JDOMExternalizerUtil.readField(element, TEST_KIND, IntegrationTestKind.MAPPING.name()));
    }


    @Override
    public void writeExternal(@NotNull Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent
        getConfigurationModule().writeExternal(element);
        JDOMExternalizerUtil.writeField(element, UPDATE_RESULT, String.valueOf(updateResult));
        JDOMExternalizerUtil.writeField(element, TEST_TO_RUN, String.valueOf(testToRun));
        JDOMExternalizerUtil.writeField(element, TEST_KIND, getTestKind().name());
        JDOMExternalizerUtil.writeField(element, JVM_OPTIONS, vmOptions);
        JDOMExternalizerUtil.writeField(element, WORKING_DIR, workingDirectory);
    }

    @Override
    public Collection<Module> getValidModules() {
        final ModuleManager moduleManager = ModuleManager.getInstance(this.project);
        return Arrays.asList(moduleManager.getModules());
    }


    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (getModule() == null) {
            throw new RuntimeConfigurationException("Module can not be empty.");
        }
        super.checkConfiguration();
    }


    @Override
    public List<String> getTests() {
        final ArrayList<String> tests = new ArrayList<>();
        if (WeaveUtils.getDWITFolder(getModule()) != null && kind.shouldRunDWIT()) {
            tests.add("dw::test::DWITTestRunner");
        }

        if (WeaveUtils.getDWMITFolder(getModule()) != null && kind.shouldRunDWMIT()) {
            tests.add("dw::test::DWMITTestRunner");
        }

        return tests;
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public boolean isUpdateResult() {
        return updateResult;
    }

    @Override
    public void addAdditionalVMParameters(JavaParameters javaParams) {
        final VirtualFile dwitFolder = WeaveUtils.getDWITFolder(getModule());
        if (dwitFolder != null && dwitFolder.getCanonicalPath() != null) {
            javaParams.getVMParametersList().addProperty("dwitDir", dwitFolder.getCanonicalPath());
        }

        VirtualFile dwmitFolder = WeaveUtils.getDWMITFolder(getModule());
        if (dwmitFolder != null && dwmitFolder.getCanonicalPath() != null) {
            javaParams.getVMParametersList().addProperty("dwmitDir", dwmitFolder.getCanonicalPath());
        }

        if (StringUtils.isNotBlank(vmOptions)) {
            javaParams.getVMParametersList().add(vmOptions);
        }
    }

    @Override
    public String getWorkingDirectory() {
        if(StringUtils.isBlank(workingDirectory)){
            return ofNullable(project.getBasePath()).orElse("");
        }else {
            return workingDirectory;
        }
    }

    public void setUpdateResult(boolean updateResult) {
        this.updateResult = updateResult;
    }

    public void setTestToRun(String testToRun) {
        this.testToRun = testToRun;
    }

    @Override
    public Module getModule() {
        return getConfigurationModule().getModule();
    }

    @Override
    public String getTestToRun() {
        return testToRun;
    }

    public IntegrationTestKind getTestKind() {
        return kind;
    }

    public void setTestKind(IntegrationTestKind selectedItem) {
        this.kind = selectedItem;
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }

    public String getVmOptions() {
        return vmOptions;
    }
}
