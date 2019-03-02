// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface WeaveAnnotationDefinition extends WeaveNamedElement {

    @NotNull
    List<WeaveAnnotationParameter> getAnnotationParameterList();

    @NotNull
    WeaveIdentifier getIdentifier();

    String getName();

    PsiElement setName(@NotNull String newName);

    PsiElement getNameIdentifier();

}
