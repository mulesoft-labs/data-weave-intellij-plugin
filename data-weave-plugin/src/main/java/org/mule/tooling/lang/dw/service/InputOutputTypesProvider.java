package org.mule.tooling.lang.dw.service;

import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.ts.WeaveType;

import java.util.Optional;

public interface InputOutputTypesProvider {

    boolean support(PsiFile psiFile);

    @NotNull
    ImplicitInput inputTypes(PsiFile psiFile);

    @NotNull
    Optional<WeaveType> expectedOutput(PsiFile psiFile);
}
