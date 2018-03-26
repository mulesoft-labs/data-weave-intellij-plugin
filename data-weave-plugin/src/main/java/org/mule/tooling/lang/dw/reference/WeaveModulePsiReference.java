package org.mule.tooling.lang.dw.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class WeaveModulePsiReference extends PsiPolyVariantReferenceBase<PsiElement> {

    private WeaveModuleReferenceSet referenceSet;
    private int index;

    public WeaveModulePsiReference(WeaveModuleReferenceSet referenceSet, TextRange range, int index) {
        super(referenceSet.getElement(), range);
        this.referenceSet = referenceSet;
        this.index = index;
    }


    @NotNull
    protected Set<PsiPackage> getContext() {
        if (index == 0) return referenceSet.getInitialContext();
        Set<PsiPackage> psiPackages = new HashSet<>();
        for (ResolveResult resolveResult : referenceSet.getReference(index - 1).multiResolve(true)) {
            PsiElement psiElement = resolveResult.getElement();
            if (psiElement instanceof PsiPackage) {
                psiPackages.add((PsiPackage)psiElement);
            }
        }
        return psiPackages;
    }


    @Override
    @NotNull
    public Object[] getVariants() {
        Set<PsiPackage> subPackages = new HashSet<>();
        for (PsiPackage psiPackage : getContext()) {
            ContainerUtil.addAll(subPackages, psiPackage.getSubPackages(referenceSet.getSearchScope()));
        }
        return subPackages.toArray();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean b) {
        Collection<PsiPackage> packages = new HashSet<>();
        for (PsiPackage parentPackage : getContext()) {
            packages.addAll(referenceSet.resolvePackageName(parentPackage, getValue()));
        }
        return PsiElementResolveResult.createResults(packages);
    }
}
