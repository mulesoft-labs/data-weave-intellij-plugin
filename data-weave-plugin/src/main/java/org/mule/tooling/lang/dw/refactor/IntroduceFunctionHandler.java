package org.mule.tooling.lang.dw.refactor;

import com.intellij.codeInsight.unwrap.ScopeHighlighter;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.TransactionGuard;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBList;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveElementFactory;
import org.mule.tooling.lang.dw.parser.psi.WeaveFqnIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDirective;
import org.mule.tooling.lang.dw.refactor.ui.WeaveExtractFunctionDialog;
import org.mule.tooling.lang.dw.refactor.utils.WeaveArgumentInfo;
import org.mule.tooling.lang.dw.refactor.utils.WeaveRefactorFunctionData;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.tooling.lang.dw.util.VariableScopeUtils;
import org.mule.weave.v2.editor.VariableDependency;
import org.mule.weave.v2.parser.ast.functions.FunctionNode;
import org.mule.weave.v2.scope.VariableScope;
import org.mule.weave.v2.ts.WeaveType;
import scala.Option;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static org.mule.tooling.lang.dw.util.StreamUtils.distinctByKey;
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
                .filter(distinctByKey(VariableScope::astNode))
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
        PsiElement scopeNode = VariableScopeUtils.getScopeNode(file, value);
        if (scopeNode instanceof WeaveDocument) {
            return "Global Scope";
        } else if (scopeNode.getParent() instanceof WeaveFunctionDefinition) {
            return ((WeaveFunctionDefinition) scopeNode.getParent()).getName() + " Scope";
        } else {
            String text = scopeNode.getText();
            return StringUtil.shortenTextWithEllipsis(text.replaceAll("\n", " "), 25, 1, true);
        }
    }

    public void doIntroduceFunction(@NotNull Project project, PsiFile psiFile, PsiElement valueToReplace, DWEditorToolingAPI toolingAPI, VariableScope parent) {
        final VariableDependency[] variableDependencies = toolingAPI.externalScopeDependencies(valueToReplace, parent);
        final List<String> possibleNames = NameProviderHelper.possibleFunctionNames(valueToReplace);
        final String functionName = possibleNames.get(0);
        final WeaveArgumentInfo[] weaveArgumentInfos = Stream.of(variableDependencies)
                .map(WeaveArgumentInfo::new)
                .toArray(WeaveArgumentInfo[]::new);

        WeaveRefactorFunctionData weaveRefactorFunctionData = null;
        if (!ApplicationManager.getApplication().isUnitTestMode()) {
            WeaveType weaveType = toolingAPI.typeOf(valueToReplace);
            WeaveExtractFunctionDialog weaveExtractFunctionDialog = new WeaveExtractFunctionDialog(project, Option.apply(weaveType), weaveArgumentInfos, functionName);
            if (weaveExtractFunctionDialog.showAndGet()) {
                weaveRefactorFunctionData = weaveExtractFunctionDialog.calculateFunctionData();
            }
        } else {
            weaveRefactorFunctionData = new WeaveRefactorFunctionData(functionName, Arrays.asList(weaveArgumentInfos), Option.empty(), true, false);
        }

        if (weaveRefactorFunctionData != null) {
            final List<String> argNames = weaveRefactorFunctionData.getArgumentInfos().stream().map(WeaveArgumentInfo::getArgName).collect(Collectors.toList());
            final WeaveFunctionCallExpression functionCall = WeaveElementFactory.createFunctionCall(project, weaveRefactorFunctionData.getFunctionName(), argNames);
            List<RenameVariable> renameVariables = weaveRefactorFunctionData.getArgumentInfos().stream()
                    .filter(arg -> !arg.getParamName().equals(arg.getArgName()))
                    .flatMap(arg -> {
                        return Stream.of(arg.getVariableDependency().usages()).map((weaveLocation -> {
                            int startOffset = weaveLocation.startPosition().index();
                            int endOffset = weaveLocation.endPosition().index();
                            WeaveFqnIdentifier elementOfClassAtRange = PsiTreeUtil.findElementOfClassAtRange(psiFile, startOffset, endOffset, WeaveFqnIdentifier.class);
                            return new RenameVariable(elementOfClassAtRange, arg.getParamName());
                        }));
                    }).collect(Collectors.toList());

            WeaveRefactorFunctionData finalWeaveRefactorFunctionData = weaveRefactorFunctionData;
            simpleRefactor(() -> {
                for (RenameVariable renameVariable : renameVariables) {
                    renameVariable.run();
                }
                return WeaveElementFactory.createFunctionDirective(project, finalWeaveRefactorFunctionData, valueToReplace);
            }, () -> functionCall, valueToReplace, getScopeNode(psiFile, parent), project, WeaveFunctionDirective::getFunctionDefinition);
        }

    }

    static class RenameVariable {
        WeaveFqnIdentifier toRename;
        String newName;

        public RenameVariable(WeaveFqnIdentifier toRename, String newName) {
            this.toRename = toRename;
            this.newName = newName;
        }

        void run() {
            if (toRename != null) {
                toRename.setName(newName);
            }
        }
    }
}
