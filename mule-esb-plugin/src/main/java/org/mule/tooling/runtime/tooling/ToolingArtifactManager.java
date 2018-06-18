package org.mule.tooling.runtime.tooling;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleComponent;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Producer;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.mule.tooling.client.api.artifact.ToolingArtifact;
import org.mule.tooling.client.api.connectivity.ConnectionValidationResult;
import org.mule.tooling.client.api.connectivity.ConnectivityTestingRequest;
import org.mule.tooling.client.api.connectivity.ConnectivityTestingService;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;
import org.mule.tooling.runtime.util.MuleModuleUtils;
import org.mule.tools.api.classloader.model.ArtifactCoordinates;
import org.mule.tools.api.packager.AbstractProjectFoldersGenerator;
import org.mule.tools.api.packager.DefaultProjectInformation;
import org.mule.tools.api.packager.Pom;
import org.mule.tools.api.packager.ProjectFoldersGeneratorFactory;
import org.mule.tools.api.packager.archiver.MuleExplodedArchiver;
import org.mule.tools.api.packager.builder.MulePackageBuilder;
import org.mule.tools.api.packager.packaging.PackagingOptions;
import org.mule.tools.api.packager.sources.MuleContentGenerator;
import org.mule.tools.api.validation.exchange.ExchangeRepositoryMetadata;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class ToolingArtifactManager implements ModuleComponent {

  public static final String MULE_PLUGIN = "mule-plugin";

  private ToolingArtifact toolingArtifact;
  private Module myModule;

  protected ToolingArtifactManager(Module module) {
    this.myModule = module;
  }

  public static ToolingArtifactManager getInstance(Module module) {
    return module.getComponent(ToolingArtifactManager.class);
  }

  private <T> T callWithToolingArtifact(Function<ToolingArtifact, T> callback, Producer<T> defaultValue) {
    return ToolingRuntimeManager.getInstance().callOnToolingRuntime(myModule.getProject(), MuleRuntimeServerManager.getMuleVersionOf(myModule), (toolingRuntimeClient) -> {
      try {
        createMuleAppForTooling();
        toolingArtifact = toolingRuntimeClient.newToolingArtifact(MuleDirectoriesUtils.getMuleWorkingDirectory(myModule).toURI().toURL(), Collections.emptyMap());
        return callback.apply(toolingArtifact);
      } catch (Exception e) {
        Notifications.Bus.notify(new Notification("Mule Tooling Client", "Unable to start mule tooling client", "Unable to start mule tooling client. Reason: \n" + e.getMessage(), NotificationType.ERROR));
        return defaultValue.produce();
      }
    }, defaultValue);
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

      DefaultProjectInformation.Builder projectInfoBuilder = new DefaultProjectInformation.Builder();
      projectInfoBuilder.isDeployment(false)
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

      DefaultProjectInformation projectInformation = projectInfoBuilder.build();


      final AbstractProjectFoldersGenerator foldersGenerator = ProjectFoldersGeneratorFactory.create(projectInformation);
      foldersGenerator.generate(buildDirectory.toPath());

      final MuleContentGenerator muleContentGenerator = new MuleContentGenerator(projectInformation);
      muleContentGenerator.createMuleSrcFolderContent();
      muleContentGenerator.createDescriptors();
      muleContentGenerator.createTestFolderContent();
      muleContentGenerator.createMetaInfMuleSourceFolderContent();
      muleContentGenerator.createContent();

      final File wdForMuleApp = MuleDirectoriesUtils.getMuleWorkingDirectory(myModule);
      if (wdForMuleApp.exists()) {
        FileUtils.deleteDirectory(wdForMuleApp);
      }
      MulePackageBuilder mulePackageBuilder = new MulePackageBuilder();
      mulePackageBuilder.withArchiver(new MuleExplodedArchiver());
      mulePackageBuilder.withPackagingOptions(new PackagingOptions(false, true, false, false));
      mulePackageBuilder.createPackage(buildDirectory.toPath(), wdForMuleApp.toPath());
    }
  }


  public Optional<ConnectionValidationResult> testConnection(String componentId) {
    return callWithToolingArtifact((toolingArtifact) -> {
      final ConnectivityTestingService connectivityTestingService = toolingArtifact.connectivityTestingService();
      final ConnectivityTestingRequest connectivityTestingRequest = new ConnectivityTestingRequest();
      connectivityTestingRequest.setComponentId(componentId);
      return Optional.of(connectivityTestingService.testConnection(connectivityTestingRequest));
    }, Optional::empty);
  }

  public static class MulePomAdapter implements Pom {

    @Override
    public void persist(Path path) {

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
