package org.mule.tooling.lang.dw.cheatsheetview;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.JBColor;
import com.sun.javafx.application.PlatformImpl;
import javafx.collections.ObservableList;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class CheatSheetToolingWindowPanel extends SimpleToolWindowPanel implements Disposable {

    private JFXPanel centerPanel;
    private StackPane stage;
    private WebView browser;

    private JButton swingButton;
    private WebEngine webEngine;

    public CheatSheetToolingWindowPanel() {
        super(true);
        createUI();
    }

    private void createUI() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(createZoomIn());
        group.add(createZoomOut());
        final ActionManager actionManager = ActionManager.getInstance();
        final ActionToolbar actionToolBar = actionManager.createActionToolbar("AST", group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        setToolbar(buttonsPanel);
        centerPanel = new JFXPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createLineBorder(JBColor.BLACK));
        centerPanel.setAutoscrolls(true);
        createScene();
        add(centerPanel);

    }

    private AnAction createZoomIn() {
        return new AnAction("Zoom In", "Zoom in", AllIcons.General.Add) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                PlatformImpl.startup(() -> browser.setZoom(browser.getZoom() * 1.5));
            }
        };
    }

    private AnAction createZoomOut() {
        return new AnAction("Zoom Out", "Zoom out", AllIcons.General.Remove) {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                PlatformImpl.startup(() -> browser.setZoom(browser.getZoom() / 1.5));
            }
        };
    }

    @Override
    public void dispose() {

    }

    private void createScene() {

        PlatformImpl.startup(new Runnable() {
            @Override
            public void run() {

                stage = new StackPane();



                // Set up the embedded browser:
                browser = new WebView();
                webEngine = browser.getEngine();
                final InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream("cheatsheet/DWCheatSheet.md");
                String html = "";
                if (resourceAsStream != null) {
                    try {
                        html = WeaveEditorToolingAPI.toHtml(IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
                    } catch (IOException e) {

                    }
                }
                webEngine.loadContent(html);
                stage.getChildren().add(browser);
                Scene scene = new Scene(stage);
                centerPanel.setScene(scene);

            }
        });
    }
}
