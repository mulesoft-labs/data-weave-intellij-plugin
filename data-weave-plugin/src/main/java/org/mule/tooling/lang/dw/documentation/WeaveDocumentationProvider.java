package org.mule.tooling.lang.dw.documentation;

import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import java.util.List;

public class WeaveDocumentationProvider implements DocumentationProvider {
    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return DWEditorToolingAPI.getInstance(element.getProject()).hover(element);
    }

    @Nullable
    @Override
    public List<String> getUrlFor(PsiElement psiElement, PsiElement psiElement1) {
        return null;
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        String documentation;
        if (element instanceof WeaveDocumentationPsiElement) {
            documentation = DWEditorToolingAPI.toHtml(((WeaveDocumentationPsiElement) element).getDocs());
        } else {
            documentation = DWEditorToolingAPI.getInstance(element.getProject()).documentation(element);
            if (originalElement != null && documentation == null) {
                documentation = DWEditorToolingAPI.getInstance(originalElement.getProject()).documentation(originalElement);
            }
        }
        return documentation;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object lookupElement, PsiElement element) {
        if (lookupElement instanceof DWEditorToolingAPI.CompletionData) {
            DWEditorToolingAPI.CompletionData completionData = (DWEditorToolingAPI.CompletionData) lookupElement;
            return new WeaveDocumentationPsiElement(element, completionData.getDocumentation(), completionData.getLabel());
        } else {
            return element;
        }
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        return null;
    }
}
