package org.mule.tooling.lang.dw.service;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.Objects;


public class Scenario implements ItemPresentation {

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
        return scenario.findChild("inputs");
    }

    @Nullable
    public VirtualFile getExpected() {
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

    @Override
    public int hashCode() {

        return Objects.hash(scenario);
    }
}
