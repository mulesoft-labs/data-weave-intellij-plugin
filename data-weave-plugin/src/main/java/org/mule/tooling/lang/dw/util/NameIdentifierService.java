package org.mule.tooling.lang.dw.util;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.psi.PsiFile;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

import java.util.Arrays;
import java.util.Optional;

public class NameIdentifierService {

    private static final ExtensionPointName<NameIdentifierProvider> EP_NAME =
            ExtensionPointName.create("org.mule.tooling.intellij.dataweave.v2.nameIdentifierProvider");

    public static Optional<NameIdentifier> resolveNameIdentifier(PsiFile file) {
        return Arrays.stream(EP_NAME.getExtensions()).filter((e) -> {
            return e.support(file);
        }).findFirst().map((ni) -> ni.resolveNameIdentifier(file));
    }
}
