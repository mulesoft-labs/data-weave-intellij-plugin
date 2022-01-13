package org.mule.tooling.restsdk.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

import java.util.Arrays;
import java.util.List;

public class RestSdkAnnotator implements Annotator {
  private List<String> URL_KEY_NAMES = Arrays.asList("url", "typeSchema", "outputType");

  @Override
  public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
    if (RestSdkHelper.isRestSdkDescriptorFile(element.getContainingFile())) {
      if (element instanceof YAMLScalar) {
        PsiElement parent = element.getParent();
        if (parent instanceof YAMLKeyValue && URL_KEY_NAMES.contains(((YAMLKeyValue) parent).getKeyText())) {
          VirtualFile virtualFile = element.getContainingFile().getVirtualFile();
          if (virtualFile != null) {
            String path = ((YAMLScalar) element).getTextValue();
            VirtualFile virtualFileParent = virtualFile.getParent();
            if (virtualFileParent != null) {
              final VirtualFile child = virtualFileParent.findFileByRelativePath(path);
              if (child == null) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Unable to resolve path to : " + path).range(element).create();
              }
            }
          }
        }
      }
    }
  }
}
