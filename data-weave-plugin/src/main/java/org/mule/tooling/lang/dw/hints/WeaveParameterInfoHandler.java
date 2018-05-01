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
import org.mule.tooling.lang.dw.service.DWEditorToolingAPI;
import org.mule.weave.v2.ts.FunctionType;
import org.mule.weave.v2.ts.FunctionTypeParameter;
import org.mule.weave.v2.ts.WeaveType;
import scala.collection.JavaConversions;
import scala.collection.Seq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
                functionTypes = toList(overloads);
            } else {
                functionTypes = Collections.singletonList(functionType);
            }

            ArgumentCallInfo[] argumentCallInfos = functionTypes.stream().map((funType) -> new ArgumentCallInfo(element, funType)).toArray(ArgumentCallInfo[]::new);
            context.setItemsToShow(argumentCallInfos);
            context.showHint(element.getFunctionCallArguments(), element.getFunctionCallArguments().getTextRange().getStartOffset(), this);
        }

    }

    @NotNull
    public static <T> T[] toArray(Seq<T> overloads, T[] a) {
        return JavaConversions.asJavaCollection(overloads).toArray(a);
    }

    @NotNull
    public static <T> List<T> toList(Seq<T> overloads) {
        return new ArrayList<>(JavaConversions.asJavaCollection(overloads));
    }

    @Nullable
    @Override
    public WeaveFunctionCallExpression findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return getFunctionCallExpression(context);
    }

    @Override
    public void updateParameterInfo(@NotNull WeaveFunctionCallExpression parameterOwner, @NotNull UpdateParameterInfoContext context) {
        if (context.getParameterOwner() == null || parameterOwner.equals(context.getParameterOwner())) {
            context.setParameterOwner(parameterOwner);
        } else {
            context.removeHint();
        }
    }

    @Override
    public void updateUI(ArgumentCallInfo p, @NotNull ParameterInfoUIContext context) {
        List<FunctionTypeParameter> functionTypeParameters = toList(p.getFunctionType().params());
        String hint = functionTypeParameters
                .stream()
                .map((param) -> param.name() + (param.optional() ? "? :" : " :") + param.wtype().toString(false, true))
                .reduce((acc, value) -> acc + ", " + value).orElse("<no parameters>");
        TextRange textRange = p.getFunctionCallExpression().getFunctionCallArguments().getTextRange();
        context.setupUIComponentPresentation(
                hint,
                textRange.getStartOffset(),
                textRange.getEndOffset(),
                !context.isUIComponentEnabled(),
                false,
                false,
                context.getDefaultParameterColor());

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


