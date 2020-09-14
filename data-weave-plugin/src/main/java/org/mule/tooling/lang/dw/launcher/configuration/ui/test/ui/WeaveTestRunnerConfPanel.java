package org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.RawCommandLineEditor;
import org.mule.tooling.lang.dw.ui.WeaveNameIdentifierSelector;

import javax.swing.*;

public class WeaveTestRunnerConfPanel {
    private JPanel mainPanel;
    private ModulesComboBox modules;
    private WeaveNameIdentifierSelector testField;
    private JTextField textField1;
    private JLabel testToRun;
    private RawCommandLineEditor vmOptions;
    private TextFieldWithBrowseButton workingDirectory;

    public WeaveTestRunnerConfPanel(final Project project) {
        modules.addItemListener(e -> testField.setModule(modules.getSelectedModule()));
        addFileChooser("Choose Working Directory", workingDirectory, project);
    }

    public RawCommandLineEditor getVmOptions() {
        return vmOptions;
    }

    public TextFieldWithBrowseButton getWorkingDirectory() {
        return workingDirectory;
    }

    public WeaveNameIdentifierSelector getTestField() {
        return testField;
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ModulesComboBox getModuleCombo() {
        return modules;
    }

    @SuppressWarnings("SameParameterValue")
    private void addFileChooser(final String title,
                                final TextFieldWithBrowseButton textField,
                                final Project project) {
        final FileChooserDescriptor fileChooserDescriptor = new FileChooserDescriptor(false, true, false, false, false, false) {
            @Override
            public boolean isFileVisible(VirtualFile file, boolean showHiddenFiles) {
                return super.isFileVisible(file, showHiddenFiles) && file.isDirectory();
            }
        };
        fileChooserDescriptor.setTitle(title);
        textField.addBrowseFolderListener(title, null, project, fileChooserDescriptor);
    }
}
