package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;

import java.util.Arrays;
import java.util.Optional;

public class InputOutputTypesExtensionService {

    private static final ExtensionPointName<InputOutputTypesProvider> EP_NAME =
            ExtensionPointName.create("org.mule.tooling.intellij.dataweave.v2.inputOutputTypes");


    public static Optional<InputOutputTypesProvider> inputOutputTypesProvider(PsiFile file) {
        return Arrays.stream(EP_NAME.getExtensions()).filter((e) -> e.support(file)).findFirst();
    }
}
