package org.mule.tooling.lang.dw.launcher.configuration;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.Scenario;
import org.mule.tooling.lang.dw.service.WeaveRuntimeContextManager;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.tooling.lang.dw.util.WeaveUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

public class WeaveConfigurationProducer extends JavaRunConfigurationProducerBase<WeaveConfiguration> {
    protected WeaveConfigurationProducer() {
        super(WeaveConfigurationType.getInstance().getConfigurationFactories()[0]);
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull WeaveConfiguration weaveConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        final Location location = configurationContext.getLocation();
        if (location != null) {
            final PsiFile containingFile = location.getPsiElement().getContainingFile();
            if (containingFile != null) {
                final WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(containingFile);
                if (weaveDocument != null && !WeaveUtils.isTestFile(weaveDocument)) {
                    final String nameIdentifier = weaveDocument.getQualifiedName();
                    weaveConfiguration.setNameIdentifier(nameIdentifier);
                    final Module module = configurationContext.getModule();
                    if (module != null) {
                        weaveConfiguration.setModule(module);
                        final WeaveRuntimeContextManager instance = WeaveRuntimeContextManager.getInstance(module.getProject());
                        final Scenario currentScenarioFor = instance.getCurrentScenarioFor(weaveDocument);
                        if (currentScenarioFor != null) {
                            weaveConfiguration.setScenario(currentScenarioFor.getName());
                        }
                        weaveConfiguration.setName("Run Mapping " + StringUtils.capitalize(containingFile.getVirtualFile().getNameWithoutExtension()));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(WeaveConfiguration muleConfiguration, @NotNull ConfigurationContext configurationContext) {
        final String configurationNameIdentifier = muleConfiguration.getNameIdentifier();
        if (configurationNameIdentifier == null) {
            return false;
        }
        final Module module = configurationContext.getModule();
        if (module == null) {
            return false;
        }
        final PsiElement psiLocation = configurationContext.getPsiLocation();
        if (psiLocation == null) {
            return false;
        }
        final PsiFile containingFile = psiLocation.getContainingFile();
        if (containingFile == null) {
            return false;
        }
        final NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(containingFile.getProject(), containingFile.getVirtualFile());
        final String currentNameIdentifier = nameIdentifier.name();
        final WeaveDocument document = WeavePsiUtils.getDocument(containingFile);
        return module.equals(muleConfiguration.getModule()) && configurationNameIdentifier.equals(currentNameIdentifier) && !WeaveUtils.isTestFile(document);
    }
}
