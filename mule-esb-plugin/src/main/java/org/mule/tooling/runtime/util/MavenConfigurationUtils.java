package org.mule.tooling.runtime.util;

import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.maven.client.api.model.RemoteRepository;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;

public class MavenConfigurationUtils {
  public static MavenConfiguration createMavenConfiguration() {
    final Optional<File> userSettingsLocation = Optional.of(MuleDirectoriesUtils.getSettingsXml());
    final Optional<File> globalSettingsLocation = Optional.empty();
    final MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder = MavenConfiguration.newMavenConfigurationBuilder();
    mavenConfigurationBuilder.forcePolicyUpdateNever(false);
    mavenConfigurationBuilder.localMavenRepositoryLocation(MuleDirectoriesUtils.getMavenTooling());
    addMavenCentralRemoteRepository(mavenConfigurationBuilder);
    userSettingsLocation.ifPresent(mavenConfigurationBuilder::userSettingsLocation);
    globalSettingsLocation.ifPresent(mavenConfigurationBuilder::globalSettingsLocation);
    return mavenConfigurationBuilder.build();
  }

  private static void addMavenCentralRemoteRepository(MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder) {
    try {
        mavenConfigurationBuilder.remoteRepository(RemoteRepository
                .newRemoteRepositoryBuilder()
                .id("maven-central")
                .url(new URL("https://repo.maven.apache.org/maven2/"))
                .build());
    } catch (MalformedURLException e) {
      //This should never happen
      throw new RuntimeException(e);
    }
  }
}
