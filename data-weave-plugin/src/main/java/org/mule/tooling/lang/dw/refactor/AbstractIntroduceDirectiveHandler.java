package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.hint.HintManagerImpl;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.refactoring.RefactoringActionHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveBody;
import org.mule.tooling.lang.dw.parser.psi.WeaveDirective;
import org.mule.tooling.lang.dw.parser.psi.WeaveDoExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory.createBlockSeparator;
import static org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory.createNewLine;
import static org.mule.tooling.lang.dw.util.ListUtils.last;

public abstract class AbstractIntroduceDirectiveHandler implements RefactoringActionHandler {
    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext) {
        PsiElement valueToReplace = getValueToReplace(psiFile, editor);
        if (valueToReplace != null) {
            invoke(project, editor, psiFile, dataContext, valueToReplace);
        } else {
            HintManagerImpl.getInstanceImpl().showErrorHint(editor, "Selection doesn't represent an expression.");
        }
    }

    public abstract void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext, PsiElement valueToReplace);

    @Nullable
    public PsiElement getValueToReplace(PsiFile psiFile, Editor editor) {
        int selectionStart = editor.getSelectionModel().getSelectionStart();
        int selectionEnd = editor.getSelectionModel().getSelectionEnd();
        PsiElement elementRange = WeavePsiUtils.findInnerElementRange(psiFile, selectionStart, selectionEnd);
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


    public <T extends PsiElement> IntroduceLocalVariableHandler.RefactorResult simpleRefactor(Supplier<T> extractedSupplier, Supplier<PsiElement> replacementSupplier, PsiElement toReplace, PsiElement scope, @NotNull Project project, Function<T, PsiNamedElement> mapper) {
        return WriteCommandAction.runWriteCommandAction(project, (Computable<IntroduceLocalVariableHandler.RefactorResult>) () -> {
            T extracted = extractedSupplier.get();
            final PsiElement replacement = replacementSupplier.get();
            final PsiElement scopeElement;
            final PsiElement newVariableRef;
            if (toReplace == scope) {
                if (scope.getParent() instanceof WeaveBody) {
                    scopeElement = getOrCreateHeader(project, (WeaveDocument) scope.getParent().getParent());
                    newVariableRef = toReplace.replace(replacement);
                } else {
                    scopeElement = toReplace.replace(WeaveElementFactory.createDoBlock(project, replacement));
                    newVariableRef = ((WeaveDoExpression) scopeElement).getExpression();
                }
            } else {
                newVariableRef = toReplace.replace(replacement);
                if (scope instanceof WeaveDoExpression) {
                    scopeElement = scope;
                } else if (scope instanceof WeaveHeader) {
                    scopeElement = scope;
                } else if (scope instanceof WeaveDocument) {
                    WeaveDocument document = (WeaveDocument) scope;
                    scopeElement = getOrCreateHeader(project, document);
                } else if(scope != null){
                    WeaveDoExpression newDoBlock = WeaveElementFactory.createDoBlock(project, scope);
                    scopeElement = scope.replace(newDoBlock);
                } else {
                    return null;
                }
            }

            final T newVarDirective;
            if (getDirectiveList(scopeElement).size() > 0) {
                WeaveDirective getLastDirective;
                PsiElement parentDirective = WeavePsiUtils.getParent(newVariableRef,
                        (psiElement) -> psiElement instanceof WeaveDirective && psiElement.getParent() == scopeElement);
                if (parentDirective != null) {
                    newVarDirective = (T) scopeElement.addBefore(extracted, parentDirective);
                    scopeElement.addAfter(createNewLine(project), newVarDirective);
                } else {
                    getLastDirective = last(getDirectiveList(scopeElement));
                    newVarDirective = (T) scopeElement.addAfter(extracted, getLastDirective);
                    scopeElement.addBefore(createNewLine(project), newVarDirective);
                }

            } else {
                PsiElement expression = scopeElement instanceof WeaveDoExpression ? ((WeaveDoExpression) scopeElement).getExpression() : scopeElement;
                final PsiElement anchor = scopeElement.addBefore(createBlockSeparator(project), expression);
                scopeElement.addAfter(createNewLine(project), anchor);
                newVarDirective = (T) scopeElement.addBefore(extracted, scopeElement.addBefore(createNewLine(project), anchor));
                scopeElement.addBefore(createNewLine(project), newVarDirective);
            }

            PsiElement[] occurrences = {newVariableRef};
            PsiNamedElement variableDefinition = mapper.apply(newVarDirective);
            return new RefactorResult(variableDefinition, occurrences);
        });
    }

    public WeaveHeader getOrCreateHeader(@NotNull Project project, WeaveDocument document) {
        WeaveHeader result = document.getHeader();
        if (result == null) {
            WeaveHeader weaveHeader = WeaveElementFactory.createHeader(project);
            result = (WeaveHeader) document.addBefore(weaveHeader, document.getBody());
            document.addAfter(WeaveElementFactory.createNewLine(project), result);
            document.addBefore(WeaveElementFactory.createBlockSeparator(project), document.getBody());
            document.addBefore(WeaveElementFactory.createNewLine(project), document.getBody());
        }
        return result;
    }

    public List<WeaveDirective> getDirectiveList(PsiElement doBlock) {
        if (doBlock instanceof WeaveDoExpression) {
            return ((WeaveDoExpression) doBlock).getDirectiveList();
        } else if (doBlock instanceof WeaveHeader) {
            return ((WeaveHeader) doBlock).getDirectiveList();
        }
        return Collections.emptyList();
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull PsiElement[] psiElements, DataContext dataContext) {

    }


    public static class RefactorResult {
        private PsiNamedElement variableDefinition;
        private PsiElement[] occurrences;

        public RefactorResult(PsiNamedElement variableDefinition, PsiElement[] occurrences) {
            this.variableDefinition = variableDefinition;
            this.occurrences = occurrences;
        }

        public PsiNamedElement getVariableDefinition() {
            return variableDefinition;
        }

        public PsiElement[] getOccurrences() {
            return occurrences;
        }
    }

}
