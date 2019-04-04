package org.mule.tooling.lang.dw.reference;


import com.intellij.lang.cacheBuilder.DefaultWordsScanner;
import com.intellij.lang.cacheBuilder.WordsScanner;
import com.intellij.lang.findUsages.FindUsagesProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.lexer.WeaveLexer;
import org.mule.tooling.lang.dw.parser.WeaveParserDefinition;
import org.mule.tooling.lang.dw.parser.psi.*;


public class WeaveFindUsagesProvider implements FindUsagesProvider {

    @Nullable
    @Override
    public WordsScanner getWordsScanner() {
        return new DefaultWordsScanner(new WeaveLexer(),
                TokenSet.create(WeaveTypes.ID),
                WeaveParserDefinition.COMMENTS,
                WeaveParserDefinition.STRING_TYPES
        );
    }

    @Override
    public boolean canFindUsagesFor(@NotNull PsiElement psiElement) {
        return psiElement instanceof PsiNamedElement;
    }

    @Nullable
    @Override
    public String getHelpId(@NotNull PsiElement psiElement) {
        return com.intellij.lang.HelpID.FIND_OTHER_USAGES;
    }

    @NotNull
    @Override
    public String getType(@NotNull PsiElement psiElement) {
        if (psiElement instanceof WeaveFunctionDefinition || psiElement instanceof WeaveBinaryExpression || psiElement instanceof WeaveCustomInterpolatorExpression || psiElement instanceof WeaveFunctionCallExpression) {
            return "function";
        } else if (psiElement instanceof WeaveFunctionParameter) {
            return "parameter";
        } else if (psiElement instanceof WeaveVariableDefinition || psiElement instanceof WeaveVariableReferenceExpression) {
            return "variable";
        } else if (psiElement instanceof WeaveTypeDefinition || psiElement instanceof WeaveReferenceType) {
            return "type";
        } else if (psiElement instanceof WeaveAnnotationDefinition) {
            return "annotation";
        } else {
            return psiElement.getClass().getSimpleName();
        }
    }

    @NotNull
    @Override
    public String getDescriptiveName(@NotNull PsiElement psiElement) {
        String name = ((PsiNamedElement) psiElement).getName();
        if (name == null) {
            return psiElement.getText();
        } else {
            return name;
        }
    }

    @NotNull
    @Override
    public String getNodeText(@NotNull PsiElement psiElement, boolean b) {
        return psiElement.getText();
    }
}
