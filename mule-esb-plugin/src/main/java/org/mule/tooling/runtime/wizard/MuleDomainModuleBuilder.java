package org.mule.tooling.runtime.wizard;

import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;

public class MuleDomainModuleBuilder extends AbstractMuleModuleBuilder {

    public MuleDomainModuleBuilder() {
        setProjectId(new MavenId("com.mycompany.myproject", "my-domain", "1.0.0-SNAPSHOT"));
    }

    protected void invokeInitializer(Project project, VirtualFile root, MavenId parentId) {

        MavenUtil.invokeLater(project, (DumbAwareRunnable) () -> {
            if (this.getEnvironmentForm() != null) {
                this.getEnvironmentForm().setData(MavenProjectsManager.getInstance(project).getGeneralSettings());
            }
            (new MuleDomainModuleInitializer(getProjectId(), getAggregatorProject(), getParentProject(), isInheritGroupId(),
                    isInheritVersion(), getArchetype(), getPropertiesToCreateByArtifact(),
                    "Create new Maven module")).configure(project, getProjectId(), getMuleVersion(),
                    getMuleMavenPluginVersion(), getMtfVersion(), root,
                    parentId);

        });
    }

    @Override
    public String getName() {
        return "Mule Domain";
    }

    @Override
    public String getBuilderId() {
        return getClass().getName();
    }

    @Override
    public String getPresentableName() {
        return "Mule Domain";
    }

    @Override
    public String getDescription() {
        return "Creates a Mule Runtime Domain Maven Based Project. Maven modules are used for developing <b>JVM-based</b> applications with dependencies managed by <b>Maven</b>. ";
    }
}
