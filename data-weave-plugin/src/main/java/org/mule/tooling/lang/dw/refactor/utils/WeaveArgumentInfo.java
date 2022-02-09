package org.mule.tooling.lang.dw.refactor.utils;

import com.intellij.refactoring.util.AbstractVariableData;
import org.mule.weave.v2.scope.VariableDependency;
import org.mule.weave.v2.ts.WeaveType;
import scala.Option;

public class WeaveArgumentInfo extends AbstractVariableData {

    private VariableDependency variableDependency;

    public WeaveArgumentInfo(VariableDependency variableDependency) {
        this.name = variableDependency.name();
        this.originalName = this.name;
        this.variableDependency = variableDependency;
    }

    public VariableDependency getVariableDependency() {
        return this.variableDependency;
    }

    public String getArgName() {
        return originalName;
    }

    public String getParamName() {
        return name;
    }

    public Option<WeaveType> getWtype() {
        return variableDependency.weaveType();
    }
}
