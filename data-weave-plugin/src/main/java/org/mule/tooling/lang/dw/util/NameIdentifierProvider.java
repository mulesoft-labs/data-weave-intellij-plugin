package org.mule.tooling.lang.dw.util;

import com.intellij.psi.PsiFile;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

public interface NameIdentifierProvider {

    boolean support(PsiFile file);

    NameIdentifier resolveNameIdentifier(PsiFile file);
}
