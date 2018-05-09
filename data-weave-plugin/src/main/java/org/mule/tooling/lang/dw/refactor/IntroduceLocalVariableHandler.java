package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import java.util.LinkedHashSet;
import java.util.List;

import static org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory.createBlockSeparator;
import static org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory.createNewLine;


public class IntroduceLocalVariableHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int selectionEnd = editor.getSelectionModel().getSelectionEnd();
        PsiElement valueToReplace = PsiTreeUtil.findElementOfClassAtRange(psiFile, selectionStart, selectionEnd, PsiElement.class);
        if (valueToReplace != null) {
            final PsiElement rootScope = DWEditorToolingAPI.getInstance(psiFile.getProject()).scopeOf(psiFile, selectionStart);
            if (rootScope instanceof WeaveDocument) {
                new IntroduceConstantHandler().invoke(project, editor, psiFile, dataContext);
            } else if (rootScope != null) {
                final List<String> possibleNames = NameProviderHelper.possibleNamesOf(valueToReplace);
                final String defaultName = possibleNames.get(0);
                final WeaveVariableDirective newVarDirective = WeaveElementFactory.createVarDirective(project, defaultName, valueToReplace);
                final WeaveVariableReferenceExpression variableRef = WeaveElementFactory.createVariableRef(project, defaultName);

                final WeaveInplaceVariableIntroducer variableIntroducer = WriteCommandAction.runWriteCommandAction(project, (Computable<WeaveInplaceVariableIntroducer>) () -> {
                    WeaveDoExpression doBlock;
                    PsiElement variableRefReplaced;
                    if (valueToReplace == rootScope) {
                        doBlock = (WeaveDoExpression) valueToReplace.replace(WeaveElementFactory.createDoBlock(project, variableRef));
                        variableRefReplaced = doBlock.getExpression();
                    } else {
                        variableRefReplaced = valueToReplace.replace(variableRef);
                        if (rootScope instanceof WeaveDoExpression) {
                            doBlock = (WeaveDoExpression) rootScope;
                        } else {
                            WeaveDoExpression newDoBlock = WeaveElementFactory.createDoBlock(project, rootScope);
                            doBlock = (WeaveDoExpression) rootScope.replace(newDoBlock);
                        }
                    }


                    WeaveVariableDirective varDirective;
                    if (doBlock.getDirectiveList().size() > 0) {
                        WeaveDirective weaveDirective = doBlock.getDirectiveList().get(doBlock.getDirectiveList().size() - 1);

                        varDirective = (WeaveVariableDirective) doBlock.addAfter(newVarDirective, weaveDirective);
                        doBlock.addBefore(createNewLine(project), varDirective);
                        doBlock.addAfter(createNewLine(project), varDirective);
                    } else {
                        PsiElement anchor = doBlock.addBefore(createBlockSeparator(project), doBlock.getExpression());
                        doBlock.addAfter(createNewLine(project), anchor);
                        varDirective = (WeaveVariableDirective) doBlock.addBefore(newVarDirective, doBlock.addBefore(createNewLine(project), anchor));
                        doBlock.addBefore(createNewLine(project), varDirective);
                    }

                    return new WeaveInplaceVariableIntroducer(varDirective.getVariableDefinition(), editor, project, "choose a variable", new PsiElement[]{variableRefReplaced});

                });

                PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
                variableIntroducer.performInplaceRefactoring(new LinkedHashSet<>(possibleNames));
            } else {

            }
        } else {
            HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Selection doesn't represent an expression.");
        }
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }

}
