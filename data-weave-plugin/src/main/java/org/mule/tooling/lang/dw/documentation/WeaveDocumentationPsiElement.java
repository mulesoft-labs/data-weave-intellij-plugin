package org.mule.tooling.lang.dw.documentation;

import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.FakePsiElement;

public class WeaveDocumentationPsiElement extends FakePsiElement {

    private PsiElement parent;
    private String docs;
    private String name;

    public WeaveDocumentationPsiElement(PsiElement parent, String docs, String name) {
        this.parent = parent;
        this.docs = docs;
        this.name = name;
    }

    @Override
    public PsiElement getParent() {
        return parent;
    }

    public String getDocs() {
        return docs;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
