package org.mule.tooling.runtime.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.util.xml.DomUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.idea.maven.dom.MavenDomUtil;
import org.jetbrains.idea.maven.dom.model.MavenDomDependency;
import org.jetbrains.idea.maven.dom.model.MavenDomProjectModel;
import org.jetbrains.idea.maven.model.MavenId;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;
import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.exchange.ExchangeArtifact;
import org.mule.tooling.runtime.exchange.ui.ExchangeDependencyDialog;

public class AddDependencyFromExchangeAction extends AnAction {

    final static Logger logger = Logger.getInstance(AddDependencyFromExchangeAction.class);

    final static String MULE_PLUGIN_CLASSIFIER = "mule-plugin";

    public AddDependencyFromExchangeAction() {
        super("Add Dependency from Exchange", "Adds connector or extension dependency from Anypoint Exchange", RuntimeIcons.AnypointExchangeIcon);
    }

    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {

        final ExchangeDependencyDialog form = new ExchangeDependencyDialog();
        form.show();
        boolean isOk = form.getExitCode() == DialogWrapper.OK_EXIT_CODE;

        if (!isOk)
            return;

        final ExchangeArtifact[] dependencies = form.getSelectedArtifacts();

        final Project project = anActionEvent.getProject();

        MavenProject mavenProject = MavenActionUtil.getMavenProject(anActionEvent.getDataContext());
        //logger.info("Maven Project is " + mavenProject);

        final MavenDomProjectModel model = MavenDomUtil.getMavenDomProjectModel(project, mavenProject.getFile());
        if (model == null) return;

        new WriteCommandAction.Simple(project, "Add Maven Dependency", (PsiFile) DomUtil.getFile(model)) {
            @Override
            protected void run() throws Throwable {
                for (ExchangeArtifact artifact : dependencies) {
                    //logger.info("Exchange Artifact is " + artifact);

                    MavenId mavenId = new MavenId(artifact.getGroupId(), artifact.getArtifactId(), artifact.getVersion());

                    MavenDomDependency dependency = MavenDomUtil.createDomDependency(model, null, mavenId);
                    dependency.getClassifier().setStringValue(StringUtils.isEmpty(artifact.getClassifier()) ? MULE_PLUGIN_CLASSIFIER : artifact.getClassifier());
                }
            }
        }.execute();
    }

    @Override
    public void update(AnActionEvent anActionEvent) {
        final VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(anActionEvent.getDataContext());

        anActionEvent.getPresentation().setEnabled(file != null && "pom.xml".equals(file.getName()));
        anActionEvent.getPresentation().setVisible(file != null && "pom.xml".equals(file.getName()));
    }

}
