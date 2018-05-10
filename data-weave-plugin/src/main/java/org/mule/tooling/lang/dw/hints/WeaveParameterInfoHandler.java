package org.mule.tooling.lang.dw.hints;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.*;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypes;
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.ts.FunctionType;
import org.mule.weave.v2.ts.FunctionTypeParameter;
import org.mule.weave.v2.ts.WeaveType;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.Collections;
import java.util.List;

import static com.intellij.lang.parameterInfo.ParameterInfoUtils.getCurrentParameterIndex;

public class WeaveParameterInfoHandler implements ParameterInfoHandler<WeaveFunctionCallExpression, WeaveParameterInfoHandler.ArgumentCallInfo> {


    @Override
    public boolean couldShowInLookup() {
        return true;
    }

    @Nullable
    @Override
    public Object[] getParametersForLookup(LookupElement item, ParameterInfoContext context) {
        return new Object[0];
    }

    @Nullable
    @Override
    public WeaveFunctionCallExpression findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        return getFunctionCallExpression(context);
    }

    @Nullable
    public WeaveFunctionCallExpression getFunctionCallExpression(@NotNull ParameterInfoContext context) {
        final PsiFile file = context.getFile();
        final int offset = context.getEditor().getCaretModel().getOffset();
        final PsiElement elementAt = file.findElementAt(offset);
        if (elementAt instanceof PsiWhiteSpace) {
            return null;
        }
        return PsiTreeUtil.getParentOfType(elementAt, WeaveFunctionCallExpression.class);
    }

    @Override
    public void showParameterInfo(@NotNull WeaveFunctionCallExpression element, @NotNull CreateParameterInfoContext context) {
        DWEditorToolingAPI instance = DWEditorToolingAPI.getInstance(context.getProject());
        WeaveType weaveType = instance.typeOf(element.getExpression());
        if (weaveType instanceof FunctionType) {
            FunctionType functionType = (FunctionType) weaveType;
            List<FunctionType> functionTypes;
            if (functionType.isOverloaded()) {
                Seq<FunctionType> overloads = functionType.overloads();
                functionTypes = ScalaUtils.toList(overloads);
            } else {
                functionTypes = Collections.singletonList(functionType);
            }

            ArgumentCallInfo[] argumentCallInfos = functionTypes.stream().map((funType) -> new ArgumentCallInfo(element, funType)).toArray(ArgumentCallInfo[]::new);
            context.setItemsToShow(argumentCallInfos);
            context.showHint(element.getFunctionCallArguments(), element.getFunctionCallArguments().getTextRange().getStartOffset(), this);
        }

    }

    @Nullable
    @Override
    public WeaveFunctionCallExpression findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return getFunctionCallExpression(context);
    }

    @Override
    public void updateParameterInfo(@NotNull WeaveFunctionCallExpression parameterOwner, @NotNull UpdateParameterInfoContext context) {
        context.setCurrentParameter(getCurrentParameterIndex(parameterOwner.getFunctionCallArguments().getNode(), context.getOffset(), WeaveTypes.COMMA));
    }

    @Override
    public void updateUI(ArgumentCallInfo p, @NotNull ParameterInfoUIContext context) {
        List<FunctionTypeParameter> functionTypeParameters = ScalaUtils.toList(p.getFunctionType().params());


        // Figure out what particular presentation is actually selected. Take in
        // account possibility of the last variadic parameter.
        int selected = context.getCurrentParameterIndex();
        int start = 0;
        int end = 0;

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < functionTypeParameters.size(); ++i) {
            if (i != 0) {
                builder.append(", ");
            }
            if (i == selected) {
                start = builder.length();
            }
            builder.append(toUIString(functionTypeParameters.get(i)));

            if (i == selected) {
                end = builder.length();
            }
        }

        context.setupUIComponentPresentation(
                builder.toString(),
                start,
                end,
                false,
                false,
                false,
                context.getDefaultParameterColor());

    }

    @NotNull
    public String toUIString(FunctionTypeParameter param) {
        return param.name() + (param.optional() ? "? :" : " :") + param.wtype().toString(false, true);
    }

    public static class ArgumentCallInfo {
        private WeaveFunctionCallExpression functionCallExpression;
        private FunctionType functionType;

        public ArgumentCallInfo(WeaveFunctionCallExpression functionCallExpression, FunctionType functionType) {
            this.functionCallExpression = functionCallExpression;
            this.functionType = functionType;
        }

        public WeaveFunctionCallExpression getFunctionCallExpression() {
            return functionCallExpression;
        }

        public FunctionType getFunctionType() {
            return functionType;
        }
    }


}


