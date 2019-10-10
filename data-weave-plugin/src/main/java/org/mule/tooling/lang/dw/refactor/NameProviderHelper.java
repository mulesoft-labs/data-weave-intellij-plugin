package org.mule.tooling.lang.dw.refactor;

import com.intellij.psi.PsiElement;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class NameProviderHelper {
    @NotNull
    public static List<String> possibleNamesForGlobalVariable(PsiElement valueToReplace) {
        final WeaveDocument document = WeavePsiUtils.getDocument(valueToReplace);
        assert document != null;
        final List<String> allGlobalNames = WeavePsiUtils.getAllGlobalNames(document);
        return possibleNamesFor(valueToReplace, allGlobalNames, "my", "myVar");
    }


    @NotNull
    public static List<String> possibleNamesForLocalVariable(PsiElement valueToReplace, @Nullable WeaveDoExpression doBlock) {
        final List<String> allGlobalNames = WeavePsiUtils.getAllLocalNames(doBlock);
        return possibleNamesFor(valueToReplace, allGlobalNames, "my", "myVar");
    }

    @NotNull
    private static List<String> possibleNamesFor(PsiElement valueToReplace, List<String> alreadyUsedNames, String prefix, String defaultName) {
        final ArrayList<String> result = new ArrayList<>();
        if (valueToReplace instanceof WeaveStringLiteral) {
            String stringText = ((WeaveStringLiteral) valueToReplace).getValue();
            String[] suggestionsByValue = getSuggestionsByValue(stringText);
            if (suggestionsByValue.length > 0) {
                result.addAll(Arrays.asList(suggestionsByValue));
            } else {
                result.add(prefix + "String");
            }
        } else if (valueToReplace instanceof WeaveBooleanLiteral) {
            result.add(prefix + "Boolean");
        } else if (valueToReplace instanceof WeaveRegexLiteral) {
            result.add(prefix + "Regex");
        } else if (valueToReplace instanceof WeaveAnyDateLiteral) {
            result.add(prefix + "Date");
        } else if (valueToReplace instanceof WeaveObjectExpression) {
            result.add(prefix + "Object");
        } else if (valueToReplace instanceof WeaveArrayExpression) {
            result.add(prefix + "Array");
        } else if (valueToReplace instanceof WeaveNumberLiteral) {
            result.add(prefix + "Number");
        } else if (valueToReplace instanceof WeaveType) {
            result.add("MyType");
        } else {
            result.add(defaultName);
        }
        //We should get a non repated name
        return result.stream().map((name) -> makeNameUnique(name, alreadyUsedNames)).collect(Collectors.toList());
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


    private static String makeNameUnique(String baseName, List<String> allGlobalNames) {
        String name = baseName;
        int i = 0;
        while (allGlobalNames.contains(name)) {
            name = baseName + i;
            i = i + 1;
        }
        return name;
    }

    @NotNull
    public static List<String> possibleFunctionNames(PsiElement valueToReplace) {
        final WeaveDocument document = WeavePsiUtils.getDocument(valueToReplace);
        if (document == null) {
            return Collections.emptyList();
        }
        final List<String> allGlobalNames = WeavePsiUtils.getAllGlobalNames(document);
        return possibleNamesFor(valueToReplace, allGlobalNames, "get", "myFunction");
    }
}
