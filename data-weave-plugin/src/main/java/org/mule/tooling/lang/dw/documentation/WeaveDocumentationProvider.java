package org.mule.tooling.lang.dw.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.service.DataWeaveServiceManager;

import java.util.List;

public class WeaveDocumentationProvider implements DocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return DataWeaveServiceManager.getInstance(originalElement.getProject()).hover(originalElement);
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement psiElement, PsiElement psiElement1) {
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (originalElement == null) {
            return DataWeaveServiceManager.getInstance(element.getProject()).documentation(element);
        } else {
            return DataWeaveServiceManager.getInstance(originalElement.getProject()).documentation(originalElement);
        }
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object lookupElement, PsiElement element) {
        if (lookupElement instanceof DataWeaveServiceManager.CompletionData) {
            return null;
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }
}
