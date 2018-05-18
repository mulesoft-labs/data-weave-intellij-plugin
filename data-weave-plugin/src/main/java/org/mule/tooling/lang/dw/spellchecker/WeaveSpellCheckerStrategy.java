package org.mule.tooling.lang.dw.spellchecker;

import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveLanguage;

public class WeaveSpellCheckerStrategy extends SpellcheckingStrategy {

    @Override
    public boolean isMyContext(@NotNull PsiElement element) {
        return element.getLanguage() == WeaveLanguage.getInstance();
    }


}
