package org.mule.tooling.lang.dw.lineMarker;

import com.intellij.execution.lineMarker.ExecutorAction;
import com.intellij.execution.lineMarker.RunLineMarkerContributor;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.testintegration.WeaveTestFramework;

public class RunTestLineMarkerContributor extends RunLineMarkerContributor {

    public  Info getInfo(@NotNull PsiElement element){
        if(WeaveTestFramework.isWeaveTestMethod(element)){
            final AnAction[] actions = ExecutorAction.getActions(Integer.MAX_VALUE);
            return new Info(
                    AllIcons.RunConfigurations.TestState.Run,
                    actions,
                    e -> StringUtil.join(ContainerUtil.mapNotNull(actions, action -> getText(action, e)), "\n")
            );
        }
        return null;
    }
}
