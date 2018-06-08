package org.mule.tooling.runtime.tooling;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.tooling.client.api.ToolingRuntimeClient;
import org.mule.tooling.client.api.artifact.ToolingArtifact;
import org.mule.tooling.client.api.configuration.agent.AgentConfiguration;
import org.mule.tooling.client.api.connectivity.ConnectionValidationResult;
import org.mule.tooling.client.api.connectivity.ConnectivityTestingRequest;
import org.mule.tooling.client.api.connectivity.ConnectivityTestingService;
import org.mule.tooling.client.api.descriptors.ArtifactDescriptor;
import org.mule.tooling.client.api.extension.ExtensionModelService;
import org.mule.tooling.client.api.extension.model.ExtensionModel;
import org.mule.tooling.client.api.extension.model.XmlDslModel;
import org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrap;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ToolingClientManager implements ModuleComponent {

    public static final String MULE_VERSION = "4.1.3-SNAPSHOT";
    public static final String MULE_PLUGIN = "mule-plugin";
    private String toolingVersion = "4.1.2";
    private ToolingArtifact toolingArtifact;
    private ToolingRuntimeClient toolingRuntimeClient;
    private boolean started;
    private Module myModule;
    private Project myProject;

    private List<ToolingClientStatusListener> listeners;

    protected ToolingClientManager(Module module) {
        this.myModule = module;
        this.myProject = module.getProject();
        this.started = false;
        this.listeners = new ArrayList<>();

    }

    public static ToolingClientManager getInstance(Module module) {
        return module.getComponent(ToolingClientManager.class);
    }

    @Override
    public void moduleAdded() {
        //We should only start if the module is mule module
        if (MuleModuleUtils.isMuleModule(myModule)) {
            ProgressManager.getInstance().run(new Task.Backgroundable(myProject, "Initializing Tooling Client", true) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    init(indicator);
                }
            });
        }
    }

    public void init(ProgressIndicator progressIndicator) {
        try {
            final MuleRuntimeStatusChecker runtime = MuleRuntimeServerManager.getInstance().getOrStartRuntime(myProject, getMuleVersion(), progressIndicator);
            final MavenConfiguration mavenConfiguration = MavenConfigurationManager.getInstance().getMavenConfiguration();
            final MuleToolingSupport muleToolingSupport = new MuleToolingSupport();
            final ToolingRuntimeClientBootstrap toolingClientBootstrap = muleToolingSupport.createToolingClientBootstrap(getMuleVersion(), toolingVersion, mavenConfiguration, progressIndicator);
            AgentConfiguration.Builder builder = AgentConfiguration.builder();
            if (runtime != null) {
                //There may be no runtime
                builder.withToolingApiUrl(runtime.getToolingApiUrl());
            }
            AgentConfiguration build = builder.build();
            toolingRuntimeClient = muleToolingSupport.createToolingRuntimeClient(toolingClientBootstrap, mavenConfiguration, build, progressIndicator);
            final URL muleAppUrl = new File(getWorkingDirectory()).toURI().toURL();
            toolingArtifact = toolingRuntimeClient.newToolingArtifact(muleAppUrl, Collections.emptyMap());
            progressIndicator.setText("Tooling Client Service Started.");
            started = true;
            for (ToolingClientStatusListener listener : listeners) {
                listener.onToolingStarted();
            }
        } catch (Exception e) {
            Notifications.Bus.notify(new Notification("Mule Agent", "Unable to start mule aget", "Unable to start agent. Reason: \n" + e.getMessage(), NotificationType.ERROR));
        }
    }

    public void addStartListener(ToolingClientStatusListener listener) {
        if (started) {
            listener.onToolingStarted();
        }
        this.listeners.add(listener);
    }

    @NotNull
    public String getWorkingDirectory() {
        return "/Users/mariano.deachaval/Downloads/mule-app1";
    }

    public String getMuleVersion() {
        //TODO We should extract it from the pom.xml
        return MULE_VERSION;
    }


    public ConnectionValidationResult testConnection(String componentId) {
        final ConnectivityTestingService connectivityTestingService = toolingArtifact.connectivityTestingService();
        final ConnectivityTestingRequest connectivityTestingRequest = new ConnectivityTestingRequest();
        connectivityTestingRequest.setComponentId(componentId);
        return connectivityTestingService.testConnection(connectivityTestingRequest);
    }

    public boolean isStarted() {
        return started;
    }

    public Optional<SchemaPair> getSchema(String groupId, String artifactId, String version) {
        if (started) {
            final ArtifactDescriptor artifactDescriptor = ArtifactDescriptor.newBuilder().withGroupId(groupId).withArtifactId(artifactId).withVersion(version).withClassifier(MULE_PLUGIN).build();
            ExtensionModelService extensionModelService = toolingRuntimeClient.extensionModelService();
            Optional<String> extensionSchema = extensionModelService.loadExtensionSchema(artifactDescriptor);
            return extensionSchema.map((schema) -> {
                Optional<ExtensionModel> extensionModel = extensionModelService.loadExtensionModel(artifactDescriptor);
                return new SchemaPair(extensionModel.get().getXmlDslModel(), schema);
            });
        } else {
            return Optional.empty();
        }
    }


    public static class SchemaPair {
        XmlDslModel xmlDslModel;
        String schemaContent;

        public SchemaPair(XmlDslModel xmlDslModel, String schemaContent) {
            this.xmlDslModel = xmlDslModel;
            this.schemaContent = schemaContent;
        }

        public String getNamespace() {
            return xmlDslModel.getNamespace();
        }

        public String getSchemaLocation() {
            return xmlDslModel.getSchemaLocation();
        }

        public String getSchemaContent() {
            return schemaContent;
        }
    }


    public interface ToolingClientStatusListener {
        void onToolingStarted();
    }
}
