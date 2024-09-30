package org.mule.tooling.lang.dw.develop_view;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.components.JBScrollPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.commons.AnypointNotification;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.settings.DataWeaveSettingsState;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public abstract class DotBasedToolingWindowPanel extends SimpleToolWindowPanel implements Disposable {


    // used for diff view
    protected Project project;
    private JComponent lblimage;

    private JPanel centerPanel;

    public DotBasedToolingWindowPanel(boolean vertical, final Project project) {
        super(vertical);
        this.project = project;
    }

    protected void setupUI() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(createAction());
        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar actionToolBar = actionManager.createActionToolbar("AST", group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        setToolbar(buttonsPanel);

        // Create a panel with a borderlayout

        centerPanel = new JPanel(new BorderLayout());
        centerPanel.setAutoscrolls(true);

        add(centerPanel);
    }

    @NotNull
    protected abstract AnAction createAction();

    public PsiFile getCurrentFile() {
        final VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
        if (files.length > 0) {
            final PsiFile selectedFile = PsiManager.getInstance(project).findFile(files[0]);
            if (selectedFile != null && selectedFile.getContainingFile() != null && selectedFile.getContainingFile().getFileType() == WeaveFileType.getInstance()) {
                return selectedFile;
            }
        }
        return null;
    }

    public void dispose() {

    }

    protected void updateDot(String text) {
        final Image image = runDot(text);
        if (image != null) {
            if (lblimage != null) {
                centerPanel.remove(lblimage);
            }
            lblimage = new JBScrollPane(new JLabel(new ImageIcon(image)));
            centerPanel.add(lblimage);
            centerPanel.setPreferredSize(getPreferredSize());
            centerPanel.revalidate();
        }
    }

    @Nullable
    Image runDot(String dot) {
        String cmdPath = DataWeaveSettingsState.getInstance().getCmdPath();
        try {
            final String tempDirectory = FileUtil.getTempDirectory();
            final File dotFile = new File(tempDirectory, getClass().getSimpleName() + ".dot");
            System.out.println("dotFile = " + dotFile);
            FileUtil.writeToFile(dotFile, dot);
            final File imageFile = new File(tempDirectory, getClass().getSimpleName() + ".png");
            final String arg1 = dotFile.getAbsolutePath();
            final String arg2 = imageFile.getAbsolutePath();

            final String[] c = {cmdPath, "-Tpng", arg1, "-o", arg2};
            final Process p = Runtime.getRuntime().exec(c);
            final int err = p.waitFor();
            return ImageIO.read(imageFile);
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification(AnypointNotification.ANYPOINT_NOTIFICATION, "Unable to run dot cmd", "Unable to run dot command line from '" + cmdPath + "'", NotificationType.ERROR));
            return null;
        }
    }


}
