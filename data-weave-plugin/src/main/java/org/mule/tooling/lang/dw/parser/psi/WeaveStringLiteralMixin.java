package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.json.psi.impl.JSStringLiteralEscaper;
import com.intellij.lang.ASTNode;
import com.intellij.psi.LiteralTextEscaper;
import com.intellij.psi.PsiLanguageInjectionHost;
import com.intellij.psi.impl.source.tree.LeafElement;
import org.jetbrains.annotations.NotNull;


public abstract class WeaveStringLiteralMixin extends ASTWrapperPsiElement implements PsiLanguageInjectionHost {

  public WeaveStringLiteralMixin(@NotNull ASTNode node) {
    super(node);
  }

  public boolean isValidHost() {
    return true;
  }

  public PsiLanguageInjectionHost updateText(@NotNull String text) {
    ASTNode valueNode = this.getNode().getFirstChildNode();

    assert valueNode instanceof LeafElement;

    ((LeafElement)valueNode).replaceWithText(text);
    return this;
  }

  @NotNull
  public LiteralTextEscaper<? extends PsiLanguageInjectionHost> createLiteralTextEscaper() {
    return new JSStringLiteralEscaper<PsiLanguageInjectionHost>(this) {
      protected boolean isRegExpLiteral() {
        return false;
      }
    };
  }
}
