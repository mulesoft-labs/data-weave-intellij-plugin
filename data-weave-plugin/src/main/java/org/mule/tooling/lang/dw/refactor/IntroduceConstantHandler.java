package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.editor.VisualPosition;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.introduce.inplace.InplaceVariableIntroducer;
import com.intellij.ui.LightweightHint;
import com.intellij.util.ArrayUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.weave.v2.ts.WeaveType;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.intellij.openapi.util.text.StringUtil.toUpperCase;


public class IntroduceConstantHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        SelectionModel selectionModel = editor.getSelectionModel();
        final int selectionStart = selectionModel.getSelectionStart();
        final int selectionEnd = selectionModel.getSelectionEnd();
        WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
        if (weaveDocument != null) {
            PsiElement valueToReplace = PsiTreeUtil.findElementOfClassAtRange(psiFile, selectionStart, selectionEnd, PsiElement.class);
            if (valueToReplace != null) {
                List<String> possibleNames = possibleNamesOf(valueToReplace);
                String name = possibleNames.get(0);
                WeaveVariableDirective varDirective = WeaveElementFactory.createVarDirective(project, name, valueToReplace);
                WeaveVariableReferenceExpression expressionToReplace = WeaveElementFactory.createVariable(project, name);
                WeaveInplaceVariableIntroducer variableIntroducer = WriteCommandAction.runWriteCommandAction(project, (Computable<WeaveInplaceVariableIntroducer>) () -> {
                    PsiElement variableReference = valueToReplace.replace(expressionToReplace);

                    if (weaveDocument.getHeader() == null) {
                        WeaveHeader header = WeaveElementFactory.createHeader(project);
                        if (weaveDocument.getBody() == null) {
                            weaveDocument.add(header);
                        } else {
                            weaveDocument.addBefore(header, weaveDocument.getBody());
                        }
                    }
                    WeaveVariableDirective newVarDirective;
                    List<WeaveDirective> directiveList = weaveDocument.getHeader().getDirectiveList();
                    if (directiveList.isEmpty()) {
                        newVarDirective = addDirectiveBefore(project, weaveDocument, varDirective, null);
                    } else {
                        Optional<WeaveDirective> firstDirective = directiveList
                                .stream()
                                .filter((directive) -> directive instanceof WeaveVariableDirective || directive instanceof WeaveFunctionDirective)
                                .findFirst();
                        newVarDirective = addDirectiveBefore(project, weaveDocument, varDirective, firstDirective.orElse(null));
                    }

                    return new WeaveInplaceVariableIntroducer(newVarDirective.getVariableDefinition(), editor, project, "choose a variable", new PsiElement[]{variableReference});
                });
                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                variableIntroducer.performInplaceRefactoring(new LinkedHashSet<>(possibleNames));

            } else {
                VisualPosition selectionStartPosition = selectionModel.getSelectionStartPosition();
                VisualPosition selectionEndPosition = selectionModel.getSelectionEndPosition();
                if (selectionStartPosition != null && selectionEndPosition != null) {
                    HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Unable to extract constant from selection", selectionStart, selectionEnd, HintManager.ABOVE, HintManager.HIDE_BY_ANY_KEY | HintManager.HIDE_BY_TEXT_CHANGE, 0);
                }
            }
        }
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
                currentWord.append(Character.toUpperCase(c));
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

    @NotNull
    public List<String> possibleNamesOf(PsiElement valueToReplace) {
        ArrayList<String> result = new ArrayList<>();
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
        return result.stream().map((name) -> makeNameUnike(valueToReplace, name)).collect(Collectors.toList());
    }

    public String makeNameUnike(PsiElement valueToReplace, String baseName) {
        WeaveDocument document = WeavePsiUtils.getDocument(valueToReplace);
        List<String> allGlobalNames = WeavePsiUtils.getAllGlobalNames(document);
        String name = baseName;
        int i = 0;
        while (allGlobalNames.contains(name)) {
            name = baseName + i;
            i = i + 1;
        }
        return name;
    }

    public WeaveVariableDirective addDirectiveBefore(@NotNull Project project, WeaveDocument weaveDocument, WeaveVariableDirective varDirective, WeaveDirective anchor) {
        WeaveHeader header = weaveDocument.getHeader();
        PsiElement variable;
        if (anchor != null) {
            variable = header.addBefore(varDirective, anchor);
        } else {
            variable = header.add(varDirective);
        }
        header.addAfter(createNewLine(project), variable);
        header.addBefore(createNewLine(project), variable);
        return (WeaveVariableDirective) variable;
    }


    @NotNull
    public PsiElement createNewLine(Project project) {
        PsiParserFacade helper = PsiParserFacade.SERVICE.getInstance(project);
        return helper.createWhiteSpaceFromText("\n");
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }


    public static class WeaveInplaceVariableIntroducer extends InplaceVariableIntroducer<PsiElement> {

        public WeaveInplaceVariableIntroducer(PsiNamedElement elementToRename, Editor editor, Project project, String title, PsiElement[] occurrences) {
            super(elementToRename, editor, project, title, occurrences, null);
        }

        @Nullable
        @Override
        protected PsiElement checkLocalScope() {
            return WeavePsiUtils.getWeaveDocument(myElementToRename.getContainingFile());
        }
    }
}
