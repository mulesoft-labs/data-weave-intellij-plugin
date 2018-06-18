package org.mule.tooling.lang.dw.preview;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.JBTabsPaneImpl;
import com.intellij.ui.tabs.TabInfo;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class InputsComponent implements Disposable {

    private List<Editor> editors = new ArrayList<>();
    private JBTabsPaneImpl inputTabs;
    private Project myProject;
    private PsiFile currentFile;
    private BorderLayoutPanel inputPanel;

    public InputsComponent() {
    }

    public JComponent createComponent(Project project) {
        myProject = project;
        return createInputsPanel();
    }

    public PsiFile getCurrentFile() {
        return currentFile;
    }

    private JComponent createInputsPanel() {
        inputPanel = new BorderLayoutPanel();
        inputTabs = new JBTabsPaneImpl(myProject, SwingConstants.TOP, myProject);
        inputPanel.add(inputTabs.getComponent());
        return inputPanel;
    }

    public void loadInputFiles(VirtualFile inputsFolder) {
        closeAllInputs();
        List<VirtualFile> children = VfsUtil.collectChildrenRecursively(inputsFolder);
        for (VirtualFile input : children) {
            if (input.isDirectory()) {
                continue;
            }
            PsiFile file = PsiManager.getInstance(myProject).findFile(input);
            if (file == null) {
                continue;
            }

            Document document = file.getViewProvider().getDocument();
            if (document == null) {
                continue;
            }
            Editor editor = EditorFactory.getInstance().createEditor(document, myProject, input, false);
            editors.add(editor);
            TabInfo tabInfo = createTabInfo(inputsFolder, input, file, editor);
            inputTabs.getTabs().addTab(tabInfo);
            inputTabs.setSelectedIndex(0);
        }
    }

    @NotNull
    private TabInfo createTabInfo(VirtualFile inputs, VirtualFile input, PsiFile file, Editor editor) {
        TabInfo tabInfo = new TabInfo(inputTabs.getComponent());
        tabInfo.setPreferredFocusableComponent(null);
        ItemPresentation presentation = file.getPresentation();
        if (presentation != null) {
            final String relativeLocation = VfsUtil.getRelativeLocation(input, inputs);
            assert relativeLocation != null;
            String expression = relativeLocation.replace('/', '.');
            String extension = input.getExtension();
            if (extension != null) {
                //extension doesn't have the dot so we need to add a + 1
                expression = expression.substring(0, expression.length() - (extension.length() + 1)) + " (" + StringUtil.capitalize(extension) + ")";
            }
            tabInfo.setText(expression);
            tabInfo.setIcon(presentation.getIcon(false));
        }
        tabInfo.setComponent(editor.getComponent());
        return tabInfo;
    }

    public void closeAllInputs() {
//        inputPanel.grabFocus();
        inputTabs.getTabs().removeAllTabs();
    }

    @Override
    public void dispose() {
        //        this.inputPanel.grabFocus();
        for (int i = 0; i < editors.size(); i++) {
            Editor editor = editors.get(i);
            if (!editor.isDisposed()) {
                EditorFactory.getInstance().releaseEditor(editor);
            }
        }
        this.editors.clear();
        this.currentFile = null;
    }

}
