package org.mule.tooling.lang.dw.preview.ui;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.ui.EnumComboBoxModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeService;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AddInputDialog extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField nameField;
    private JComboBox<DataFormat> formatCombo;
    private WeaveRuntimeService manager;
    private Scenario currentScenarioMaybe;
    private PsiFile currentFile;

    public AddInputDialog(@Nullable Project project, WeaveRuntimeService manager, Scenario currentScenario, PsiFile currentFile) {
        super(project);
        this.manager = manager;
        currentScenarioMaybe = currentScenario;
        this.currentFile = currentFile;
        setTitle("Add New Input");
        init();
        formatCombo.setModel(new EnumComboBoxModel<>(DataFormat.class));
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                validateInputName(currentScenario);
            }
        });

        formatCombo.addActionListener((e) ->
                validateInputName(currentScenario)
        );
    }

    private void validateInputName(Scenario currentScenario) {
        if (currentScenario == null || !currentScenario.isValid()) return;
        String inputName = getInputFileName();
        if (currentScenario.containsInput(inputName)) {
            setErrorText("'" + inputName + "' already exists");
            setOKActionEnabled(false);
        } else {
            setErrorText(null);
            setOKActionEnabled(true);
        }
    }

    @NotNull
    private String getInputFileName() {
        return getInputName() + "." + getFormat().extension;
    }

    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return nameField;
    }

    @Override
    protected void doOKAction() {
        if (!getOKAction().isEnabled()) return;

        WriteAction.run(() -> {
            Scenario scenario = getOrCreateScenario();
            if(scenario != null) {
                DataFormat format = getFormat();
                String fileName = getInputFileName();
                VirtualFile inputFile = scenario.addInput(fileName);
                switch (format) {
                    case JSON:
                        setFileContent(inputFile, "{\n  \"a\": 1\n}");
                        break;
                    case XML:
                        setFileContent(inputFile, "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n<element></element>");
                        break;
                    case CSV:
                        setFileContent(inputFile, "name,lastName\nJohn,Doe");
                        break;
                    default:
                        break;
                }
            }
        });
        close(OK_EXIT_CODE);
    }

    @Nullable
    private Scenario getOrCreateScenario() {
        Scenario currentScenario = currentScenarioMaybe;
        if (currentScenario == null || !currentScenario.isValid()) {
            //create scenario
            currentScenario = manager.createScenario(currentFile, WeaveConstants.DEFAULT_SCENARIO_NAME);
        }
        return currentScenario;
    }

    private void setFileContent(VirtualFile inputFile, String content) {
        if (inputFile == null) {
            return;
        }
        if (!inputFile.isValid()) {
            System.out.println("Input file is invalid. " + inputFile.toString());
            return;
        }

        try {
            inputFile.setBinaryContent(content.getBytes());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getInputName() {
        return nameField.getText();
    }

    public DataFormat getFormat() {
        return (DataFormat) formatCombo.getSelectedItem();
    }

    private enum DataFormat {
        JSON("json"), XML("xml"), CSV("csv"), TXT("txt"), DWL("dwl"), YAML("yaml"), NDJSON("ndjson"), MULTIPART("multipart"), URLENCODED("urlencoded");

        final String extension;

        DataFormat(String extension) {
            this.extension = extension;
        }
    }
}
