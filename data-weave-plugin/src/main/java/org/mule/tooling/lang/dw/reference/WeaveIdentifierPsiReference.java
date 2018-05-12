package org.mule.tooling.lang.dw.reference;


import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.compiled.ClsJavaCodeReferenceElementImpl;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariable;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class WeaveIdentifierPsiReference extends PsiPolyVariantReferenceBase<PsiElement> {
    private WeaveNamedElement namedElement;

    public WeaveIdentifierPsiReference(@NotNull WeaveNamedElement element) {
        super(element, TextRange.from(element.getNameIdentifier().getStartOffsetInParent(), element.getNameIdentifier().getTextLength()));
        namedElement = element;
    }

    public PsiElement[] resolveInner() {
        final DWEditorToolingAPI instance = DWEditorToolingAPI.getInstance(myElement.getProject());
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
