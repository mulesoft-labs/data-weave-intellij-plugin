package org.mule.tooling.lang.dw.launcher.configuration;


import com.intellij.execution.ExecutionException;
import com.intellij.execution.Executor;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.configurations.JavaRunConfigurationModule;
import com.intellij.execution.configurations.ModuleBasedConfiguration;
import com.intellij.execution.configurations.ModuleRunProfile;
import com.intellij.execution.configurations.RunConfiguration;
import com.intellij.execution.configurations.RunProfileState;
import com.intellij.execution.configurations.RuntimeConfigurationException;
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

import java.util.Arrays;
import java.util.Collection;

public class WeaveTestConfiguration extends ModuleBasedConfiguration implements ModuleRunProfile {

    public static final String PREFIX = "DataWeaveConfig-";
    public static final String WEAVE_FILE = PREFIX + "WeaveFile";

    private Project project;
    private String weaveFile;

    protected WeaveTestConfiguration(String name, @NotNull ConfigurationFactory factory, Project project) {
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
    public RunProfileState getState(@NotNull Executor executor, @NotNull ExecutionEnvironment executionEnvironment) throws ExecutionException {
        return new WeaveTestRunnerCommandLine(executionEnvironment, this);
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        super.readExternal(element);
        this.weaveFile = JDOMExternalizerUtil.readField(element, WEAVE_FILE);
        getConfigurationModule().readExternal(element);
    }


    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        super.writeExternal(element);
        // Stores the values of this class into the parent
        JDOMExternalizerUtil.writeField(element, WEAVE_FILE, this.getWeaveFile());
        getConfigurationModule().writeExternal(element);
    }

    @Override
    public Collection<Module> getValidModules() {
        final ModuleManager moduleManager = ModuleManager.getInstance(this.project);
        return Arrays.asList(moduleManager.getModules());
    }


    @Override
    public void checkConfiguration() throws RuntimeConfigurationException {
        if (StringUtils.isBlank(getWeaveFile())) {
            throw new RuntimeConfigurationException(getWeaveFile() + " weave file can not be empty.");
        }
        if (getModule() == null) {
            throw new RuntimeConfigurationException("Module can not be empty.");
        }
        super.checkConfiguration();
    }


    public String getWeaveFile() {
        return weaveFile;
    }

    public void setWeaveFile(String weaveFile) {
        this.weaveFile = weaveFile;
    }

    public Module getModule() {
        return getConfigurationModule().getModule();
    }

}
