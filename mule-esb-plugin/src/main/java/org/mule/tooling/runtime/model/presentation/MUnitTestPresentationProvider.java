package org.mule.tooling.runtime.model.presentation;

import com.intellij.ide.presentation.PresentationProvider;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.model.MUnitTest;

import javax.swing.*;


public class MUnitTestPresentationProvider extends PresentationProvider<MUnitTest> {

    @Nullable
    @Override
    public String getName(MUnitTest test) {
        return test.getName().getValue() != null ? "Test : " + test.getName().getValue() : "Test";
    }

    @Nullable
    @Override
    public String getTypeName(MUnitTest mUnitTest) {
        return "MUnitTest";
    }

    @Nullable
    @Override
    public Icon getIcon(MUnitTest test) {
        return RuntimeIcons.MuleRunConfigIcon;
    }
}
