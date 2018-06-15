package org.mule.tooling.bat.launcher;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextComponentAccessor;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

import javax.swing.*;
import java.io.File;

public class BatTestRunnerConfPanel {
  private final Project project;
  private JPanel mainPanel;
  private ModulesComboBox modules;
  private JTextField vmOptionsField;
  private TextFieldWithBrowseButton nameIdentifierField;

  public BatTestRunnerConfPanel(final Project project) {
    this.project = project;
    final FileChooserDescriptor waveDescriptor = new FileChooserDescriptor(true, false, false, false, false, false)
        .withHideIgnored(true)
        .withShowHiddenFiles(false)
        .withFileFilter(virtualFile -> {
          final String extensions = WeaveFileType.WeaveFileExtension;
          return (virtualFile.getExtension() != null && extensions.equalsIgnoreCase(virtualFile.getExtension()));
        });
    getWeaveFile().addBrowseFolderListener("Select Bat File", "Select bat file", project, waveDescriptor, new TextComponentAccessor<JTextField>() {
      @Override
      public String getText(JTextField component) {
        VirtualFile resolve = VirtualFileSystemUtils.resolve(NameIdentifier.apply(component.getText(), Option.empty()), project);
        return resolve != null ? resolve.getPath() : null;
      }

      @Override
      public void setText(JTextField component, @NotNull String text) {
        VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(text));
        NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(project, fileByIoFile);
        component.setText(nameIdentifier.name());
      }
    });
  }

  public TextFieldWithBrowseButton getWeaveFile() {
    return nameIdentifierField;
  }

  public JPanel getMainPanel() {
    return mainPanel;
  }

  public ModulesComboBox getModuleCombo() {
    return modules;
  }

  public JTextField getVmOptionsField() {
    return vmOptionsField;
  }
}
