package org.mule.tooling.restsdk.utils;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.schema.RestSdkDescriptorFileProvider;

public class RestSdkHelper {

    public static boolean isRestSdkFile(PsiFile psiFile){
        PsiElement context = psiFile.getContext();
        if (context instanceof YAMLScalar) {
            String text = context.getContainingFile().getText();
            return isRestSdkDescriptor(text);
        } else {
            return false;
        }
    }

    public static boolean isRestSdkDescriptor(String text) {
        return text.contains("#% Rest Connector Descriptor 1.0");
    }
}
