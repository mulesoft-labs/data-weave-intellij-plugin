package org.mule.tooling.restsdk.wizzard;

import static com.intellij.openapi.diagnostic.Logger.getInstance;
import static java.util.Collections.singletonList;
import static org.mule.tooling.restsdk.wizzard.ApiKind.OAS_JSON;

import com.intellij.ide.util.projectWizard.ModuleWizardStep;
import com.intellij.ide.util.projectWizard.SourcePathsBuilder;
import com.intellij.ide.util.projectWizard.WizardContext;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ui.configuration.ModulesProvider;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import com.intellij.openapi.diagnostic.Logger;

import org.mule.tooling.commons.wizard.maven.AbstractMavenBasedProjectBuilder;
import org.mule.tooling.lang.dw.WeaveIcons;

import javax.swing.Icon;

import java.io.File;
import java.util.Objects;
import java.util.Properties;

public class RestSdkModuleBuilder extends AbstractMavenBasedProjectBuilder implements SourcePathsBuilder {

    private static final Logger LOGGER = getInstance(RestSdkModuleBuilder.class);

    public static final String REST_SDK_DEFAULT_VERSION = "0.8.0-SNAPSHOT";

    public static final String M2_HOME_ENV = "M2_HOME";

    public static final String MAVEN_GROUP_ID_PROPERTY = "groupId";
    public static final String MAVEN_ARTIFACT_ID_PROPERTY = "artifactId";
    public static final String MAVEN_VERSION_PROPERTY = "version";

    public static final String MAVEN_ARCHETYPE_GROUP_ID_PROPERTY = "archetypeGroupId";
    public static final String MAVEN_ARCHETYPE_ARTIFACT_ID_PROPERTY = "archetypeArtifactId";
    public static final String MAVEN_ARCHETYPE_VERSION_PROPERTY = "archetypeVersion";
    public static final String MAVEN_ARCHETYPE_GENERATE_GOAL = "archetype:generate";

    public static final String REST_SDK_ARCHETYPE_GROUP_ID = "com.mulesoft.connectivity";
    public static final String REST_SDK_ARCHETYPE_ARTIFACT_ID = "rest-sdk-archetype";

    public static final String REST_SDK_ARCHETYPE_CONNECTOR_NAME_PROPERTY = "connectorName";
    public static final String REST_SDK_ARCHETYPE_API_FORMAT_PROPERTY = "apiSpecFormat";

    public static final String DEFAULT_CONNECTOR_GROUP_ID = "com.mulesoft.connectors";
    public static final String DEFAULT_CONNECTOR_ARTIFACT_ID = "mule4-product-connector";
    public static final String DEFAULT_CONNECTOR_VERSION = "1.0.0-SNAPSHOT";

    private final RestSdkConfigurationModel restSdkModel;

    public RestSdkModuleBuilder() {
        this.restSdkModel = new RestSdkConfigurationModel(REST_SDK_DEFAULT_VERSION, OAS_JSON);
        setProjectId(new MavenId(DEFAULT_CONNECTOR_GROUP_ID, DEFAULT_CONNECTOR_ARTIFACT_ID, DEFAULT_CONNECTOR_VERSION));
    }

    public ModuleWizardStep[] createWizardSteps(@NotNull WizardContext wizardContext, @NotNull ModulesProvider modulesProvider) {
        return new ModuleWizardStep[]{
                createMavenStep(),
                new RestSdkConfigurationStep(this.restSdkModel, getMavenModel())
        };
    }

    @Override
    public void setupRootModel(ModifiableRootModel rootModel) {
        final Project project = rootModel.getProject();
        super.setupRootModel(rootModel);
        final VirtualFile root = createAndGetContentEntry();
        rootModel.addContentEntry(root);

        MavenUtil.runWhenInitialized(project, (DumbAwareRunnable) () ->
            ProgressManager
            .getInstance()
            .run(new Task.Modal(rootModel.getProject(), "Initializing Rest SDK Connector Project", false) {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    indicator.setText("Generating connector sources");
                    invokeArchetype(new File(root.getPath()), restSdkModel, project);
                    indicator.setText("Initializing maven project");
                    MavenProjectsManager.getInstance(project).forceUpdateAllProjectsOrFindAllAvailablePomFiles();
                    root.refresh(false, true);
                }
            }));
    }

    private void invokeArchetype(File workingDir, RestSdkConfigurationModel restSdkModel, Project project) {
        LOGGER.debug("Working dir: " + workingDir);

        // There is an issue with the IntelliJ bundled maven
        // https://youtrack.jetbrains.com/issue/IDEA-139236
        // Which forces us to use the user installation instead
        // Looks like this happens in mac/linux systems.
        String mavenHome = System.getenv(M2_HOME_ENV);
        File mavenHomeFile = StringUtils.isNotBlank(mavenHome) ?
            new File(mavenHome) :
            MavenUtil.getEffectiveMavenHome(project, workingDir.getAbsolutePath()).getMavenHome();
        LOGGER.debug(M2_HOME_ENV + " : " + mavenHome);

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(mavenHomeFile);

        invoker.setWorkingDirectory(workingDir);
        invoker.setOutputHandler(System.out::println);

        InvocationRequest request = new DefaultInvocationRequest();
        request.setGoals(singletonList(MAVEN_ARCHETYPE_GENERATE_GOAL));
        request.setBatchMode(true);
        request.setOutputHandler(System.out::println);

        Properties properties = new Properties();
        properties.setProperty(MAVEN_ARCHETYPE_GROUP_ID_PROPERTY, REST_SDK_ARCHETYPE_GROUP_ID);
        properties.setProperty(MAVEN_ARCHETYPE_ARTIFACT_ID_PROPERTY, REST_SDK_ARCHETYPE_ARTIFACT_ID);
        properties.setProperty(MAVEN_ARCHETYPE_VERSION_PROPERTY, restSdkModel.getRestSdkVersion());

        properties.setProperty(MAVEN_GROUP_ID_PROPERTY, getProjectId().getGroupId());
        properties.setProperty(MAVEN_ARTIFACT_ID_PROPERTY, getProjectId().getArtifactId());
        properties.setProperty(MAVEN_VERSION_PROPERTY, getProjectId().getVersion());
        properties.setProperty(REST_SDK_ARCHETYPE_CONNECTOR_NAME_PROPERTY, restSdkModel.getConnectorName());
        properties.setProperty(REST_SDK_ARCHETYPE_API_FORMAT_PROPERTY, restSdkModel.getApiKind().name());

        request.setProperties(properties);

        try {
            InvocationResult result = invoker.execute(request);
            if(result.getExitCode() != 0){
                throw new RuntimeException("Invalid exit code");
            }
        } catch (MavenInvocationException e) {
            throw new RuntimeException("An error occurred when executing maven", e);
        }
    }

    private VirtualFile createAndGetContentEntry() {
        final String path = FileUtil.toSystemIndependentName(Objects.requireNonNull(this.getContentEntryPath()));
        boolean mkdirs = new File(path).mkdirs();
        if (!mkdirs) {
            System.out.println("Unable to create " + path);
        }
        return LocalFileSystem.getInstance().refreshAndFindFileByPath(path);
    }

    @Override
    public String getName() {
        return "RestSdk Project";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getPresentableName() {
        return "RestSdk Project";
    }

    @Override
    public Icon getNodeIcon() {
        return WeaveIcons.RestSdkIcon;
    }

    @Override
    public String getDescription() {
        return "Create a RestSdk project.";
    }

}
