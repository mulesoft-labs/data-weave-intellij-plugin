package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.unwrap.ScopeHighlighter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDirective;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.tooling.lang.dw.util.VariableScopeUtils;
import org.mule.weave.v2.editor.VariableDependency;
import org.mule.weave.v2.parser.ast.functions.FunctionNode;
import org.mule.weave.v2.scope.VariableScope;

import javax.swing.*;
import java.awt.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.mule.tooling.lang.dw.util.VariableScopeUtils.getScopeNode;
import static org.mule.tooling.lang.dw.util.VariableScopeUtils.variablesScopesHierarchy;

public class IntroduceFunctionHandler extends AbstractIntroduceDirectiveHandler {

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile psiFile, DataContext dataContext, PsiElement valueToReplace) {
        final DWEditorToolingAPI instance = DWEditorToolingAPI.getInstance(project);
        final VariableScope variableScope = instance.scopeOf(valueToReplace);
        final List<VariableScope> variableScopes = variablesScopesHierarchy(variableScope)
                .stream()
                .filter((varScope) -> !(varScope.astNode() instanceof FunctionNode))
                .collect(Collectors.toList());
        if (variableScopes.size() == 1) {
            VariableScope theScope = variableScopes.get(0);
            doIntroduceFunction(project, psiFile, valueToReplace, instance, theScope);
        } else {
            selectScope(psiFile, editor, variableScopes, (selectedScope) -> {
                doIntroduceFunction(project, psiFile, valueToReplace, instance, selectedScope);
                return null;
            });
        }
    }

    protected void selectScope(PsiFile file, Editor editor, List<VariableScope> variableScopes, Function<VariableScope, Void> selectionCallback) {
        final JBList<VariableScope> list = new JBList<>(variableScopes);
        final ScopeHighlighter highlighter = new ScopeHighlighter(editor);

        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                final Component listCellRendererComponent = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                setText(getScopeName(file, (VariableScope) value));
                return listCellRendererComponent;
            }
        });

        list.addListSelectionListener(e -> {
            highlighter.dropHighlight();
            int index = list.getSelectedIndex();
            if (index < 0) {
                return;
            }
            final VariableScope variableScope = variableScopes.get(index);
            final PsiElement scopeNode = VariableScopeUtils.getScopeNode(file, variableScope);
            highlighter.highlight(scopeNode, Collections.singletonList(scopeNode));
        });

        JBPopupFactory.getInstance().createListPopupBuilder(list)
                .setTitle("Select the target scope")
                .setMovable(false)
                .setResizable(false)
                .setRequestFocus(true)
                .setItemChoosenCallback(() -> {
                    VariableScope selectedValue = list.getSelectedValue();
                    if (selectedValue != null) {
                        TransactionGuard.getInstance().submitTransactionLater(requireNonNull(editor.getProject()), () -> selectionCallback.apply(selectedValue));
                    }
                })
                .addListener(new JBPopupListener() {
                    @Override
                    public void beforeShown(LightweightWindowEvent lightweightWindowEvent) {

                    }

                    @Override
                    public void onClosed(LightweightWindowEvent lightweightWindowEvent) {
                        highlighter.dropHighlight();
                    }
                })
                .createPopup()
                .showInBestPositionFor(editor);
    }

    private String getScopeName(PsiFile file, VariableScope value) {
        if (value.name().isDefined()) {
            return value.name().get();
        } else {
            PsiElement scopeNode = VariableScopeUtils.getScopeNode(file, value);
            String text = scopeNode.getText();
            return StringUtil.shortenTextWithEllipsis(text.replaceAll("\n", " "), 25, 1, true);
        }

    }

    public void doIntroduceFunction(@NotNull Project project, PsiFile psiFile, PsiElement valueToReplace, DWEditorToolingAPI instance, VariableScope parent) {
        final VariableDependency[] variableDependencies = instance.externalScopeDependencies(valueToReplace, parent);
        final List<String> possibleNames = NameProviderHelper.possibleFunctionNames(valueToReplace);
        final String functionName = possibleNames.get(0);
        final List<String> paramNames = Stream.of(variableDependencies).map(VariableDependency::name).collect(Collectors.toList());
        final WeaveFunctionCallExpression functionCall = WeaveElementFactory.createFunctionCall(project, functionName, paramNames);
        final WeaveFunctionDirective functionDirective = WeaveElementFactory.createFunctionDirective(project, functionName, paramNames, valueToReplace);
        simpleRefactor(functionDirective, functionCall, valueToReplace, getScopeNode(psiFile, parent), project, WeaveFunctionDirective::getFunctionDefinition);
    }
}
