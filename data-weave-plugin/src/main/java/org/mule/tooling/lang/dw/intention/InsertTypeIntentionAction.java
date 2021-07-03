package org.mule.tooling.lang.dw.intention;

import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.service.WeaveToolingService;
import org.mule.weave.v2.ts.WeaveType;

public class InsertTypeIntentionAction extends PsiElementBaseIntentionAction implements IntentionAction {
    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getText() {
        return "Add weave type annotation.";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "Insert weave type annotation";
    }


    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, @NotNull PsiElement element) {
        if (element.getParent() instanceof WeaveIdentifier) {
            final WeaveVariableDefinition mayBeVarDef = PsiTreeUtil.getParentOfType(element, WeaveVariableDefinition.class, false, WeaveDocument.class);
            if (mayBeVarDef != null) {
                return mayBeVarDef.getType() == null;
            } else {
                final WeaveFunctionDefinition mayBeFunction = PsiTreeUtil.getParentOfType(element, WeaveFunctionDefinition.class, false, WeaveDocument.class);
                return mayBeFunction != null && mayBeFunction.getType() == null;
            }
        }
        return false;
    }


    @Override
    public void invoke(@NotNull Project project, Editor editor, @NotNull PsiElement element) throws IncorrectOperationException {
        final WeaveVariableDefinition weaveIdentifier = PsiTreeUtil.getParentOfType(element, WeaveVariableDefinition.class);
        if (weaveIdentifier != null) {
            final WeaveType weaveType = WeaveToolingService.getInstance(project).typeOf(weaveIdentifier.getExpression());
            if (weaveType != null && weaveIdentifier.getNameIdentifier() != null) {
                WriteAction.run(() -> {
                    editor.getDocument().insertString(weaveIdentifier.getNameIdentifier().getTextRange().getEndOffset(), ": " + weaveType.baseType().toString(false, true));
                });
            }
        } else {
            final WeaveFunctionDefinition functionDefinition = PsiTreeUtil.getParentOfType(element, WeaveFunctionDefinition.class);
            if (functionDefinition != null) {
                WeaveToolingService instance = WeaveToolingService.getInstance(project);
                if (functionDefinition.getType() == null) {
                    WeaveType weaveType = instance.typeOf(functionDefinition.getExpression());
                    if (weaveType != null && functionDefinition.getExpression() != null) {
                        WriteAction.run(() -> {
                            ASTNode[] children = functionDefinition.getNode().getChildren(TokenSet.create(WeaveTypes.R_PARREN));
                            if (children.length > 0) {
                                editor.getDocument().insertString(children[0].getTextRange().getEndOffset(), ": " + weaveType.baseType().toString(false, true));
                            }
                        });
                    }
                }
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
