package org.mule.tooling.lang.dw.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFile;
import org.mule.tooling.lang.dw.parser.psi.WeaveBody;
import org.mule.tooling.lang.dw.parser.psi.WeaveDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class WeaveDocumentStructureView extends PsiTreeElementBase<WeaveFile> {


    protected WeaveDocumentStructureView(@NotNull WeaveFile psiElement) {
        super(psiElement);
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        final WeaveFile weaveFile = getElement();
        if (weaveFile == null) {
            return Collections.emptyList();
        } else {
            final WeaveDocument element = weaveFile.getDocument();
            final List<StructureViewTreeElement> result = new ArrayList<>();
            if (element != null) {
                final WeaveHeader header = element.getHeader();
                if (header != null) {
                    final List<WeaveDirective> weaveDirectives = header.getDirectiveList();
                    for (WeaveDirective weaveDirective : weaveDirectives) {
                        StructureViewTreeElement structureViewTreeElement = WeaveStructureElementFactory.create(weaveDirective);
                        if (structureViewTreeElement != null) {
                            result.add(structureViewTreeElement);
                        }
                    }
                }
                final WeaveBody body = element.getBody();
                if (body != null) {
                    final StructureViewTreeElement treeElement = WeaveStructureElementFactory.create(body.getExpression());
                    if (treeElement != null) {
                        result.add(treeElement);
                    }
                }
            }
            return result;
        }
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return "";
    }
}
