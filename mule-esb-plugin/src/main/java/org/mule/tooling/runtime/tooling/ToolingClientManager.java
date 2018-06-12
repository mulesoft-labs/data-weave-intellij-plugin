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
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
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
import org.mule.tools.api.classloader.model.ArtifactCoordinates;
import org.mule.tools.api.packager.DefaultProjectInformation;
import org.mule.tools.api.packager.Pom;
import org.mule.tools.api.packager.archiver.MuleExplodedArchiver;
import org.mule.tools.api.packager.builder.MulePackageBuilder;
import org.mule.tools.api.packager.packaging.PackagingOptions;
import org.mule.tools.api.packager.sources.MuleContentGenerator;
import org.mule.tools.api.validation.exchange.ExchangeRepositoryMetadata;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
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
            final AgentConfiguration.Builder builder = AgentConfiguration.builder();
            if (runtime != null) {
                //There may be no runtime
                builder.withToolingApiUrl(runtime.getToolingApiUrl());
            }
            final AgentConfiguration build = builder.build();
            toolingRuntimeClient = muleToolingSupport.createToolingRuntimeClient(toolingClientBootstrap, mavenConfiguration, build, progressIndicator);
            final URL muleAppUrl = getMuleAppWorkingDir().toURI().toURL();

            createMuleAppForTooling();

            toolingArtifact = toolingRuntimeClient.newToolingArtifact(muleAppUrl, Collections.emptyMap());
            progressIndicator.setText("Tooling Client Service Started.");
            started = true;
            for (ToolingClientStatusListener listener : listeners) {
                listener.onToolingStarted();
            }


        } catch (Exception e) {
            e.printStackTrace();
            Notifications.Bus.notify(new Notification("Mule Agent", "Unable to start mule agent", "Unable to start agent. Reason: \n" + e.getMessage(), NotificationType.ERROR));
        }
    }

    public void addStartListener(ToolingClientStatusListener listener) {
        if (started) {
            listener.onToolingStarted();
        }
        this.listeners.add(listener);
    }

    public File getMuleIdeWorkingDir() {
        return new File(getModuleHome(), ".mule_ide");
    }

    @NotNull
    private String getModuleHome() {
        VirtualFile moduleFile = myModule.getModuleFile();
        if (moduleFile == null) {
            return myModule.getProject().getBasePath();
        } else {
            return moduleFile.getParent().getPath();
        }
    }

    public void createMuleAppForTooling() throws IOException {
        MavenProject mavenProject = MuleModuleUtils.getMavenProject(myModule);
        if (mavenProject != null) {
            MavenId mavenId = mavenProject.getMavenId();
            String groupId = mavenId.getGroupId();
            String artifactId = mavenId.getArtifactId();
            String version = mavenId.getVersion();
            String packaging = mavenProject.getPackaging();
            File moduleHome = new File(getModuleHome());
            //We should take this from the module configuration
            File buildDirectory = new File(moduleHome, "target");
            if (!buildDirectory.exists()) {
                buildDirectory.mkdirs();
            }
            DefaultProjectInformation.Builder builder = new DefaultProjectInformation.Builder();
            builder.isDeployment(false)
                    .setTestProject(false)
                    .withArtifactId(artifactId)
                    .withGroupId(groupId)
                    .withVersion(version)
                    .withPackaging(packaging)
                    .withBuildDirectory(buildDirectory.toPath())
                    .withProjectBaseFolder(moduleHome.toPath())
                    .withResolvedPom(new MulePomAdapter())
                    .withDependencyProject(new MuleProjectAdapter())
                    .withExchangeRepositoryMetadata(new ExchangeRepositoryMetadata());

            MuleContentGenerator muleContentGenerator = new MuleContentGenerator(builder.build());
            muleContentGenerator.createMuleSrcFolderContent();
            muleContentGenerator.createDescriptors();
            muleContentGenerator.createTestFolderContent();
            muleContentGenerator.createMetaInfMuleSourceFolderContent();
            muleContentGenerator.createContent();

            File wdForMuleApp = getMuleAppWorkingDir();
            if (wdForMuleApp.exists()) {
                FileUtils.deleteDirectory(wdForMuleApp);
            }
            MulePackageBuilder mulePackageBuilder = new MulePackageBuilder();
            mulePackageBuilder.withArchiver(new MuleExplodedArchiver());
            mulePackageBuilder.withPackagingOptions(new PackagingOptions(false, true, false, false));
            mulePackageBuilder.createPackage(buildDirectory.toPath(), wdForMuleApp.toPath());
        }

    }


    @NotNull
    private File getMuleAppWorkingDir() {
        final File muleIdeWorkingDir = getMuleIdeWorkingDir();
        final File apps = new File(muleIdeWorkingDir, "apps");
        if (!apps.exists()) {
            apps.mkdirs();
        }
        return apps;
    }

    public String getMuleVersion() {
        //TODO We should extract it from the json min mule version
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

    public static class MulePomAdapter implements Pom {

        @Override
        public void persist(Path path) throws IOException {

        }

        @Override
        public List<Path> getResourcesLocation() {
            return Arrays.asList();
        }
    }


    public static class MuleProjectAdapter implements org.mule.tools.api.util.Project {

        @Override
        public List<ArtifactCoordinates> getDependencies() {
            return Arrays.asList();
        }
    }
}
