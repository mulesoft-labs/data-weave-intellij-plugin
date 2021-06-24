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
import org.mule.tooling.lang.dw.WeaveLanguage;

import java.util.Collections;
import java.util.List;

public class YamlLanguageInjector implements MultiHostInjector {

    private static final String EXPRESSION_LANGUAGE_PREFIX = "#[";
    private static final String EXPRESSION_LANGUAGE_SUFFIX = "]";

    @Override
    public void getLanguagesToInject(@NotNull MultiHostRegistrar registrar, @NotNull PsiElement context) {


        if (!(context instanceof YAMLQuotedText)) {
            return;
        }

        PsiFile file = context.getContainingFile();
        YAMLQuotedText element = (YAMLQuotedText) context;
        final Language requiredLanguage = WeaveLanguage.getInstance();
        String value = element.getTextValue();
        if (value.startsWith(EXPRESSION_LANGUAGE_PREFIX) && value.endsWith(EXPRESSION_LANGUAGE_SUFFIX)) {
            TextRange valueTextRange = ElementManipulators.getValueTextRange(element);
//            new TextRange(EXPRESSION_LANGUAGE_PREFIX.length() + 1, value.length() + 1);
            TextRange expressionLength = TextRange.from(valueTextRange.getStartOffset() + EXPRESSION_LANGUAGE_PREFIX.length(), valueTextRange.getLength() - (EXPRESSION_LANGUAGE_PREFIX.length() + EXPRESSION_LANGUAGE_SUFFIX.length()));
//            TextRange expressionLength = new TextRange(EXPRESSION_LANGUAGE_PREFIX.length() + 1, value.length() + 1);
            registrar
                    .startInjecting(requiredLanguage)
                    .addPlace(null, null, element, expressionLength)
                    .doneInjecting();
        }

    }

    @NotNull
    @Override
    public List<? extends Class<? extends PsiElement>> elementsToInjectIn() {
        return Collections.singletonList(YAMLQuotedText.class);
    }

}