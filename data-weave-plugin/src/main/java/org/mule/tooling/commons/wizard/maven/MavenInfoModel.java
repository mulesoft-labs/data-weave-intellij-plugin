package org.mule.tooling.commons.wizard.maven;

import org.jetbrains.idea.maven.model.MavenId;

public class MavenInfoModel {
    private MavenId mavenId;

    public MavenInfoModel(MavenId mavenId) {
        this.mavenId = mavenId;
    }

    public MavenInfoModel() {
    }

    public MavenId getMavenId() {
        return mavenId;
    }

    public void setMavenId(MavenId mavenId) {
        this.mavenId = mavenId;
    }
}
