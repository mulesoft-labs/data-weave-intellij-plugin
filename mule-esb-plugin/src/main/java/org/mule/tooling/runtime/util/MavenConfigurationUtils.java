package org.mule.tooling.runtime.util;

import org.mule.maven.client.api.model.MavenConfiguration;

import java.io.File;
import java.util.Optional;

public class MavenConfigurationUtils {
  public static MavenConfiguration createMavenConfiguration() {
    final Optional<File> userSettingsLocation = Optional.of(MuleDirectoriesUtils.getSettingsXml());
    final Optional<File> globalSettingsLocation = Optional.empty();
    final MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder = MavenConfiguration.newMavenConfigurationBuilder();
    mavenConfigurationBuilder.forcePolicyUpdateNever(false);
    mavenConfigurationBuilder.localMavenRepositoryLocation(MuleDirectoriesUtils.getMavenTooling());
    userSettingsLocation.ifPresent(mavenConfigurationBuilder::userSettingsLocation);
    globalSettingsLocation.ifPresent(mavenConfigurationBuilder::globalSettingsLocation);
    return mavenConfigurationBuilder.build();
  }
}
