package org.mule.tooling.lang.dw.injector;


import com.intellij.openapi.util.TextRange;
import com.intellij.psi.InjectedLanguagePlaces;
import com.intellij.psi.LanguageInjector;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.WeaveLanguage;
import org.mule.tooling.lang.dw.parser.psi.WeaveStringLiteralMixin;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringInterpolationLanguageInjector implements LanguageInjector {

    static Pattern STRING_INTERPOLATION = Pattern.compile("\\$\\(");

    @Override
    public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host,
                                     @NotNull InjectedLanguagePlaces injectedLanguagePlaces) {

        if (host.getContainingFile().getFileType().equals(WeaveFileType.getInstance())) {
            if (host instanceof WeaveStringLiteralMixin) {
                String text = host.getText();
                Matcher expressionMatcher = STRING_INTERPOLATION.matcher(text);
                int end = 0;
                while (expressionMatcher.find(end)) {
                    int start = expressionMatcher.end();
                    end = calculateEndIndex(start, text);
                    final TextRange expressionTextRange = TextRange.from(start, end - start);
                    injectedLanguagePlaces.addPlace(WeaveLanguage.getInstance(), expressionTextRange, null, null);
                }
            }
        }
    }

    private int calculateEndIndex(int start, String text) {
        int length = text.length();
        boolean ended = false;
        int i = start;
        boolean insideText = false;
        int openParenthesis = 0;
        while (!ended && i < length) {
            switch (text.charAt(i)) {
                case '\\':
                    i = i + 1;
                    break;
                case '\"':
                case '`':
                case '\'':
                    insideText = !insideText;
                    break;
                case '(':
                    if (!insideText)
                        openParenthesis = openParenthesis + 1;
                    break;
                case ')':
                    if (!insideText) {
                        if (openParenthesis == 0) {
                            ended = true;
                        } else {
                            openParenthesis = openParenthesis - 1;
                        }
                    }
                    break;

            }
            i++;
        }
        return i - 1;
    }
}
