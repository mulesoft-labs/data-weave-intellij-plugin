package org.mule.tooling.lang.dw.service;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;


public class Scenario implements ItemPresentation {

    public static final String INPUTS_FOLDER = "inputs";
    private VirtualFile scenario;

    public Scenario(@NotNull VirtualFile scenario) {
        this.scenario = scenario;
    }

    @NotNull
    public String getPath() {
        return this.scenario.getPath();
    }

    @Nullable
    public VirtualFile getInputs() {
        if (!isValid(scenario)) {
            return null;
        }
        return scenario.findChild(INPUTS_FOLDER);
    }

    @Nullable
    public VirtualFile addInput(String fileName) {
        try {
            VirtualFile inputs = getOrCreateInputs();
            return inputs.createChildData(this, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @NotNull
    private VirtualFile getOrCreateInputs() throws IOException {
        VirtualFile inputs = getInputs();
        if (!isValid(inputs)) {
            inputs = scenario.createChildDirectory(this, INPUTS_FOLDER);
        }
        return inputs;
    }

    @Nullable
    public VirtualFile addOutput(String fileName) {
        if (!isValid(scenario)) {
            return null;
        }
        try {
            return scenario.createChildData(this, fileName);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Nullable
    public VirtualFile getExpected() {
        if (!isValid(scenario)) {
            return null;
        }
        VirtualFile[] children = scenario.getChildren();
        return Arrays.stream(children).filter((vf) -> vf.getNameWithoutExtension().equals("out")).findFirst().orElse(null);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return StringUtil.capitalizeWords(scenario.getName(), "_", false, false);
    }

    @Nullable
    @Override
    public String getLocationString() {
        return scenario.getPresentableUrl();
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario1 = (Scenario) o;
        return Objects.equals(scenario, scenario1.scenario);
    }

    private boolean isValid(VirtualFile file) {
        return file != null && file.isValid();
    }

    @Override
    public int hashCode() {

        return Objects.hash(scenario);
    }

    public boolean isValid() {
        return scenario.isValid();
    }
}
