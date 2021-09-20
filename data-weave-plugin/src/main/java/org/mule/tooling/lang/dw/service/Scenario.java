package org.mule.tooling.lang.dw.service;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.migrator.YesNoDialog;

import javax.swing.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


public class Scenario implements ItemPresentation {

  public static final String INPUTS_FOLDER = "inputs";
  public static final String OUTPUT_FILE_NAME = "out";
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

  public Optional<VirtualFile> getOutput() {
    return getChildByName(scenario, OUTPUT_FILE_NAME);
  }

  private Optional<VirtualFile> getChildByName(VirtualFile parent, String nameWithoutExt) {
    if (!isValid(parent)) return Optional.empty();
    VirtualFile[] children = parent.getChildren();
    return Arrays.stream(children).filter(x -> x.getNameWithoutExtension().equals(nameWithoutExt)).findAny();
  }

  public boolean containsInput(String inputName) {
    VirtualFile inputs = getInputs();
    if (inputs == null) {
      return false;
    }
    return inputs.findChild(inputName) != null;
  }

  @Nullable
  public VirtualFile addInput(String fileName) {
    try {
      VirtualFile inputs = getOrCreateInputs();
      String baseName = FilenameUtils.getBaseName(fileName);
      String extension = FilenameUtils.getExtension(fileName);
      if (baseName.contains(".")) {
        String[] split = baseName.split("\\.");
        List<String> folders = Arrays.asList(split).subList(0, split.length - 1);
        VirtualFile childDirectory = inputs;
        for (String folder : folders) {
          VirtualFile child = childDirectory.findChild(folder);
          if (child == null) {
            childDirectory = childDirectory.createChildDirectory(this, folder);
          } else {
            childDirectory = child;
          }
        }
        return childDirectory.createChildData(this, split[split.length - 1] + "." + extension);
      } else {
        return inputs.createChildData(this, fileName);
      }
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
  public VirtualFile addOutput(String fileName, String content, Project project) {
    if (!isValid(scenario)) {
      return null;
    }
    VirtualFile file = scenario.findChild(fileName);
    if (file == null) {
      return createFile(fileName, content);
    }

    //TODO: maybe instead of yes/no show a preview of before/after
    if (promptOverwrite(project)) {
      setFileContent(file, content);
    }
    return file;
  }

  private Boolean promptOverwrite(Project project) {
    Ref<Boolean> overwriteRef = Ref.create();
    ApplicationManager.getApplication().invokeAndWait(() -> {
      YesNoDialog dialog = new YesNoDialog("Overwrite file", "Do you want to overwrite the existing expected output?", null, project);
      overwriteRef.set(dialog.showAndGet());
    });
    return overwriteRef.get();
  }

  private VirtualFile createFile(String fileName, String content) {
    try {
      return WriteAction.compute(() -> {
        VirtualFile newFile = scenario.createChildData(this, fileName);
        setFileContent(newFile, content);
        return newFile;
      });
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  private void setFileContent(VirtualFile file, String content) {
    WriteAction.run(() -> {
      if (file == null) {
        return;
      }
      if (!file.isValid()) {
        System.out.println("Input file is invalid. " + file.toString());
        return;
      }

      try {
        file.setBinaryContent(content.getBytes());
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    });
  }

  @Nullable
  public VirtualFile getExpected() {
    if (!isValid(scenario)) {
      return null;
    }
    VirtualFile[] children = scenario.getChildren();
    return Arrays.stream(children).filter((vf) -> vf.getNameWithoutExtension().equals("out")).findFirst().orElse(null);
  }


  public String getName() {
    return scenario.getName();
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
