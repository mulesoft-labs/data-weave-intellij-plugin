package org.mule.tooling.lang.dw.launcher.configuration.ui.test;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.configurations.ConfigurationFactory;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiImplUtils;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.util.WeaveUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

import java.util.Optional;

import static org.apache.commons.lang.StringUtils.capitalize;

public class WeaveTestConfigurationProducer extends JavaRunConfigurationProducerBase<WeaveTestConfiguration> {
    protected WeaveTestConfigurationProducer() {
        super();
    }

    @Override
    public @NotNull ConfigurationFactory getConfigurationFactory() {
        return WeaveTestConfigurationType.getInstance().getConfigurationFactories()[0];
    }

    @Override
    protected boolean setupConfigurationFromContext(@NotNull WeaveTestConfiguration weaveConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        final Location<?> location = configurationContext.getLocation();
        if (location != null && isTestFile(configurationContext)) {
            final PsiFile containingFile = location.getPsiElement().getContainingFile();
            if (containingFile != null) {
                final boolean weaveFile = containingFile.getFileType() == WeaveFileType.getInstance();
                if (weaveFile) {
                    final NameIdentifier nameIdentifier = WeavePsiImplUtils.getNameIdentifier(containingFile);
                    weaveConfiguration.setWeaveFile(nameIdentifier.name());
                    weaveConfiguration.setModule(configurationContext.getModule());
                    if (ref.get() != null && WeaveUtils.isTestCase(ref.get())) {
                        Optional<String> testCaseName = WeaveUtils.getTestCaseName(ref.get());
                        if (testCaseName.isPresent()) {
                            testCaseName.ifPresent((s) -> weaveConfiguration.setTestToRun(s));
                            weaveConfiguration.setName("Test Case: " + testCaseName.get() + "@" + capitalize(containingFile.getVirtualFile().getNameWithoutExtension()));
                        } else {
                            weaveConfiguration.setName("Suite: " + capitalize(containingFile.getVirtualFile().getNameWithoutExtension()));
                        }
                    } else {
                        weaveConfiguration.setName("Suite: " + capitalize(containingFile.getVirtualFile().getNameWithoutExtension()));
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(@NotNull WeaveTestConfiguration muleConfiguration, ConfigurationContext configurationContext) {
        final PsiElement psiLocation = configurationContext.getPsiLocation();
        if (isTestFile(configurationContext) && psiLocation != null) {
            final PsiFile containingFile = psiLocation.getContainingFile();
            final NameIdentifier nameIdentifier = WeavePsiImplUtils.getNameIdentifier(containingFile);
            Optional<String> testCaseName = WeaveUtils.getTestCaseName(psiLocation);
            if (testCaseName.isPresent() && !muleConfiguration.getTestToRun().equals(testCaseName.get())) {
                return false;
            }
            return muleConfiguration.getTests().contains(nameIdentifier.name());
        } else {
            return false;
        }
    }

    private boolean isTestFile(ConfigurationContext configurationContext) {
        final Location<?> location = configurationContext.getLocation();
        if (location != null) {
            final PsiFile containingFile = location.getPsiElement().getContainingFile();
            if (containingFile != null) {
                final boolean weaveFile = containingFile.getFileType() == WeaveFileType.getInstance();
                if (weaveFile) {
                    WeaveDocument document = WeavePsiUtils.getDocument(location.getPsiElement());
                    return WeaveUtils.isTestFile(document);
                }
            }
        }
        return false;
    }
}
