package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.SelectionModel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableReferenceExpression;

import java.util.LinkedHashSet;
import java.util.List;

import static org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils.findInnerElementRange;
import static org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils.getWeaveDocument;


public class IntroduceConstantHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        SelectionModel selectionModel = editor.getSelectionModel();
        final int selectionStart = selectionModel.getSelectionStart();
        final int selectionEnd = selectionModel.getSelectionEnd();
        WeaveDocument weaveDocument = getWeaveDocument(psiFile);
        if (weaveDocument != null) {
            PsiElement valueToReplace = findInnerElementRange(psiFile, selectionStart, selectionEnd);
            if (valueToReplace != null) {
                final List<String> possibleNames = NameProviderHelper.possibleNamesForGlobalVariable(valueToReplace);
                final String name = possibleNames.get(0);
                final WeaveVariableDirective varDirective = WeaveElementFactory.createVarDirective(project, name, valueToReplace);
                final WeaveVariableReferenceExpression expressionToReplace = WeaveElementFactory.createVariableRef(project, name);
                final WeaveInplaceVariableIntroducer variableIntroducer = WriteCommandAction.runWriteCommandAction(project, (Computable<WeaveInplaceVariableIntroducer>) () -> {
                    WeaveVariableReferenceExpression variableReference = (WeaveVariableReferenceExpression) valueToReplace.replace(expressionToReplace);

                    if (weaveDocument.getHeader() == null) {
                        WeaveHeader header = WeaveElementFactory.createHeader(project);
                        if (weaveDocument.getBody() == null) {
                            weaveDocument.add(header);
                        } else {
                            header = (WeaveHeader) weaveDocument.addBefore(header, weaveDocument.getBody());
                            weaveDocument.addAfter(WeaveElementFactory.createNewLine(project), header);
                            weaveDocument.addBefore(WeaveElementFactory.createBlockSeparator(project), weaveDocument.getBody());
                            weaveDocument.addBefore(WeaveElementFactory.createNewLine(project), weaveDocument.getBody());
                        }
                    }
                    WeaveVariableDirective newVarDirective;
                    List<WeaveDirective> directiveList = weaveDocument.getHeader().getDirectiveList();
                    if (directiveList.isEmpty()) {
                        newVarDirective = addDirectiveBefore(project, weaveDocument, varDirective, null);
                    } else {
                        PsiElement parentDirective = WeavePsiUtils.getParent(variableReference,
                                (psiElement) -> psiElement instanceof WeaveDirective && psiElement.getParent() instanceof WeaveHeader);
                        WeaveDirective anchor;
                        if (parentDirective != null) {
                            anchor = (WeaveDirective) parentDirective;
                            newVarDirective = addDirectiveBefore(project, weaveDocument, varDirective, anchor);
                        } else {
                            anchor = directiveList.get(directiveList.size() - 1);
                            newVarDirective = addDirectiveAfter(project, weaveDocument, varDirective, anchor);
                        }

                    }
                    int startOffset = variableReference.getFqnIdentifier().getTextRange().getStartOffset();
                    //If we don't move the  cursor then the Introducer doesn't work :(
                    editor.getCaretModel().moveToOffset(startOffset);
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
        assert header != null;
        if (anchor != null) {
            variable = header.addBefore(varDirective, anchor);
            header.addAfter(WeaveElementFactory.createNewLine(project), variable);
        } else {
            variable = header.add(varDirective);
            header.addBefore(WeaveElementFactory.createNewLine(project), variable);
        }
        return (WeaveVariableDirective) variable;
    }

    public WeaveVariableDirective addDirectiveAfter(@NotNull Project project, WeaveDocument weaveDocument, WeaveVariableDirective varDirective, WeaveDirective anchor) {
        WeaveHeader header = weaveDocument.getHeader();
        PsiElement variable;
        variable = header.addAfter(varDirective, anchor);
        header.addBefore(WeaveElementFactory.createNewLine(project), variable);
        return (WeaveVariableDirective) variable;
    }


    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }


}
