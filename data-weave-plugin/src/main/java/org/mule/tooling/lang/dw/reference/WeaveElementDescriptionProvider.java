package org.mule.tooling.lang.dw.reference;

import com.intellij.codeInsight.highlighting.HighlightUsagesDescriptionLocation;
import com.intellij.psi.ElementDescriptionLocation;
import com.intellij.psi.ElementDescriptionProvider;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageViewLongNameLocation;
import com.intellij.usageView.UsageViewShortNameLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;

public class WeaveElementDescriptionProvider implements ElementDescriptionProvider {
    @Nullable
    @Override
    public String getElementDescription(@NotNull PsiElement o, @NotNull ElementDescriptionLocation location) {
        if (location == UsageViewShortNameLocation.INSTANCE ||
                location == UsageViewLongNameLocation.INSTANCE ||
                location == HighlightUsagesDescriptionLocation.INSTANCE) {
            return o instanceof WeaveNamedElement ? ((WeaveNamedElement) o).getName() : null;
        } else {
            return null;
        }
    }
}
