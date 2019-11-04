package org.mule.tooling.lang.dw.structure;

import com.intellij.icons.AllIcons;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotationDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnnotationDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypeDirective;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class WeaveAnnotationView extends PsiTreeElementBase<WeaveAnnotationDirective> {

    public WeaveAnnotationView(WeaveAnnotationDirective element) {
        super(element);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return Collections.emptyList();
    }

    @Nullable
    @Override
    public String getPresentableText() {
        if (getElement() != null) {
            final WeaveAnnotationDefinition annotationDefinition = getElement().getAnnotationDefinition();
            if (annotationDefinition != null) {
                return annotationDefinition.getName();
            }
        }
        return null;
    }

    @Override
    public Icon getIcon(boolean open) {
        return AllIcons.Nodes.Annotationtype;
    }
}
