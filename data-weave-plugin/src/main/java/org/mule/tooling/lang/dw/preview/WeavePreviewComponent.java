package org.mule.tooling.lang.dw.preview;

import com.intellij.execution.ui.RunnerLayoutUi;
import com.intellij.execution.ui.layout.PlaceInGrid;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.content.Content;
import com.intellij.util.Alarm;
import com.intellij.util.ui.JBUI;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.DataWeaveScenariosManager;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.weave.v2.debugger.event.PreviewExecutedSuccessfulEvent;

import javax.swing.*;
import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class WeavePreviewComponent implements Disposable {


    private Project myProject;
    private ComboBox<Scenario> scenariosComboBox;
    private BorderLayoutPanel previewPanel;
    private PsiFile currentFile;
    private boolean runOnChange = true;

    private Alarm myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);

    private InputsComponent inputsComponent;
    private final OutputComponent outputComponent;

    public WeavePreviewComponent() {
        inputsComponent = new InputsComponent();
        outputComponent = new OutputComponent();
    }

    public JComponent createComponent(Project project) {
        myProject = project;

        PsiManager.getInstance(project).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                if (currentFile == null || !runOnChange) {
                    return;
                }

                //We call all the request and add a new one if in 200 milliseconds no change was introduce then trigger preview
                myDocumentAlarm.cancelAllRequests();
                myDocumentAlarm.addRequest(() -> {
                    if (myDocumentAlarm.isDisposed())
                        return;
                    runPreview();
                }, WeaveConstants.MODIFICATIONS_DELAY);

                //We know the change came from this file now
            }
        }, this);

        return createPreviewPanel();
    }

    public PsiFile getCurrentFile() {
        return currentFile;
    }

    private ComboBoxModel<Scenario> createModel(List<Scenario> scenarios) {
        return new DefaultComboBoxModel<>(scenarios.toArray(new Scenario[0]));
    }

    private JComponent createPreviewPanel() {
        previewPanel = new BorderLayoutPanel();

        final JPanel chooserPanel = createScenarioSelectorPanel();

        RunnerLayoutUi layoutUi = RunnerLayoutUi.Factory.getInstance(myProject).create("DW-Preview", "DW Preview", myProject.getName(), myProject);
        Content inputsContent = layoutUi.createContent("inputs", inputsComponent.createComponent(myProject), "Inputs", null, null);
        inputsContent.setCloseable(false);
        layoutUi.addContent(inputsContent, 0, PlaceInGrid.left, false);

        Content outputContent = layoutUi.createContent("output", outputComponent.createComponent(myProject), "Output", null, null);
        outputContent.setCloseable(false);
        layoutUi.addContent(outputContent, 1, PlaceInGrid.right, false);

        previewPanel.addToTop(chooserPanel);
        previewPanel.addToCenter(layoutUi.getComponent());
        return previewPanel;
    }


    /**
     * Sets the current PsiFile and loads its first scenario
     */
    public void open(@Nullable PsiFile psiFile) {
        if (currentFile == psiFile) {
            return;
        }
        if (psiFile != null && psiFile.getFileType() == WeaveFileType.getInstance()) {
            this.currentFile = psiFile;
            DataWeaveScenariosManager instance = DataWeaveScenariosManager.getInstance(myProject);
            WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
            List<Scenario> scenarios = instance.getScenariosFor(weaveDocument);
            scenariosComboBox.setModel(createModel(scenarios));
            //Load first scenario
            if (!scenarios.isEmpty()) {
                Scenario currentScenarioFor = instance.getCurrentScenarioFor(weaveDocument);
                if (currentScenarioFor != null) {
                    loadScenario(currentScenarioFor);
                }
            }
        }
    }


    @NotNull
    private JPanel createScenarioSelectorPanel() {
        scenariosComboBox = new ComboBox<>();
        scenariosComboBox.setRenderer(new ScenarioNameRenderer());
        scenariosComboBox.addActionListener(evt -> {
            Scenario scenario = (Scenario) scenariosComboBox.getSelectedItem();
            if (scenario != null) {
                DataWeaveScenariosManager.getInstance(myProject).setCurrentScenario(getCurrentWeaveDocument(), scenario);
                loadScenario(scenario);
            }
        });

        final JPanel chooserPanel = new JPanel(new GridBagLayout());
        final JLabel scopesLabel = new JLabel("Scenario:");
        scopesLabel.setDisplayedMnemonic('S');
        scopesLabel.setLabelFor(scenariosComboBox);
        final GridBagConstraints gc =
                new GridBagConstraints(
                        GridBagConstraints.RELATIVE,
                        0,
                        1,
                        1,
                        0,
                        0,
                        GridBagConstraints.WEST,
                        GridBagConstraints.NONE,
                        JBUI.insets(2),
                        0,
                        0);

        chooserPanel.add(scopesLabel, gc);
        chooserPanel.add(scenariosComboBox, gc);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        chooserPanel.add(Box.createHorizontalBox(), gc);

        return chooserPanel;
    }

    private WeaveDocument getCurrentWeaveDocument() {
        return WeavePsiUtils.getWeaveDocument(currentFile);
    }

    private void loadScenario(Scenario scenario) {
        final VirtualFile inputs = scenario.getInputs();
        if (inputs != null && inputs.isDirectory()) {
            inputsComponent.loadInputFiles(inputs);
        }


        outputComponent.runPreview(scenario, currentFile);
    }

    @NotNull
    public String getContent(PreviewExecutedSuccessfulEvent result) {
        try {
            return new String(result.result(), result.mimeType());
        } catch (UnsupportedEncodingException e) {
            return new String(result.result());
        }
    }

    @Override
    public void dispose() {
        //        this.previewPanel.grabFocus();
        this.scenariosComboBox.removeAllItems();
        this.currentFile = null;

        inputsComponent.dispose();
        outputComponent.dispose();
    }

    public void runPreview() {
        Scenario selectedItem = (Scenario) scenariosComboBox.getSelectedItem();
        outputComponent.runPreview(selectedItem, currentFile);
    }

    public boolean runAvailable() {
        if (currentFile != null && scenariosComboBox != null) {
            Scenario selectedItem = (Scenario) scenariosComboBox.getSelectedItem();
            return selectedItem != null;
        } else {
            return false;
        }
    }

    public boolean runOnChange() {
        return runOnChange;
    }

    public void runOnChange(boolean state) {
        this.runOnChange = state;
    }


    private static class ScenarioNameRenderer extends ListCellRendererWrapper<Scenario> {
        @Override
        public void customize(JList list, Scenario value, int index, boolean selected, boolean hasFocus) {
            if (value != null) {
                setText(value.getPresentableText());
            }
        }
    }
}
