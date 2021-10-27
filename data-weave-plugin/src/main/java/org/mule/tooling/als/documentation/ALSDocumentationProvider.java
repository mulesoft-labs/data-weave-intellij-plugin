package org.mule.tooling.als.documentation;

import com.intellij.lang.documentation.CodeDocumentationProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.als.component.ALSLanguageService;

public class ALSDocumentationProvider implements CodeDocumentationProvider {
  @Nullable
  @Override
  public PsiComment findExistingDocComment(PsiComment contextElement) {
    return null;
  }

  @Nullable
  @Override
  public Pair<PsiElement, PsiComment> parseContext(@NotNull PsiElement startPoint) {
    return null;
  }

  @Nullable
  @Override
  public String generateDocumentationContentStub(PsiComment contextComment) {
    return null;
  }

  @Nullable
  @Override
  public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
    final ALSLanguageService instance = ALSLanguageService.getInstance(element.getProject());
    if (instance.isSupportedFile(element.getContainingFile())) {
      return instance.hover(element);
    } else {
      return null;
    }
  }


  @Nullable
  @Override
  public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
    final ALSLanguageService instance = ALSLanguageService.getInstance(element.getProject());
    if (instance.isSupportedFile(element.getContainingFile())) {
      return instance.hover(element);
    } else {
      return null;
    }
  }


}
