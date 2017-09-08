package org.mule.tooling.lang.dw;


import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;

public class WeaveFile extends PsiFileBase {
  public WeaveFile(@NotNull FileViewProvider viewProvider) {
    super(viewProvider, WeaveLanguage.getInstance());
  }

  @NotNull
  @Override
  public FileType getFileType() {
    return WeaveFileType.getInstance();
  }

  public WeaveDocument getDocument() {
    for (PsiElement psiElement : getChildren()) {
      if (psiElement instanceof WeaveDocument) {
        return (WeaveDocument) psiElement;
      }
    }
    return null;
  }

}
