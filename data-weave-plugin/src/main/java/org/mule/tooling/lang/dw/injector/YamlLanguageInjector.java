package org.mule.tooling.lang.dw.injector;

import com.intellij.lang.Language;
import com.intellij.lang.injection.MultiHostInjector;
import com.intellij.lang.injection.MultiHostRegistrar;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.ElementManipulators;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLQuotedText;
import org.jetbrains.yaml.psi.YAMLScalar;
import org.jetbrains.yaml.psi.YAMLScalarList;
import org.jetbrains.yaml.psi.YAMLScalarText;
import org.mule.tooling.lang.dw.WeaveLanguage;

import java.util.Arrays;
import java.util.List;

public class YamlLanguageInjector implements MultiHostInjector {

  private static final String EXPRESSION_LANGUAGE_PREFIX = "#[";
  private static final String EXPRESSION_LANGUAGE_SUFFIX = "]";

  @Override
  public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {
    if (context instanceof YAMLQuotedText || context instanceof YAMLScalarText || context instanceof YAMLScalarList) {
      PsiFile file = context.getContainingFile();
      YAMLScalar element = (YAMLScalar) context;
      final Language requiredLanguage = WeaveLanguage.getInstance();
      final String value = element.getTextValue();
      if (value.trim().startsWith(EXPRESSION_LANGUAGE_PREFIX) && value.trim().endsWith(EXPRESSION_LANGUAGE_SUFFIX)) {
        TextRange valueTextRange = ElementManipulators.getValueTextRange(element);
        String elementText = element.getText();
        int leftOffset = elementText.indexOf(EXPRESSION_LANGUAGE_PREFIX) + EXPRESSION_LANGUAGE_PREFIX.length();
        int rightOffset = elementText.lastIndexOf(EXPRESSION_LANGUAGE_SUFFIX);

        TextRange expressionRange = TextRange.from(leftOffset, rightOffset - leftOffset);

        registrar
                .startInjecting(requiredLanguage)
                .addPlace(null, null, element, expressionRange)
                .doneInjecting();
      }
    }
  }

  @NotNull
  @Override
  public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
    return Arrays.asList(YAMLQuotedText.class, YAMLScalarText.class, YAMLScalarList.class);
  }

}