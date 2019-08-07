package org.mule.tooling.lang.dw.hints;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.lang.parameterInfo.CreateParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoContext;
import com.intellij.lang.parameterInfo.ParameterInfoHandler;
import com.intellij.lang.parameterInfo.ParameterInfoUIContext;
import com.intellij.lang.parameterInfo.ParameterInfoUtils;
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveBinaryExpression;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionCallExpression;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypes;
import org.mule.tooling.lang.dw.service.WeaveEditorToolingAPI;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.ts.FunctionType;
import org.mule.weave.v2.ts.FunctionTypeParameter;
import org.mule.weave.v2.ts.WeaveType;
import scala.collection.Seq;

import java.util.Collections;
import java.util.List;

public class WeaveParameterInfoHandler implements ParameterInfoHandler<WeaveParameterInfoHandler.FunctionCallInformation, WeaveParameterInfoHandler.ArgumentCallInfo> {


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
    public FunctionCallInformation findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        return getFunctionCallExpression(context);
    }

    @Nullable
    public FunctionCallInformation getFunctionCallExpression(@NotNull ParameterInfoContext context) {
        final PsiFile file = context.getFile();
        final int offset = context.getEditor().getCaretModel().getOffset();
        final PsiElement elementAt = file.findElementAt(offset);
        if (elementAt instanceof PsiWhiteSpace) {
            return null;
        }
        PsiElement parent = WeavePsiUtils.getParent(elementAt, (element) -> element instanceof WeaveFunctionCallExpression || element instanceof WeaveBinaryExpression);
        if (parent instanceof WeaveFunctionCallExpression) {
            return new WeaveFunctionCallInfo(((WeaveFunctionCallExpression) parent));
        } else if (parent instanceof WeaveBinaryExpression)
            return new BinaryFunctionCallInfo((WeaveBinaryExpression) parent);
        else {
            return null;
        }
    }

    @Override
    public void showParameterInfo(@NotNull FunctionCallInformation element, @NotNull CreateParameterInfoContext context) {
        WeaveEditorToolingAPI instance = WeaveEditorToolingAPI.getInstance(context.getProject());
        WeaveType weaveType = instance.typeOf(element.getFunction());
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
            context.showHint(element.getHintElement(), element.getStartOffset(), this);
        }

    }

    @Nullable
    @Override
    public FunctionCallInformation findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return getFunctionCallExpression(context);
    }

    @Override
    public void updateParameterInfo(@NotNull FunctionCallInformation parameterOwner, @NotNull UpdateParameterInfoContext context) {
        context.setCurrentParameter(parameterOwner.getCurrentParameterIndex(context));
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
        private FunctionCallInformation functionCallExpression;
        private FunctionType functionType;

        public ArgumentCallInfo(FunctionCallInformation functionCallExpression, FunctionType functionType) {
            this.functionCallExpression = functionCallExpression;
            this.functionType = functionType;
        }

        public FunctionCallInformation getFunctionCallExpression() {
            return functionCallExpression;
        }

        public FunctionType getFunctionType() {
            return functionType;
        }
    }

    public interface FunctionCallInformation {
        PsiElement getFunction();

        PsiElement getHintElement();

        int getStartOffset();

        int getCurrentParameterIndex(UpdateParameterInfoContext context);
    }

    public static class WeaveFunctionCallInfo implements FunctionCallInformation {

        WeaveFunctionCallExpression functionCall;

        public WeaveFunctionCallInfo(WeaveFunctionCallExpression functionCall) {
            this.functionCall = functionCall;
        }

        @Override
        public PsiElement getFunction() {
            return functionCall.getExpression();
        }

        @Override
        public PsiElement getHintElement() {
            return functionCall.getFunctionCallArguments();
        }

        @Override
        public int getStartOffset() {
            return functionCall.getFunctionCallArguments().getTextRange().getStartOffset();
        }

        @Override
        public int getCurrentParameterIndex(UpdateParameterInfoContext context) {
            return ParameterInfoUtils.getCurrentParameterIndex(functionCall.getFunctionCallArguments().getNode(), context.getOffset(), WeaveTypes.COMMA);
        }
    }

    public static class BinaryFunctionCallInfo implements FunctionCallInformation {
        WeaveBinaryExpression binaryExpression;

        public BinaryFunctionCallInfo(WeaveBinaryExpression binaryExpression) {
            this.binaryExpression = binaryExpression;
        }

        @Override
        public PsiElement getFunction() {
            return binaryExpression.getBinaryFunctionIdentifier();
        }

        @Override
        public PsiElement getHintElement() {
            return binaryExpression.getBinaryFunctionIdentifier();
        }

        @Override
        public int getStartOffset() {
            return binaryExpression.getBinaryFunctionIdentifier().getTextRange().getStartOffset();
        }

        @Override
        public int getCurrentParameterIndex(UpdateParameterInfoContext context) {
            int offset = context.getEditor().getCaretModel().getOffset();
            int endOffset = binaryExpression.getBinaryFunctionIdentifier().getTextRange().getEndOffset();
            if (endOffset < offset) {
                return 1;
            } else {
                return 0;
            }
        }
    }


}


