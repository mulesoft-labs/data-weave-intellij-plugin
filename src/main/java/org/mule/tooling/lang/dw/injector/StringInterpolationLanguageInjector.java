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

  static Pattern STRING_INTERPOLATION = Pattern.compile("\\$\\(([^\\)]*)\\)+");

  @Override
  public void getLanguagesToInject(@NotNull PsiLanguageInjectionHost host,
                                   @NotNull InjectedLanguagePlaces injectedLanguagePlaces) {

    if (host.getContainingFile().getFileType().equals(WeaveFileType.getInstance())) {
      if (host instanceof WeaveStringLiteralMixin) {
        String text = host.getText();
        Matcher expressionMatcher = STRING_INTERPOLATION.matcher(text);
        while (expressionMatcher.find()) {
          int start = expressionMatcher.start(1);
          int end = expressionMatcher.end(1);
          final TextRange expressionTextRange = TextRange.from(start, end - start);
          injectedLanguagePlaces.addPlace(WeaveLanguage.getInstance(), expressionTextRange, null, null);
        }
      }
    }
  }
}
