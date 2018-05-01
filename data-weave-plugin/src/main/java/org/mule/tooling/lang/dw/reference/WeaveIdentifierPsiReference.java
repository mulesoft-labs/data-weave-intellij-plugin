package org.mule.tooling.lang.dw.reference;


import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.impl.source.resolve.ResolveCache;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariable;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import java.util.ArrayList;
import java.util.List;

public class WeaveIdentifierPsiReference extends PsiReferenceBase<PsiElement> {
    private WeaveNamedElement namedElement;

    public WeaveIdentifierPsiReference(@NotNull WeaveNamedElement element) {
        super(element, TextRange.from(element.getNameIdentifier().getStartOffsetInParent(), element.getNameIdentifier().getTextLength()));
        namedElement = element;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        return ResolveCache.getInstance(getElement().getProject()).resolveWithCaching(this, MyResolver.INSTANCE, false, false);
    }

    public PsiElement resolveInner() {
        final DWEditorToolingAPI instance = DWEditorToolingAPI.getInstance(myElement.getProject());
        return instance.resolveReference(namedElement.getIdentifier());
    }


    @NotNull
    @Override
    public Object[] getVariants() {
        final List<WeaveVariable> variables = WeavePsiUtils.collectLocalVisibleVariables(myElement);
        final List<LookupElement> variants = new ArrayList<>();
        for (final WeaveVariable property : variables) {
            if (property.getName() != null && property.getName().length() > 0) {
                variants.add(LookupElementBuilder.create(property).
                        withIcon(AllIcons.Nodes.Variable).
                        withTypeText(property.getContainingFile().getName())
                );
            }
        }
        return variants.toArray();
    }

    private static class MyResolver implements ResolveCache.Resolver {
        private static final MyResolver INSTANCE = new MyResolver();

        @Nullable
        public PsiElement resolve(@NotNull PsiReference ref, boolean incompleteCode) {
            return ((WeaveIdentifierPsiReference) ref).resolveInner();
        }

    }
}
