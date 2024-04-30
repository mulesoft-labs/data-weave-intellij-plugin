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
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveTestRunnerCommandLine;

import java.util.*;

import static java.util.Optional.ofNullable;

public class WeaveTestConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule, RunProfileState> implements ModuleRunProfile, WeaveTestBaseRunnerConfig {

    public static final String PREFIX = "DataWeaveTestConfig-";
    public static final String WEAVE_FILE = PREFIX + "WeaveFile";
    public static final String TEST_TO_RUN = PREFIX + "TestToRun";
    public static final String WORKING_DIRECTORY = PREFIX + "WorkingDirectory";
    public static final String VM_OPTIONS = PREFIX + "VMOptions";


    private Project project;
    private String weaveFile = "";
    private String testToRun = "";
    private String vmOptions = "";
    private String workingDirectory = "";

    protected WeaveTestConfiguration(String name, @NotNull ConfigurationFactory factory, @NotNull Project project) {
        super(name, new JavaRunConfigurationModule(project, true), factory);
        this.project = project;
    }

    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new WeaveTestRunnerEditor(this);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) {
        return new WeaveTestRunnerCommandLine(executionEnvironment, this);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        this.weaveFile = JDOMExternalizerUtil.readField(element, WEAVE_FILE);
        this.testToRun = JDOMExternalizerUtil.readField(element, TEST_TO_RUN, "");
        this.vmOptions = JDOMExternalizerUtil.readField(element, VM_OPTIONS, "");
        this.workingDirectory = JDOMExternalizerUtil.readField(element, WORKING_DIRECTORY, Optional.ofNullable(project.getBasePath()).orElse(""));
        getConfigurationModule().readExternal(element);
    }


    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent
        JDOMExternalizerUtil.writeField(element, WEAVE_FILE, weaveFile);
        JDOMExternalizerUtil.writeField(element, TEST_TO_RUN, testToRun);
        JDOMExternalizerUtil.writeField(element, VM_OPTIONS, vmOptions);
        JDOMExternalizerUtil.writeField(element, WORKING_DIRECTORY, workingDirectory);
        getConfigurationModule().writeExternal(element);
    }

    public String getWorkingDirectory() {
        if(StringUtils.isBlank(workingDirectory)){
            return ofNullable(project.getBasePath()).orElse("");
        }else {
            return workingDirectory;
        }
    }

    public void setWorkingDirectory(String workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public Collection<Module> getValidModules() {
        final ModuleManager moduleManager = ModuleManager.getInstance(this.project);
        return Arrays.asList(moduleManager.getModules());
    }

    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (StringUtils.isBlank(weaveFile)) {
            throw new RuntimeConfigurationException(getTests() + " weave file can not be empty.");
        }
        if (getModule() == null) {
            throw new RuntimeConfigurationException("Module can not be empty.");
        }
        super.checkConfiguration();
    }


    @Override
    public List<String> getTests() {
        return Collections.singletonList(weaveFile);
    }

    @Override
    public String getTestToRun() {
        return testToRun;
    }

    public void setTestToRun(String testToRun) {
        this.testToRun = testToRun;
    }

    public void setWeaveFile(String weaveFile) {
        this.weaveFile = weaveFile;
    }

    @Override
    public Module getModule() {
        return getConfigurationModule().getModule();
    }

    public String getVmOptions() {
        return vmOptions;
    }

    public void setVmOptions(String vmOptions) {
        this.vmOptions = vmOptions;
    }
}
