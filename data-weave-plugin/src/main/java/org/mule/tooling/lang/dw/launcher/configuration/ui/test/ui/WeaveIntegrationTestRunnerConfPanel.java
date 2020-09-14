package org.mule.tooling.lang.dw.launcher.configuration.ui.test.ui;

import com.intellij.application.options.ModulesComboBox;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.EnumComboBoxModel;
import com.intellij.ui.RawCommandLineEditor;
import org.mule.tooling.lang.dw.launcher.configuration.ui.test.IntegrationTestKind;

import javax.swing.*;

public class WeaveIntegrationTestRunnerConfPanel {
    private JPanel mainPanel;
    private ModulesComboBox moduleCombo;
    private JTextField testToRun;
    private JCheckBox updateResult;
    private JComboBox<IntegrationTestKind> testKind;
    private RawCommandLineEditor vmOptions;
    private TextFieldWithBrowseButton workingDirectory;

    public WeaveIntegrationTestRunnerConfPanel(Project project) {
        testKind.setModel(new EnumComboBoxModel<IntegrationTestKind>(IntegrationTestKind.class));
        addFileChooser("Choose Working Directory", workingDirectory, project);
    }

    public RawCommandLineEditor getVmOptions() {
        return vmOptions;
    }

    public JComboBox<IntegrationTestKind> getTestKind() {
        return testKind;
    }


    public JPanel getMainPanel() {
        return mainPanel;
    }

    public ModulesComboBox getModuleCombo() {
        return moduleCombo;
    }

    public JTextField getTestToRun() {
        return testToRun;
    }

    public JCheckBox getUpdateResult() {
        return updateResult;
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


    public TextFieldWithBrowseButton getWorkingDirectory() {
        return workingDirectory;
    }
}
