package org.mule.tooling.runtime.exchange;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ExchangeArtifact implements Comparable<ExchangeArtifact> {
    private String groupId;
    private String artifactId;
    private String classifier;
    private String version;
    private String runtimeVersion;
    private String name;
    private String description;

    public ExchangeArtifact(String groupId, String artifactId, String classifier, String version, String runtimeVersion, String name, String description) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.classifier = classifier;
        this.version = version;
        this.runtimeVersion = runtimeVersion;
        this.name = name;
        this.description = description;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getClassifier() {
        return classifier;
    }

    public void setClassifier(String classifier) {
        this.classifier = classifier;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getRuntimeVersion() {
        return runtimeVersion;
    }

    public void setRuntimeVersion(String runtimeVersion) {
        this.runtimeVersion = runtimeVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExchangeArtifact)) return false;
        ExchangeArtifact that = (ExchangeArtifact) o;
        return Objects.equals(getGroupId(), that.getGroupId()) &&
                Objects.equals(getArtifactId(), that.getArtifactId()) &&
                Objects.equals(getClassifier(), that.getClassifier()) &&
                Objects.equals(getVersion(), that.getVersion()) &&
                Objects.equals(getRuntimeVersion(), that.getRuntimeVersion()) &&
                Objects.equals(getName(), that.getName()) &&
                Objects.equals(getDescription(), that.getDescription());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getGroupId(), getArtifactId(), getClassifier(), getVersion(), getRuntimeVersion(), getName(), getDescription());
    }

    @Override
    public String toString() {
        return "ExchangeArtifact{" +
                "groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", classifier='" + classifier + '\'' +
                ", version='" + version + '\'' +
                ", runtimeVersion='" + runtimeVersion + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull ExchangeArtifact o) {
        return (getName() + getVersion()).compareTo(o.getName() + o.getVersion());
    }
}
