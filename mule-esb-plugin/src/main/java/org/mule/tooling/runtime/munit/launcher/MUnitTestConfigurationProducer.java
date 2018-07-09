package org.mule.tooling.runtime.munit.launcher;

import org.apache.commons.lang.StringUtils;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;

public class MUnitTestConfigurationProducer extends JavaRunConfigurationProducerBase<MUnitTestConfiguration> {

  protected MUnitTestConfigurationProducer() {
    super(MUnitTestConfigurationType.getInstance());
  }

  @Override
  protected boolean setupConfigurationFromContext(MUnitTestConfiguration weaveConfiguration,
                                                  ConfigurationContext configurationContext, Ref<PsiElement> ref) {
    final Location location = configurationContext.getLocation();
    if (isTestFile(configurationContext)) {
      final PsiFile containingFile = location.getPsiElement().getContainingFile();
      if (containingFile != null) {
        weaveConfiguration.setModule(configurationContext.getModule());
        weaveConfiguration.setName("MUnit Test " + StringUtils.capitalize(containingFile.getVirtualFile().getName()));
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean isConfigurationFromContext(MUnitTestConfiguration muleConfiguration, ConfigurationContext configurationContext) {
    final PsiElement psiLocation = configurationContext.getPsiLocation();
    return isTestFile(configurationContext) && psiLocation != null;
  }

  private boolean isTestFile(ConfigurationContext configurationContext) {
    final Location location = configurationContext.getLocation();
    if (location != null) {
      final PsiFile containingFile = location.getPsiElement().getContainingFile();
      if (containingFile != null) {
        return containingFile.getName().endsWith("-test.xml");
      }
    }
    return false;
  }
}
