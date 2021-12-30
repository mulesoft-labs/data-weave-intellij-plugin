package org.mule.tooling.lang.dw.hints;

import com.intellij.codeInsight.hints.HintInfo;
import com.intellij.codeInsight.hints.InlayInfo;
import com.intellij.codeInsight.hints.InlayParameterHintsProvider;
import com.intellij.lang.parameterInfo.ParameterInfoUtils;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallArguments;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypes;
import org.mule.tooling.lang.dw.service.WeaveToolingService;
import org.mule.tooling.lang.dw.settings.DataWeaveSettingsState;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.ts.FunctionType;
import org.mule.weave.v2.ts.FunctionTypeParameter;
import org.mule.weave.v2.ts.WeaveType;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public class WeaveInlayParameterHintsProvider implements InlayParameterHintsProvider {
    @NotNull
    @Override
    public List<InlayInfo> getParameterHints(PsiElement element) {
        if (!DataWeaveSettingsState.getInstance().getShowTypeInference() || DataWeaveSettingsState.getInstance().isBigFileForSemanticAnalysis(element.getContainingFile())) {
            return Collections.emptyList();
        } else {
            PsiElement parent = element.getParent();
            if (parent instanceof WeaveFunctionCallArguments && !(element instanceof LeafPsiElement)) {
                int currentParameterIndex = ParameterInfoUtils.getCurrentParameterIndex(parent.getNode(), element.getNode().getStartOffset(), WeaveTypes.COMMA);
                WeaveFunctionCallExpression functionCall = (WeaveFunctionCallExpression) WeavePsiUtils.getParent(parent, (p) -> p instanceof WeaveFunctionCallExpression);
                WeaveToolingService instance = WeaveToolingService.getInstance(element.getProject());
                WeaveType weaveType = instance.typeOf(functionCall.getExpression());
                if (weaveType instanceof FunctionType) {
                    FunctionType ft = (FunctionType) weaveType;
                    List<FunctionTypeParameter> functionTypeParameters = ScalaUtils.toList(ft.params());
                    if (functionTypeParameters.size() > currentParameterIndex) {
                        String name;
                        if (functionTypeParameters.get(0).optional()) {
                            int optionalParams = 0;
                            for (FunctionTypeParameter functionTypeParameter : functionTypeParameters) {
                                if (functionTypeParameter.optional()) {
                                    optionalParams = optionalParams + 1;
                                } else {
                                    break;
                                }
                            }
                            int shift;
                            if (parent.getChildren().length > (functionTypeParameters.size() - optionalParams)) {
                                shift = functionTypeParameters.size() - parent.getChildren().length;
                            } else {
                                shift = optionalParams;
                            }
                            final int index = currentParameterIndex + shift;
                            name = index >= 0 && index < functionTypeParameters.size() ? functionTypeParameters.get(index).name() : "";

                        } else {
                            name = functionTypeParameters.get(currentParameterIndex).name();

                        }
                        return Collections.singletonList(new InlayInfo(name, element.getTextOffset()));
                    }
                }
                return Collections.emptyList();
            } else {
                return Collections.emptyList();
            }
        }
    }

    @Nullable
    @Override
    public HintInfo getHintInfo(PsiElement element) {
        return null;
    }

    @Override
    public String getInlayPresentation(@NotNull String inlayText) {
        return inlayText + "=";
    }

    @NotNull
    @Override
    public Set<String> getDefaultBlackList() {
        return Collections.emptySet();
    }
}
