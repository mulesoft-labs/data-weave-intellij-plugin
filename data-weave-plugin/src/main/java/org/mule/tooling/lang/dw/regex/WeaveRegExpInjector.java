package org.mule.tooling.lang.dw.regex;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.intellij.lang.regexp.RegExpLanguage;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveRegexLiteral;

public class WeaveRegExpInjector implements LanguageInjector {


    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host, @NotNull InjectedLanguagePlaces injectionPlacesRegistrar) {
        if (!(host instanceof WeaveRegexLiteral))
            return;
        injectionPlacesRegistrar.addPlace(RegExpLanguage.INSTANCE, TextRange.from(1, host.getTextLength() - 2), null, null);
    }
}
