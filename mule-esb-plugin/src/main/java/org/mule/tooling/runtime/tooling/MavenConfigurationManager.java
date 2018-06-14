package org.mule.tooling.runtime.tooling;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.maven.client.api.model.RemoteRepository;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MavenConfigurationManager implements ApplicationComponent {


    private MavenConfiguration mavenConfiguration;

    public static MavenConfigurationManager getInstance() {
        return ApplicationManager.getApplication().getComponent(MavenConfigurationManager.class);
    }

    public MavenConfigurationManager() {
    }

    public void initComponent() {
        this.mavenConfiguration = createMavenConfiguration();
    }

    public MavenConfiguration getMavenConfiguration() {
        return mavenConfiguration;
    }

    private MavenConfiguration createMavenConfiguration() {
        final List<RemoteRepository> externalRepositories = new ArrayList<>();
        final String currentUsersHomeDir = System.getProperty("user.home");

        boolean forcePolicyUpdateNever = true;
        //TODO fix
        final File localMavenRepositoryLocation = new File(currentUsersHomeDir + File.separator + ".m2");
        final File localMavenRepositoryFolderLocation = new File(localMavenRepositoryLocation, "repository");
        localMavenRepositoryFolderLocation.mkdir();
        final Optional<File> userSettingsLocation = Optional.of(new File(localMavenRepositoryLocation, "settings.xml"));
        final Optional<File> globalSettingsLocation = Optional.empty();
        final MavenConfiguration.MavenConfigurationBuilder mavenConfigurationBuilder = MavenConfiguration.newMavenConfigurationBuilder();
        mavenConfigurationBuilder.forcePolicyUpdateNever(forcePolicyUpdateNever);

        mavenConfigurationBuilder.localMavenRepositoryLocation(localMavenRepositoryFolderLocation);
        userSettingsLocation.ifPresent(mavenConfigurationBuilder::userSettingsLocation);
        globalSettingsLocation.ifPresent(mavenConfigurationBuilder::globalSettingsLocation);
        externalRepositories.forEach(mavenConfigurationBuilder::remoteRepository);
        return mavenConfigurationBuilder.build();
    }
}
