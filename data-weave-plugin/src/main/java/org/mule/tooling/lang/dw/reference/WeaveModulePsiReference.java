package org.mule.tooling.lang.dw.reference;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.psi.util.PsiElementFilter;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.WeaveIcons;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WeaveModulePsiReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private WeaveModuleReferenceSet referenceSet;
    private String fqn;
    private final int index;

    public WeaveModulePsiReference(WeaveModuleReferenceSet referenceSet, String fqn, TextRange range, int index) {
        super(referenceSet.getElement(), range);
        this.referenceSet = referenceSet;
        this.fqn = fqn;
        this.index = index;
    }


    @NotNull
    protected Set<PsiFileSystemItem> getContext() {
        if (index == 0) {
            return referenceSet.getContext("");
        } else {
            String parentFQN = referenceSet.getReference(index - 1).getFqn();
            return referenceSet.getContext(parentFQN);
        }
    }

    public String getFqn() {
        return fqn;
    }

    @Override
    @NotNull
    public Object[] getVariants() {
        PsiElementFilter filter = psiElement -> isWeaveFileOrFolder(psiElement);
        Set<LookupElement> subPackages = new HashSet<>();
        for (PsiFileSystemItem psiPackage : getContext()) {
            PsiElementProcessor.CollectElements<PsiFileSystemItem> elementProcessor = new PsiElementProcessor.CollectFilteredElements<>(filter);
            psiPackage.processChildren(elementProcessor);
            elementProcessor.getCollection().stream().map((item) -> item.getVirtualFile().getNameWithoutExtension()).forEach((name) -> {
                subPackages.add(LookupElementBuilder.create(name).withIcon(WeaveIcons.WeaveFileType));
            });
        }
        return subPackages.toArray();
    }

    private boolean isWeaveFileOrFolder(PsiElement psiElement) {
        if (psiElement instanceof PsiFileSystemItem) {
            return ((PsiFileSystemItem) psiElement).isDirectory() || psiElement.getContainingFile().getFileType() == WeaveFileType.getInstance();
        } else {
            return false;
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        Collection<PsiFileSystemItem> packages = new HashSet<>();
        for (PsiFileSystemItem psiPackage : getContext()) {
            PsiElementFilter filter = psiElement -> {
                if (isWeaveFileOrFolder(psiElement)) {
                    PsiFileSystemItem fileSystemItem = (PsiFileSystemItem) psiElement;
                    if (fileSystemItem.isDirectory()) {
                        return fileSystemItem.getName().equals(getValue());
                    } else {
                        return fileSystemItem.getVirtualFile().getNameWithoutExtension().equals(getValue());
                    }
                }
                return false;
            };
            PsiElementProcessor.CollectFilteredElements<PsiFileSystemItem> elementProcessor = new PsiElementProcessor.CollectFilteredElements<>(filter);
            psiPackage.processChildren(elementProcessor);
            packages.addAll(elementProcessor.getCollection());
        }
        return PsiElementResolveResult.createResults(packages);
    }
}
