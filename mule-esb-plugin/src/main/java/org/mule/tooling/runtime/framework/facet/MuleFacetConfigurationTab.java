package org.mule.tooling.runtime.framework.facet;

import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Created by eberman on 3/26/17.
 */
public class MuleFacetConfigurationTab extends FacetEditorTab {
    private JPanel top;
    private JTextField myPath;
    private JComboBox domainCombo;
    private JPanel customDomainPanel;
    private JTextField groupIdTextField;
    private JTextField artifactIdTextField;
    private JTextField versionTextField;

    MuleFacetConfiguration muleFacetConfiguration;

    public MuleFacetConfigurationTab(MuleFacetConfiguration configuration) {
        muleFacetConfiguration = configuration;
    }

    @NotNull
    @Override
    public JComponent createComponent() {
        return top;
    }

    @Override
    public boolean isModified() {
        return !myPath.getText().equals(muleFacetConfiguration.getPathToSdk());
    }

    @Override
    public void reset() {
        myPath.setText(muleFacetConfiguration.getPathToSdk());
    }

    public void apply() throws ConfigurationException {
        muleFacetConfiguration.setPathToSdk(myPath.getText());
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Mule SDK";
    }

//    public void onFacetInitialized(@NotNull Facet facet) {
//        ToolWindowManager.getInstance(facet.getModule().getProject()).registerToolWindow("Global Configs", true, ToolWindowAnchor.LEFT, false);
//    }


    public JPanel getTop() {
        return top;
    }

    public void setTop(JPanel top) {
        this.top = top;
    }

    public JTextField getMyPath() {
        return myPath;
    }

    public void setMyPath(JTextField myPath) {
        this.myPath = myPath;
    }

    public JComboBox getDomainCombo() {
        return domainCombo;
    }

    public void setDomainCombo(JComboBox domainCombo) {
        this.domainCombo = domainCombo;
    }

    public JPanel getCustomDomainPanel() {
        return customDomainPanel;
    }

    public void setCustomDomainPanel(JPanel customDomainPanel) {
        this.customDomainPanel = customDomainPanel;
    }

    public JTextField getGroupIdTextField() {
        return groupIdTextField;
    }

    public void setGroupIdTextField(JTextField groupIdTextField) {
        this.groupIdTextField = groupIdTextField;
    }

    public JTextField getArtifactIdTextField() {
        return artifactIdTextField;
    }

    public void setArtifactIdTextField(JTextField artifactIdTextField) {
        this.artifactIdTextField = artifactIdTextField;
    }

    public JTextField getVersionTextField() {
        return versionTextField;
    }

    public void setVersionTextField(JTextField versionTextField) {
        this.versionTextField = versionTextField;
    }
}
