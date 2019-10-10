package org.mule.tooling.lang.dw.util;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.weave.v2.parser.location.WeaveLocation;
import org.mule.weave.v2.scope.VariableScope;

import java.util.ArrayList;
import java.util.List;

public class VariableScopeUtils {

    @NotNull
    public static List<VariableScope> variablesScopesHierarchy(@NotNull VariableScope scope) {
        final List<VariableScope> result = new ArrayList<>();
        VariableScope myScope = scope;
        result.add(myScope);
        while (myScope.parentScope().isDefined()) {
            myScope = myScope.parentScope().get();
            result.add(myScope);
        }

        return result;
    }

    public static PsiElement getScopeNode(PsiFile file, @NotNull VariableScope scope) {
        final WeaveLocation nodeLocation = scope.astNode().location();
        return WeavePsiUtils.findInnerElementRange(file, nodeLocation.startPosition().index(), nodeLocation.endPosition().index());
    }
}
