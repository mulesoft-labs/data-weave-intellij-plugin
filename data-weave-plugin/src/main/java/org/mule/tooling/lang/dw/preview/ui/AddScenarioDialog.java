package org.mule.tooling.lang.dw.preview.ui;

import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeService;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class AddScenarioDialog extends DialogWrapper {

    private JPanel mainPanel;
    private JTextField nameField;
    private WeaveRuntimeService manager;
    private PsiFile currentFile;
    private final OnOkAction onOkAction;

    public AddScenarioDialog(@Nullable Project project, WeaveRuntimeService manager, PsiFile currentFile, OnOkAction action) {
        super(project);
        this.manager = manager;
        this.currentFile = currentFile;
        this.onOkAction = action;
        setTitle("Add New Scenario");
        init();
        nameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyTyped(e);
                validateInputName();
            }
        });
    }

    private void validateInputName() {
        String inputName = getInputName();
        VirtualFile dwitFolder = manager.findOrCreateMappingResourceFolder(currentFile);
        if (dwitFolder != null) {
            VirtualFile child = dwitFolder.findChild(inputName);
            if (child != null) {
                setErrorText("'" + inputName + "' already exists");
                setOKActionEnabled(false);
            } else {
                setErrorText(null);
                setOKActionEnabled(true);
            }
        }
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
            Scenario scenario = manager.createScenario(currentFile, getInputName());
            onOkAction.run(scenario);
        });
        close(OK_EXIT_CODE);
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return mainPanel;
    }

    public String getInputName() {
        return nameField.getText();
    }


    public interface OnOkAction {
        void run(Scenario scenario);
    }
}
