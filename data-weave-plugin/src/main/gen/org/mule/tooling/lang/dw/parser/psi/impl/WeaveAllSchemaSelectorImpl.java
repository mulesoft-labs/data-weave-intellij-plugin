// This is a generated file. Not intended for manual editing.
package org.mule.tooling.lang.dw.parser.psi.impl;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElementVisitor;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.parser.psi.WeaveAllSchemaSelector;
import org.mule.tooling.lang.dw.parser.psi.WeaveVisitor;

public class WeaveAllSchemaSelectorImpl extends ASTWrapperPsiElement implements WeaveAllSchemaSelector {

    public WeaveAllSchemaSelectorImpl(@NotNull ASTNode node) {
        super(node);
    }

    public void accept(@NotNull WeaveVisitor visitor) {
        visitor.visitAllSchemaSelector(this);
    }

    public void accept(@NotNull PsiElementVisitor visitor) {
        if (visitor instanceof WeaveVisitor) accept((WeaveVisitor) visitor);
        else super.accept(visitor);
    }

}
