package org.mule.tooling.lang.dw.refactor;

import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveAnyDateLiteral;
import org.mule.tooling.lang.dw.parser.psi.WeaveArrayExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveBooleanLiteral;
import org.mule.tooling.lang.dw.parser.psi.WeaveDoExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveNumberLiteral;
import org.mule.tooling.lang.dw.parser.psi.WeaveObjectExpression;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveRegexLiteral;
import org.mule.tooling.lang.dw.parser.psi.WeaveStringLiteral;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NameProviderHelper {
    @NotNull
    public static List<String> possibleNamesForGlobalVariable(PsiElement valueToReplace) {
        final WeaveDocument document = WeavePsiUtils.getDocument(valueToReplace);
        final List<String> allGlobalNames = WeavePsiUtils.getAllGlobalNames(document);
        return possibleNamesFor(valueToReplace, allGlobalNames);
    }


    @NotNull
    public static List<String> possibleNamesForLocalVariable(PsiElement valueToReplace, @Nullable WeaveDoExpression doBlock) {
        final List<String> allGlobalNames = WeavePsiUtils.getAllLocalNames(doBlock);
        return possibleNamesFor(valueToReplace, allGlobalNames);
    }

    public static List<String> possibleNamesFor(PsiElement valueToReplace, List<String> alreadyUsedNames) {
        final ArrayList<String> result = new ArrayList<>();
        if (valueToReplace instanceof WeaveStringLiteral) {
            String stringText = ((WeaveStringLiteral) valueToReplace).getValue();
            String[] suggestionsByValue = getSuggestionsByValue(stringText);
            if (suggestionsByValue.length > 0) {
                result.addAll(Arrays.asList(suggestionsByValue));
            } else {
                result.add("myString");
            }
        } else if (valueToReplace instanceof WeaveBooleanLiteral) {
            result.add("myBoolean");
        } else if (valueToReplace instanceof WeaveRegexLiteral) {
            result.add("myRegex");
        } else if (valueToReplace instanceof WeaveAnyDateLiteral) {
            result.add("myDate");
        } else if (valueToReplace instanceof WeaveObjectExpression) {
            result.add("myObject");
        } else if (valueToReplace instanceof WeaveArrayExpression) {
            result.add("myArray");
        } else if (valueToReplace instanceof WeaveNumberLiteral) {
            result.add("myNumber");
        } else {
            result.add("myVar");
        }
        //We should get a non repated name
        return result.stream().map((name) -> makeNameUnike(valueToReplace, name, alreadyUsedNames)).collect(Collectors.toList());
    }

    @NotNull
    private static String[] getSuggestionsByValue(@NotNull String stringValue) {
        List<String> result = new ArrayList<>();
        StringBuffer currentWord = new StringBuffer();

        boolean prevIsUpperCase = false;

        for (int i = 0; i < stringValue.length(); i++) {
            final char c = stringValue.charAt(i);
            if (Character.isUpperCase(c)) {
                if (currentWord.length() > 0 && !prevIsUpperCase) {
                    result.add(currentWord.toString());
                    currentWord = new StringBuffer();
                }
                currentWord.append(c);
            } else if (Character.isLowerCase(c)) {
                currentWord.append(c);
            } else if (Character.isJavaIdentifierPart(c) && c != '_') {
                if (Character.isJavaIdentifierStart(c) || currentWord.length() > 0 || !result.isEmpty()) {
                    currentWord.append(c);
                }
            } else {
                if (currentWord.length() > 0) {
                    result.add(currentWord.toString());
                    currentWord = new StringBuffer();
                }
            }

            prevIsUpperCase = Character.isUpperCase(c);
        }

        if (currentWord.length() > 0) {
            result.add(currentWord.toString());
        }
        return ArrayUtil.toStringArray(result);
    }


    public static String makeNameUnike(PsiElement valueToReplace, String baseName, List<String> allGlobalNames) {

        String name = baseName;
        int i = 0;
        while (allGlobalNames.contains(name)) {
            name = baseName + i;
            i = i + 1;
        }
        return name;
    }
}
