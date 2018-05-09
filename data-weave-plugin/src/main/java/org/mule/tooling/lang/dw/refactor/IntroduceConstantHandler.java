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
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.*;

import java.util.*;
import java.util.List;

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
                final List<String> possibleNames = NameProviderHelper.possibleNamesOf(valueToReplace);
                final String name = possibleNames.get(0);
                final WeaveVariableDirective varDirective = WeaveElementFactory.createVarDirective(project, name, valueToReplace);
                final WeaveVariableReferenceExpression expressionToReplace = WeaveElementFactory.createVariableRef(project, name);
                final WeaveInplaceVariableIntroducer variableIntroducer = WriteCommandAction.runWriteCommandAction(project, (Computable<WeaveInplaceVariableIntroducer>) () -> {
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
                HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Selection doesn't represent an expression.");
            }
        }
    }


    public WeaveVariableDirective addDirectiveBefore(@NotNull Project project, WeaveDocument weaveDocument, WeaveVariableDirective varDirective, WeaveDirective anchor) {
        WeaveHeader header = weaveDocument.getHeader();
        PsiElement variable;
        if (anchor != null) {
            variable = header.addBefore(varDirective, anchor);
        } else {
            variable = header.add(varDirective);
        }
        header.addAfter(WeaveElementFactory.createNewLine(project), variable);
        header.addBefore(WeaveElementFactory.createNewLine(project), variable);
        return (WeaveVariableDirective) variable;
    }


    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }


}
