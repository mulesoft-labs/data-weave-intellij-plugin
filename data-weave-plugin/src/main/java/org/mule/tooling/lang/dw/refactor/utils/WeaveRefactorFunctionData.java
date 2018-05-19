package org.mule.tooling.lang.dw.refactor.utils;

import org.mule.weave.v2.ts.WeaveType;
import scala.Option;

import java.util.List;

public class WeaveRefactorFunctionData {

    private String functionName;
    private List<WeaveArgumentInfo> argumentInfos;
    private boolean addArgumentTypes;
    private boolean addReturnType;
    private Option<WeaveType> returnType;

    public WeaveRefactorFunctionData(String functionName, List<WeaveArgumentInfo> argumentInfos, Option<WeaveType> returnType, boolean addArgumentTypes, boolean addReturnType) {
        this.functionName = functionName;
        this.argumentInfos = argumentInfos;
        this.addArgumentTypes = addArgumentTypes;
        this.addReturnType = addReturnType;
        this.returnType = returnType;
    }

    public String getFunctionName() {
        return functionName;
    }

    public List<WeaveArgumentInfo> getArgumentInfos() {
        return argumentInfos;
    }

    public boolean isAddArgumentTypes() {
        return addArgumentTypes;
    }

    public boolean isAddReturnType() {
        return addReturnType;
    }

    public Option<WeaveType> getReturnType() {
        return returnType;
    }
}
