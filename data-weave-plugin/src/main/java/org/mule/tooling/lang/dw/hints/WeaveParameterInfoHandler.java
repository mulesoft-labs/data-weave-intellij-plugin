package org.mule.tooling.lang.dw.hints;

import com.intellij.lang.parameterInfo.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.service.WeaveToolingService;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.weave.v2.ts.FunctionType;
import org.mule.weave.v2.ts.FunctionTypeParameter;
import org.mule.weave.v2.ts.WeaveType;
import scala.collection.Seq;

import java.util.Collections;
import java.util.List;

public class WeaveParameterInfoHandler implements ParameterInfoHandler<WeaveExpression, WeaveParameterInfoHandler.ArgumentCallInfo> {

    @Nullable
    @Override
    public WeaveExpression findElementForParameterInfo(@NotNull CreateParameterInfoContext context) {
        final WeaveExpression functionCallExpression = getFunctionCallExpression(context);
        if (functionCallExpression != null) {
            final FunctionCallInformation element = FunctionCallInformation.fromWeaveExpression((WeaveExpression) functionCallExpression);
            WeaveToolingService instance = WeaveToolingService.getInstance(context.getProject());
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
                context.setHighlightedElement(functionCallExpression);
            }
        }
        return functionCallExpression;
    }

    @Nullable
    public WeaveExpression getFunctionCallExpression(@NotNull ParameterInfoContext context) {
        final PsiFile file = context.getFile();
        final int offset = context.getEditor().getCaretModel().getOffset();
        final PsiElement elementAt = file.findElementAt(offset);
        if (elementAt instanceof PsiWhiteSpace) {
            return null;
        }
        PsiElement parent = WeavePsiUtils.getParent(elementAt, (element) -> element instanceof WeaveFunctionCallExpression || element instanceof WeaveBinaryExpression);
        if (parent instanceof WeaveFunctionCallExpression || parent instanceof WeaveBinaryExpression) {
            return (WeaveExpression) parent;
        } else {
            return null;
        }


    }

    @Override
    public void showParameterInfo(@NotNull WeaveExpression expression, @NotNull CreateParameterInfoContext context) {
        final FunctionCallInformation element = FunctionCallInformation.fromWeaveExpression(expression);
        context.showHint(expression, element.getStartOffset(), this);
    }

    @Nullable
    @Override
    public WeaveExpression findElementForUpdatingParameterInfo(@NotNull UpdateParameterInfoContext context) {
        return getFunctionCallExpression(context);
    }

    @Override
    public void updateParameterInfo(@NotNull WeaveExpression expression, @NotNull UpdateParameterInfoContext context) {
        final FunctionCallInformation parameterOwner = FunctionCallInformation.fromWeaveExpression(expression);
        context.setCurrentParameter(parameterOwner.getCurrentParameterIndex(context));
        context.setParameterOwner(expression);

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

//        context.setupRawUIComponentPresentation(builder.toString());
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
        private final FunctionCallInformation functionCallExpression;
        private final FunctionType functionType;

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

        static FunctionCallInformation fromWeaveExpression(WeaveExpression expression) {
            if (expression instanceof WeaveFunctionCallExpression) {
                return new WeaveFunctionCallInfo((WeaveFunctionCallExpression) expression);
            } else if (expression instanceof WeaveBinaryExpression) {
                return new BinaryFunctionCallInfo((WeaveBinaryExpression) expression);
            } else {
                throw new RuntimeException("Expecting function call expression but got  : '" + expression.getClass() + "'");
            }
        }
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


