package org.mule.tooling.lang.dw.structure;


import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveKeyValuePair;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WeavePropertyView extends PsiTreeElementBase<WeaveKeyValuePair> {
    protected WeavePropertyView(WeaveKeyValuePair psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        final List<StructureViewTreeElement> result = new ArrayList<>();
        WeaveKeyValuePair element = getElement();
        if (element != null) {
            final WeaveExpression expression = element.getExpression();
            final StructureViewTreeElement treeElement = WeaveStructureElementFactory.create(expression);
            if (treeElement != null) {
                result.add(treeElement);
            }
        }
        return result;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return getElement().getPresentation().getPresentableText();
    }

    @Override
    public Icon getIcon(boolean open) {
        return getElement().getPresentation().getIcon(open);
    }
}
