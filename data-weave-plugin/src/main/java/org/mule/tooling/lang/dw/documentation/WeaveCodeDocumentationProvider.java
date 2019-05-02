package org.mule.tooling.lang.dw.documentation;

import com.intellij.lang.documentation.CodeDocumentationProvider;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDirective;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;

import java.util.List;
import java.util.Optional;

public class WeaveCodeDocumentationProvider implements CodeDocumentationProvider {
    @Nullable
    @Override
    public PsiComment findExistingDocComment(PsiComment contextElement) {
        PsiElement nextSibling = contextElement.getNextSibling();
        while (nextSibling instanceof PsiWhiteSpace) {
            nextSibling = nextSibling.getNextSibling();
        }
        return nextSibling != null ? contextElement : null;
    }

    @Nullable
    @Override
    public Pair<PsiElement, PsiComment> parseContext(@NotNull PsiElement startPoint) {
        return null;
    }

    @Nullable
    @Override
    public String generateDocumentationContentStub(PsiComment contextComment) {

        PsiElement nextSibling = contextComment.getNextSibling();
        while (nextSibling instanceof PsiWhiteSpace) {
            nextSibling = nextSibling.getNextSibling();
        }
        if (nextSibling instanceof WeaveFunctionDirective) {
            Optional<String> documentation = WeaveEditorToolingAPI.getInstance(nextSibling.getProject()).scaffoldWeaveDocOf(nextSibling);
            if (documentation.isPresent()) {
                String documentationText = documentation.get().trim();
                return documentationText.substring("/**".length(), documentationText.length() - 3).trim();
            }
        }

        return "";
    }

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return WeaveEditorToolingAPI.getInstance(element.getProject()).hover(element);
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
            documentation = WeaveEditorToolingAPI.toHtml(((WeaveDocumentationPsiElement) element).getDocs());
        } else {
            documentation = WeaveEditorToolingAPI.getInstance(element.getProject()).documentation(element);
            if (originalElement != null && documentation == null) {
                documentation = WeaveEditorToolingAPI.getInstance(originalElement.getProject()).documentation(originalElement);
            }
        }
        return documentation;
    }

    @Nullable
    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object lookupElement, PsiElement element) {
        if (lookupElement instanceof WeaveEditorToolingAPI.CompletionData) {
            WeaveEditorToolingAPI.CompletionData completionData = (WeaveEditorToolingAPI.CompletionData) lookupElement;
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
