package org.mule.tooling.restsdk.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.psi.YAMLScalar;

public class RestSdkHelper {

  public static boolean isInRestSdkContextFile(PsiFile psiFile) {
    PsiElement context = psiFile.getContext();
    if (context instanceof YAMLScalar) {
      PsiFile containingFile = context.getContainingFile();
      return isRestSdkDescriptorFile(containingFile);
    } else {
      return false;
    }
  }

  public static boolean isRestSdkDescriptorFile(PsiFile containingFile) {
    String text = containingFile.getText();
    return isRestSdkDescriptor(text);
  }

  public static boolean isRestSdkDescriptor(String text) {
    return text.contains("#% Rest Connector Descriptor 1.0");
  }
}
