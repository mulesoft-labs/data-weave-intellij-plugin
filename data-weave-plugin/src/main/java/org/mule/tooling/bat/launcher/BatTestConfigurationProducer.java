package org.mule.tooling.bat.launcher;

import com.intellij.execution.Location;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.execution.junit.JavaRunConfigurationProducerBase;
import com.intellij.openapi.util.Ref;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.apache.commons.lang.StringUtils;
import org.mule.tooling.bat.utils.BatUtils;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiImplUtils;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

public class BatTestConfigurationProducer extends JavaRunConfigurationProducerBase<BatTestConfiguration> {
  protected BatTestConfigurationProducer() {
    super(BatTestConfigurationType.getInstance());
  }

  @Override
  protected boolean setupConfigurationFromContext(BatTestConfiguration weaveConfiguration, ConfigurationContext configurationContext, Ref<PsiElement> ref) {
    final Location location = configurationContext.getLocation();
    if (isTestFile(configurationContext)) {
      final PsiFile containingFile = location.getPsiElement().getContainingFile();
      if (containingFile != null) {
        final boolean weaveFile = containingFile.getFileType() == WeaveFileType.getInstance();
        if (weaveFile) {
          NameIdentifier nameIdentifier = WeavePsiImplUtils.getNameIdentifier(containingFile);
          weaveConfiguration.setNameIdentifier(nameIdentifier.name());
          weaveConfiguration.setModule(configurationContext.getModule());
          weaveConfiguration.setName("Bat Test " + StringUtils.capitalize(containingFile.getVirtualFile().getName()));
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public boolean isConfigurationFromContext(BatTestConfiguration muleConfiguration, ConfigurationContext configurationContext) {
    final PsiElement psiLocation = configurationContext.getPsiLocation();
    if (isTestFile(configurationContext) && psiLocation != null) {
      final PsiFile containingFile = psiLocation.getContainingFile();
      final NameIdentifier nameIdentifier = WeavePsiImplUtils.getNameIdentifier(containingFile);
      return nameIdentifier.name().equals(muleConfiguration.getNameIdentifier());
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
          return BatUtils.isTestFile(document);
        }
      }
    }
    return false;
  }
}
