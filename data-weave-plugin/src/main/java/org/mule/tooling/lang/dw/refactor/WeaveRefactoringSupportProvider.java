package org.mule.tooling.lang.dw.refactor;


import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.refactoring.changeSignature.ChangeSignatureHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveNamedElement;

public class WeaveRefactoringSupportProvider extends RefactoringSupportProvider {

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceConstantHandler() {
        return new IntroduceConstantHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler() {
        return new IntroduceLocalVariableHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceVariableHandler(PsiElement element) {
        return new IntroduceLocalVariableHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractMethodHandler() {
        return new IntroduceFunctionHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractModuleHandler() {
        return new IntroduceModuleHandler();
    }

    @Override
    public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
        return element instanceof WeaveNamedElement;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(@NotNull PsiElement element, PsiElement context) {
        return element instanceof WeaveNamedElement;
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceFieldHandler() {
        return super.getIntroduceFieldHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceParameterHandler() {
        return super.getIntroduceParameterHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getIntroduceFunctionalParameterHandler() {
        return super.getIntroduceFunctionalParameterHandler();
    }

    @Override
    public RefactoringActionHandler getIntroduceFunctionalVariableHandler() {
        return super.getIntroduceFunctionalVariableHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getPullUpHandler() {
        return super.getPullUpHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getPushDownHandler() {
        return super.getPushDownHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractInterfaceHandler() {
        return super.getExtractInterfaceHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractSuperClassHandler() {
        return super.getExtractSuperClassHandler();
    }

    @Nullable
    @Override
    public ChangeSignatureHandler getChangeSignatureHandler() {
        return super.getChangeSignatureHandler();
    }

    @Nullable
    @Override
    public RefactoringActionHandler getExtractClassHandler() {
        return super.getExtractClassHandler();
    }
}
