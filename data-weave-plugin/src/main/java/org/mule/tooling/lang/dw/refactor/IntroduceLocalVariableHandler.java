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
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveBody;
import org.mule.tooling.lang.dw.parser.psi.WeaveDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveDoExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableReferenceExpression;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;

import java.util.LinkedHashSet;
import java.util.List;

import static org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory.createBlockSeparator;
import static org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory.createNewLine;


public class IntroduceLocalVariableHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        int selectionStart = editor.getSelectionModel().getSelectionStart();
        PsiElement valueToReplace = getValueToReplace(psiFile, editor);
        if (valueToReplace != null) {
            final PsiElement rootScope = getRootScope(psiFile, selectionStart);
            if (rootScope instanceof WeaveDocument) {
                new IntroduceConstantHandler().invoke(project, editor, psiFile, dataContext);
            } else if (rootScope != null) {
                final WeaveDoExpression doExpression = rootScope instanceof WeaveDoExpression ? (WeaveDoExpression) rootScope : null;
                final List<String> possibleNames = NameProviderHelper.possibleNamesForLocalVariable(valueToReplace, doExpression);
                final WeaveInplaceVariableIntroducer variableIntro = getWeaveInplaceVariableIntroducer(project, editor, valueToReplace, rootScope, possibleNames);
                variableIntro.performInplaceRefactoring(new LinkedHashSet<>(possibleNames));
            } else {
                HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Unable to perform the refactor.");
            }
        } else {
            HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Selection doesn't represent an expression.");
        }
    }

    @Nullable
    public PsiElement getValueToReplace(PsiFile psiFile, Editor editor) {
        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int selectionEnd = editor.getSelectionModel().getSelectionEnd();
        PsiElement elementRange = WeavePsiUtils.findElementRange(psiFile, selectionStart, selectionEnd);
        if (elementRange instanceof WeaveDocument) {
            WeaveBody body = ((WeaveDocument) elementRange).getBody();
            if (body != null) {
                return body.getExpression();
            } else {
                return null;
            }
        } else {
            return elementRange;
        }
    }

    public WeaveInplaceVariableIntroducer getWeaveInplaceVariableIntroducer(@NotNull Project project, Editor editor, PsiElement valueToReplace, PsiElement rootScope, List<String> possibleNames) {
        final String defaultName = possibleNames.get(0);
        final WeaveVariableDirective varDirective = WeaveElementFactory.createVarDirective(project, defaultName, valueToReplace);
        final WeaveVariableReferenceExpression variableRef = WeaveElementFactory.createVariableRef(project, defaultName);

        final WeaveInplaceVariableIntroducer variableIntroducer = WriteCommandAction.runWriteCommandAction(project, (Computable<WeaveInplaceVariableIntroducer>) () -> {
            final WeaveDoExpression doBlock;
            final WeaveVariableReferenceExpression newVariableRef;
            if (valueToReplace == rootScope) {
                doBlock = (WeaveDoExpression) valueToReplace.replace(WeaveElementFactory.createDoBlock(project, variableRef));
                newVariableRef = (WeaveVariableReferenceExpression) doBlock.getExpression();
            } else {
                newVariableRef = (WeaveVariableReferenceExpression) valueToReplace.replace(variableRef);
                if (rootScope instanceof WeaveDoExpression) {
                    doBlock = (WeaveDoExpression) rootScope;
                } else {
                    WeaveDoExpression newDoBlock = WeaveElementFactory.createDoBlock(project, rootScope);
                    doBlock = (WeaveDoExpression) rootScope.replace(newDoBlock);
                }
            }


            final WeaveVariableDirective newVarDirective;
            if (doBlock.getDirectiveList().size() > 0) {
                final WeaveDirective weaveDirective = doBlock.getDirectiveList().get(doBlock.getDirectiveList().size() - 1);
                newVarDirective = (WeaveVariableDirective) doBlock.addAfter(varDirective, weaveDirective);
                doBlock.addBefore(createNewLine(project), newVarDirective);
            } else {
                final PsiElement anchor = doBlock.addBefore(createBlockSeparator(project), doBlock.getExpression());
                doBlock.addAfter(createNewLine(project), anchor);
                newVarDirective = (WeaveVariableDirective) doBlock.addBefore(varDirective, doBlock.addBefore(createNewLine(project), anchor));
                doBlock.addBefore(createNewLine(project), newVarDirective);
            }

            int startOffset = newVariableRef.getFqnIdentifier().getTextRange().getStartOffset();
            //If we don't move the  cursor then the Introducer doesn't work :(
            editor.getCaretModel().moveToOffset(startOffset);
            return new WeaveInplaceVariableIntroducer(newVarDirective.getVariableDefinition(), editor, project, "choose a variable", new PsiElement[]{newVariableRef});

        });

        PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
        return variableIntroducer;
    }

    public PsiElement getRootScope(PsiFile psiFile, int selectionStart) {
        return DWEditorToolingAPI.getInstance(psiFile.getProject()).scopeOf(psiFile, selectionStart);
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }

}
