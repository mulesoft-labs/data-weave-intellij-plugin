package org.mule.tooling.runtime.util;

import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.maven.client.api.model.RemoteRepository;

import java.net.MalformedURLException;
import java.net.URL;

public class MavenConfigurationUtils {
  public static MavenConfiguration createMavenConfiguration() {
    final MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder = MavenConfiguration.newMavenConfigurationBuilder();
    mavenConfigurationBuilder.forcePolicyUpdateNever(false);
    mavenConfigurationBuilder.localMavenRepositoryLocation(MuleDirectoriesUtils.getMavenTooling());
    addMavenCentralRemoteRepository(mavenConfigurationBuilder);
    addAnypointExchange(mavenConfigurationBuilder);
    addMuleReleases(mavenConfigurationBuilder);
    addMuleSnapshots(mavenConfigurationBuilder);
    mavenConfigurationBuilder.userSettingsLocation(MuleDirectoriesUtils.getSettingsXml());
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

  private static void addAnypointExchange(MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder) {
    try {
      mavenConfigurationBuilder.remoteRepository(RemoteRepository
          .newRemoteRepositoryBuilder()
          .id("anypoint-exchange")
          .url(new URL("https://maven.anypoint.mulesoft.com/api/v1/maven"))
          .build());
    } catch (MalformedURLException e) {
      //This should never happen
      throw new RuntimeException(e);
    }
  }

  private static void addMuleReleases(MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder) {
    try {
      mavenConfigurationBuilder.remoteRepository(RemoteRepository
          .newRemoteRepositoryBuilder()
          .id("mulesoft-releases")
          .url(new URL("https://repository.mulesoft.org/releases/"))
          .build());
    } catch (MalformedURLException e) {
      //This should never happen
      throw new RuntimeException(e);
    }
  }

  private static void addMuleSnapshots(MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder) {
    try {
      mavenConfigurationBuilder.remoteRepository(RemoteRepository
          .newRemoteRepositoryBuilder()
          .id("mulesoft-snapshots")
          .url(new URL("https://repository.mulesoft.org/snapshots/"))
          .build());
    } catch (MalformedURLException e) {
      //This should never happen
      throw new RuntimeException(e);
    }
  }

}
