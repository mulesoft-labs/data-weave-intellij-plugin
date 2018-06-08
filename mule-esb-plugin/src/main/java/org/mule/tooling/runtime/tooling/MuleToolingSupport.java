package org.mule.tooling.runtime.tooling;

import com.intellij.openapi.progress.ProgressIndicator;
import org.mule.maven.client.api.model.MavenConfiguration;
import org.mule.tooling.client.api.ToolingRuntimeClient;
import org.mule.tooling.client.api.configuration.agent.AgentConfiguration;
import org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrap;
import org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrapConfiguration;

import static org.mule.tooling.client.bootstrap.api.ToolingRuntimeClientBootstrapFactory.newToolingRuntimeClientBootstrap;

public class MuleToolingSupport {
    public ToolingRuntimeClientBootstrap createToolingClientBootstrap(
            String muleVersion,
            String toolingVersion, MavenConfiguration mavenConfiguration,
            ProgressIndicator progressIndicator) {

        final ToolingRuntimeClientBootstrapConfiguration toolingRuntimeClientBootstrapConfiguration = ToolingRuntimeClientBootstrapConfiguration.builder()
                .muleVersion(muleVersion)
                .toolingVersion(toolingVersion)
//            .log4jConfiguration(new File("/Users/julianesevich/localwork/projects/playground-intellij/data-weave-intellij-plugin/mule-app-plugin/src/test/resources/log4j2-test.xml").toURI())
                .mavenConfiguration(mavenConfiguration)
                .build();
        progressIndicator.setText2("Initializing Tooling Bootstrap");
        final ToolingRuntimeClientBootstrap toolingRuntimeClientBootstrap = newToolingRuntimeClientBootstrap(toolingRuntimeClientBootstrapConfiguration);
        progressIndicator.setText2("Tooling Bootstrap Initialized");
        return toolingRuntimeClientBootstrap;
    }

    public ToolingRuntimeClient createToolingRuntimeClient(ToolingRuntimeClientBootstrap toolingRuntimeClientBootstrap,
                                                           MavenConfiguration mavenConfiguration,
                                                           AgentConfiguration agentConfiguration,
                                                           ProgressIndicator progressIndicator) {
        progressIndicator.setText2("Creating Tooling Runtime Client");
        final ToolingRuntimeClient.Builder builder = toolingRuntimeClientBootstrap.getToolingRuntimeClientBuilderFactory().create();
        final ToolingRuntimeClient build = builder
                .withMavenConfiguration(mavenConfiguration)
                .withRemoteAgentConfiguration(agentConfiguration)
                .build();
        progressIndicator.setText2("Tooling Runtime Created");
        return build;
    }


}
