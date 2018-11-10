package org.mule.tooling.runtime.framework.facet;

import com.intellij.facet.Facet;
import com.intellij.facet.FacetType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.json.JSONObject;
import org.mule.tooling.runtime.util.MuleModuleUtils;
//import org.mule.tooling.esb.toolwindow.globalconfigs.GlobalConfigsToolWindowPanel;
//import org.mule.tooling.esb.util.MuleIcons;

import java.util.Arrays;
import java.util.List;

public class MuleFacet extends Facet<MuleFacetConfiguration> {

    static final Logger logger = Logger.getInstance(MuleFacet.class);

    public static final String ID = "MULE_FACET_ID";

    public MuleFacet(FacetType facetType,
                     Module module,
                     String name,
                     MuleFacetConfiguration configuration,
                     Facet underlyingFacet) {
        super(facetType, module, name, configuration, underlyingFacet);
    }

    @Override
    public void initFacet() {

        ApplicationManager.getApplication().invokeLater(new Runnable() {
            @Override
            public void run() {
                //Initialize JSON Artifact handler
                VirtualFile jsonArtifact = MuleModuleUtils.getMuleArtifactJson(getModule());
                if (jsonArtifact != null) {
                    try {
                        String jsonString = new String(jsonArtifact.contentsToByteArray());
                        JSONObject jsonObject = new JSONObject(jsonString);
                        getConfiguration().setMuleArtifact(jsonObject);
                    } catch (Exception e) {
                        logger.error(e);
                    }
                }

                ToolWindowManager manager = ToolWindowManager.getInstance(MuleFacet.this.getModule().getProject());
                List<String> ids = Arrays.asList(manager.getToolWindowIds());

                if (manager.getToolWindow("Global Configs") == null && !ids.contains("Global Configs")) {
//TODO Global Configs
                    /*
                    try {
                        ToolWindow toolWindow = manager.registerToolWindow("Global Configs", true, ToolWindowAnchor.LEFT, false);
                        toolWindow.setIcon(MuleIcons.MuleIcon);

                        GlobalConfigsToolWindowPanel toolWindowPanel = new GlobalConfigsToolWindowPanel(MuleFacet.this.getModule().getProject());
                        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
                        Content content = contentFactory.createContent(toolWindowPanel, "", true);
                        toolWindow.getContentManager().addContent(content);
                    } catch (Exception e) {
                        logger.error("Unable to initialize toolWindow: ", e);
                    }
                    */
                }

                //TODO - configuration for domains
            }
        });
    }

    @Override
    public void disposeFacet() {
        try {
            ToolWindowManager.getInstance(this.getModule().getProject()).unregisterToolWindow("Global Configs");
        } catch (Exception e) {}
    }
}
