package org.mule.tooling.commons.wizard.maven;

import javax.swing.*;

public class MavenCoordsConfigurationStep {
    private JTextField version;
    private JPanel container;
    private JTextField groupId;
    private JTextField artifactId;

    public MavenCoordsConfigurationStep() {

    }


    public JTextField getVersion() {
        return version;
    }

    public JTextField getGroupId() {
        return groupId;
    }

    public JTextField getArtifactId() {
        return artifactId;
    }

    public JPanel getContainer() {
        return container;
    }
}
