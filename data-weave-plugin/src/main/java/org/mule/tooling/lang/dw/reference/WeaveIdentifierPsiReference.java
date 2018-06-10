package org.mule.tooling.lang.dw.reference;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;

import java.util.Objects;
import java.util.stream.Stream;

public class WeaveIdentifierPsiReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private WeaveNamedElement namedElement;

    public WeaveIdentifierPsiReference(@NotNull WeaveNamedElement element) {
        super(element, TextRange.from(element.getNameIdentifier().getStartOffsetInParent(), element.getNameIdentifier().getTextLength()));
        namedElement = element;
    }

    public PsiElement[] resolveInner() {
        final WeaveEditorToolingAPI instance = WeaveEditorToolingAPI.getInstance(myElement.getProject());
        return instance.resolveReference(namedElement.getIdentifier());
    }


    @NotNull
    @Override
    public Object[] getVariants() {
        return new Object[0];
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        ResolveCache resolveCache = ResolveCache.getInstance(getElement().getProject());
        return resolveCache.resolveWithCaching(this, MyResolver.INSTANCE, true, incompleteCode, namedElement.getContainingFile());
    }

    private static class MyResolver implements ResolveCache.PolyVariantContextResolver<WeaveIdentifierPsiReference> {
        private static final MyResolver INSTANCE = new MyResolver();

        @NotNull
        @Override
        public ResolveResult[] resolve(@NotNull WeaveIdentifierPsiReference ref, @NotNull PsiFile containingFile, boolean incompleteCode) {
            return Stream.of(ref.resolveInner()).filter(Objects::nonNull).map(PsiElementResolveResult::new).toArray(ResolveResult[]::new);
        }

    }
}
