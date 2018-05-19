package org.mule.tooling.lang.dw.refactor.utils;

public class RefactorUtils {

    public static String calculateSignature(WeaveRefactorFunctionData data) {
        final String baseName = "fun " + data.getFunctionName();
        final String args = data.getArgumentInfos()
                .stream()
                .map((arg) -> data.isAddArgumentTypes() && arg.getWtype().isDefined() ? arg.getParamName() + ": " + arg.getWtype().get().toString(false, true) : arg.getParamName())
                .reduce((value, acc) -> value + ", " + acc).orElse("");
        final String returnDecl = data.isAddReturnType() && data.getReturnType().isDefined() ? ": " + data.getReturnType().get().toString(false, true) : "";
        return baseName + "(" + args + ")" + returnDecl;
    }
}
