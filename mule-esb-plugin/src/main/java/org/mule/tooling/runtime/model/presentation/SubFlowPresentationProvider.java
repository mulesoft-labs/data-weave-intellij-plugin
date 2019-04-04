package org.mule.tooling.runtime.model.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.model.SubFlow;

import javax.swing.*;


public class SubFlowPresentationProvider extends PresentationProvider<SubFlow> {

    @Nullable
    @Override
    public String getName(SubFlow flow) {
        return flow.getName().getValue() != null ? "SubFlow : " + flow.getName().getValue() : "SubFlow";
    }

    @Nullable
    @Override
    public String getTypeName(SubFlow subFlow) {
        return "SubFlow";
    }


    @Nullable
    @Override
    public Icon getIcon(SubFlow flow) {
        return RuntimeIcons.MuleRunConfigIcon;
    }
}
