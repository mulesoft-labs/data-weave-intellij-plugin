package org.mule.tooling.lang.dw.launcher.configuration;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.mule.tooling.lang.dw.WeaveFile;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.launcher.configuration.ui.WeaveInput;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.util.WeaveUtils;

import java.util.ArrayList;
import java.util.List;

public class WeaveTestConfigurationProducer extends JavaRunConfigurationProducerBase<WeaveTestConfiguration> {
    protected WeaveTestConfigurationProducer() {
        super(WeaveTestConfigurationType.getInstance());
    }

    @Override
    protected boolean setupConfigurationFromContext(WeaveTestConfiguration weaveConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
        final Location location = configurationContext.getLocation();
        if (isTestFile(configurationContext)) {
            final PsiFile containingFile = location.getPsiElement().getContainingFile();
            if (containingFile != null) {
                final boolean weaveFile = containingFile.getFileType() == WeaveFileType.getInstance();
                if (weaveFile) {
                    weaveConfiguration.setWeaveFile(WeavePsiUtils.getFQN(location.getPsiElement()));
                    weaveConfiguration.setModule(configurationContext.getModule());
                    weaveConfiguration.setName("Run WeaveTest " + StringUtils.capitalize(containingFile.getVirtualFile().getName()));
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isConfigurationFromContext(WeaveTestConfiguration muleConfiguration, ConfigurationContext configurationContext) {
        if (isTestFile(configurationContext)) {
            return WeavePsiUtils.getFQN(configurationContext.getPsiLocation()).equals(muleConfiguration.getWeaveFile());
        } else {
            return false;
        }
    }

    private boolean isTestFile(ConfigurationContext configurationContext) {
        final Location location = configurationContext.getLocation();
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
