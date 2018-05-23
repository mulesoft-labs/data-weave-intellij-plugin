package org.mule.tooling.lang.dw.surround;

import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;

public class WeaveSurroundDescriptor implements SurroundDescriptor {

    private Surrounder[] surrounders = new Surrounder[]{
            new LogSurrounder(),
            new DoBlockSurrounder(),
    };

    @NotNull
    @Override
    public Surrounder[] getSurrounders() {
        return surrounders;
    }

    @Override
    public boolean isExclusive() {
        return false;
    }

    @Override
    @NotNull
    public PsiElement[] getElementsToSurround(PsiFile file, int startOffset, int endOffset) {
        final PsiElement statements = WeavePsiUtils.findInnerElementRange(file, startOffset, endOffset);
        if (statements == null) return PsiElement.EMPTY_ARRAY;
        return new PsiElement[]{statements};
    }
}
