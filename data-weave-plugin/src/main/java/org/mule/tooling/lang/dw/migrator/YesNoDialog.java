package org.mule.tooling.lang.dw.migrator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.MultiLineLabelUI;
import com.intellij.refactoring.RefactoringBundle;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class YesNoDialog extends DialogWrapper {
    private final String myMessage;
    private final String myHelpID;

    public YesNoDialog(String title, String message,
                       String helpID, Project project) {
        super(project, false);
        myHelpID = helpID;
        setTitle(title);
        myMessage = message;
        setOKButtonText(RefactoringBundle.message("yes.button"));
        setCancelButtonText(RefactoringBundle.message("no.button"));
        init();
    }

    protected JComponent createNorthPanel() {
        JLabel label = new JLabel(myMessage);
        label.setUI(new MultiLineLabelUI());
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(label, BorderLayout.CENTER);
        Icon icon = Messages.getQuestionIcon();
        label.setIcon(icon);
        label.setIconTextGap(7);
        return panel;
    }

    protected JComponent createCenterPanel() {
        return null;
    }


    @NotNull
    protected Action[] createActions() {
        if (myHelpID != null) {
            return new Action[]{getOKAction(), getCancelAction(), getHelpAction()};
        } else {
            return new Action[]{getOKAction(), getCancelAction()};
        }
    }


}