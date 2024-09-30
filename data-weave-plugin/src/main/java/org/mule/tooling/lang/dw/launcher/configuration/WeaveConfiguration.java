package org.mule.tooling.lang.dw.launcher.configuration;


import com.intellij.execution.ExecutionException;
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
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.launcher.configuration.runner.WeaveRunnerCommandLine;

import java.util.Arrays;
import java.util.Collection;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class WeaveConfiguration extends ModuleBasedConfiguration<JavaRunConfigurationModule, Element> implements ModuleRunProfile, RunConfigurationWithSuppressedDefaultDebugAction, WeaveBasedConfiguration {

    public static final String PREFIX = "DataWeaveConfig-";
    public static final String WEAVE_NAME_IDENTIFIER = PREFIX + "WeaveNameIdentifier";
    public static final String WEAVE_SCENARIO = PREFIX + "WeaveScenario";
    public static final String WEAVE_OUTPUT = PREFIX + "WeaveOutput";


    private Project project;
    private String nameIdentifier;
    private String scenario;
    private String outputPath;


    protected WeaveConfiguration(String name, @NotNull ConfigurationFactory factory, Project project) {
        super(name, new JavaRunConfigurationModule(project, true), factory);
        this.project = project;
    }


    @NotNull
    @Override
    public SettingsEditor<? extends RunConfiguration> getConfigurationEditor() {
        return new WeaveRunnerEditor(this);
    }

    @Nullable
    @Override
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new WeaveRunnerCommandLine(executionEnvironment, this);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        this.nameIdentifier = JDOMExternalizerUtil.readField(element, WEAVE_NAME_IDENTIFIER);
        this.scenario = JDOMExternalizerUtil.readField(element, WEAVE_SCENARIO);
        this.outputPath = JDOMExternalizerUtil.readField(element, WEAVE_OUTPUT);
        getConfigurationModule().readExternal(element);
    }


    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent
        JDOMExternalizerUtil.writeField(element, WEAVE_NAME_IDENTIFIER, this.getNameIdentifier());
        JDOMExternalizerUtil.writeField(element, WEAVE_SCENARIO, this.getScenario());
        JDOMExternalizerUtil.writeField(element, WEAVE_OUTPUT, this.getOutputPath());
        getConfigurationModule().writeExternal(element);
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

        if (isBlank(getNameIdentifier())) {
            throw new RuntimeConfigurationException(getNameIdentifier() + " weave name identifier can not be empty.");
        }
        super.checkConfiguration();
    }

    public String getNameIdentifier() {
        return nameIdentifier;
    }

    public void setNameIdentifier(String nameIdentifier) {
        this.nameIdentifier = nameIdentifier;
    }

    public String getScenario() {
        return scenario;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public void setScenario(String scenario) {
        this.scenario = scenario;
    }

    public Module getModule() {
        return getConfigurationModule().getModule();
    }

    @Override
    public String getWorkingDirectory() {
        return project.getBasePath();
    }
}
