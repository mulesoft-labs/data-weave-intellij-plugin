package org.mule.tooling.lang.dw.refactor;

import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDoExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveReferenceType;
import org.mule.tooling.lang.dw.parser.psi.WeaveType;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypeDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableReferenceExpression;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;

import java.util.LinkedHashSet;
import java.util.List;

public class IntroduceLocalVariableHandler extends AbstractIntroduceDirectiveHandler {

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext, PsiElement valueToReplace) {
        final PsiElement scopeElement = getTargetScope(psiFile, valueToReplace);
        if(scopeElement != null) {
            doRefactor(project, editor, valueToReplace, scopeElement);
        }
    }

    public void doRefactor(@NotNull Project project, Editor editor, PsiElement valueToReplace, PsiElement scopeElement) {
        final WeaveDoExpression doExpression = scopeElement instanceof WeaveDoExpression ? (WeaveDoExpression) scopeElement : null;
        final List<String> possibleNames = NameProviderHelper.possibleNamesForLocalVariable(valueToReplace, doExpression);
        final RefactorResult variableIntro = getWeaveInplaceVariableIntroducer(project, valueToReplace, scopeElement, possibleNames);
        if(variableIntro != null) {
            int startOffset = variableIntro.getVariableDefinition().getTextRange().getStartOffset();
            //If we don't move the  cursor then the Introducer doesn't work :(
            editor.getCaretModel().moveToOffset(startOffset);
            PsiDocumentManager.getInstance(project).doPostponedOperationsAndUnblockDocument(editor.getDocument());
            WeaveInplaceVariableIntroducer variableIntroducer = new WeaveInplaceVariableIntroducer(variableIntro.getVariableDefinition(), editor, project, "choose a variable", variableIntro.getOccurrences());
            variableIntroducer.performInplaceRefactoring(new LinkedHashSet<>(possibleNames));
        }
    }

    public PsiElement getTargetScope(PsiFile psiFile, PsiElement valueToReplace) {
        if (valueToReplace instanceof WeaveType) {
            return WeavePsiUtils.getWeaveDocument(psiFile);
        } else {
            return WeaveEditorToolingAPI.getInstance(psiFile.getProject()).scopeOf(psiFile, valueToReplace);
        }
    }


    public RefactorResult getWeaveInplaceVariableIntroducer(@NotNull Project project, PsiElement valueToReplace, PsiElement rootScope, List<String> possibleNames) {
        final String defaultName = possibleNames.get(0);
        if (valueToReplace instanceof WeaveType) {
            final WeaveTypeDirective weaveTypeDirective = WeaveElementFactory.createTypeDirective(project, defaultName, valueToReplace);
            final WeaveReferenceType typeReference = WeaveElementFactory.createTypeReference(project, defaultName);
            return simpleRefactor(() -> weaveTypeDirective, () -> typeReference, valueToReplace, rootScope, project, WeaveTypeDirective::getTypeDefinition);
        } else {
            final WeaveVariableDirective varDirective = WeaveElementFactory.createVarDirective(project, defaultName, valueToReplace);
            final WeaveVariableReferenceExpression variableRef = WeaveElementFactory.createVariableRef(project, defaultName);
            return simpleRefactor(() -> varDirective, () -> variableRef, valueToReplace, rootScope, project, WeaveVariableDirective::getVariableDefinition);
        }
    }


}
