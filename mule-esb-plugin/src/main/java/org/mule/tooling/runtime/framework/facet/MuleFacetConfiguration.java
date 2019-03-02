package org.mule.tooling.runtime.framework.facet;

import com.intellij.facet.FacetConfiguration;
import com.intellij.facet.ui.FacetEditorContext;
import com.intellij.facet.ui.FacetEditorTab;
import com.intellij.facet.ui.FacetValidatorsManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.apache.commons.lang.StringUtils;
import org.jdom.Element;
import org.jetbrains.idea.maven.model.MavenArtifact;
import org.json.JSONObject;

import java.util.List;

public class MuleFacetConfiguration implements FacetConfiguration {
    public static final String MULE_FACET_TAG_NAME = "MuleFacet";
    public static final String PATH_TO_SDK_ATTR_NAME = "pathToSdk";

    private JSONObject muleArtifact;
    private MavenArtifact domainArtifact;
    private List<String> domainsList;

    private String myPathToSdk = "";

    MuleFacetConfigurationTab tab;

    @Override
    public FacetEditorTab[] createEditorTabs(FacetEditorContext context, FacetValidatorsManager manager) {
        return new FacetEditorTab[] { getConfigurationTab() };
    }

    public void setPathToSdk(String path) {
        myPathToSdk = path;
        getConfigurationTab().getMyPath().setText(path);
    }

    public void setDomains(List domainsList, MavenArtifact domainArtifact) {
        this.domainsList = domainsList;
        this.domainArtifact = domainArtifact;

        for (Object o : domainsList)
            getConfigurationTab().getDomainCombo().addItem(o);

        if (domainArtifact != null) {
            int idx = domainsList.indexOf(domainArtifact.getArtifactId());
            if (idx != -1) {
                getConfigurationTab().getDomainCombo().setSelectedIndex(idx);
                getConfigurationTab().getCustomDomainPanel().setVisible(false);
            } else {
                getConfigurationTab().getDomainCombo().setSelectedIndex(domainsList.indexOf("Custom..."));
                getConfigurationTab().getCustomDomainPanel().setVisible(true);
            }
        } else {
            getConfigurationTab().getDomainCombo().setSelectedIndex(domainsList.indexOf("default"));
        }
    }

    public String getPathToSdk() {
        return myPathToSdk;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        Element facet = element.getChild(MULE_FACET_TAG_NAME);
        if (facet != null) {
            myPathToSdk = facet.getAttributeValue(PATH_TO_SDK_ATTR_NAME, "");
            getConfigurationTab().getMyPath().setText(myPathToSdk);
        }
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        Element facet = new Element(MULE_FACET_TAG_NAME);
        facet.setAttribute(PATH_TO_SDK_ATTR_NAME, StringUtils.isEmpty(myPathToSdk) ? getConfigurationTab().getMyPath().getText() : myPathToSdk);
        element.addContent(facet);
    }

    private MuleFacetConfigurationTab getConfigurationTab() {
        if (tab == null)
            tab = new MuleFacetConfigurationTab(this);
        return tab;
    }

    public JSONObject getMuleArtifact() {
        return muleArtifact;
    }

    public void setMuleArtifact(JSONObject muleArtifact) {
        this.muleArtifact = muleArtifact;
    }

    private void persistJsonArtifact() {

    }
}
