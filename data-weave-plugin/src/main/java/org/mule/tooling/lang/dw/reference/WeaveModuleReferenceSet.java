package org.mule.tooling.lang.dw.reference;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.JavaPsiFacadeImpl;
import org.jetbrains.annotations.NotNull;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

import java.util.*;


public class WeaveModuleReferenceSet {

    private static final Logger LOG = Logger.getInstance("#org.mule.tooling.lang.dw.reference.WeaveModuleReferenceSet");

    private final List<WeaveModulePsiReference> psiReferences;
    private PsiElement moduleReference;

    public WeaveModuleReferenceSet(@NotNull PsiElement moduleReference, String fqn) {
        this.moduleReference = moduleReference;
        this.psiReferences = this.parse(fqn);
    }

    @NotNull
    private List<WeaveModulePsiReference> parse(String str) {
        final List<WeaveModulePsiReference> references = new ArrayList<>();
        int current = 0;
        int index = 0;
        int next = 0;
        while (next >= 0) {
            next = findNextSeparator(str, current);
            int end = next >= 0 ? next : str.length();
            final TextRange range = new TextRange(current, end);
            references.addAll(createReferences(range, str.substring(0, end), index++));
            current = next + getSeparator().length();
        }
        return references;
    }

    private String getSeparator() {
        return NameIdentifier.SEPARATOR();
    }

    private int findNextSeparator(final String str, final int current) {
        final int next;
        next = str.indexOf(getSeparator(), current);
        return next;
    }

    public PsiElement getElement() {
        return moduleReference;
    }

    private List<WeaveModulePsiReference> getPsiReferences() {
        return psiReferences;
    }

    public PsiReference[] getAllReferences() {
        return psiReferences.toArray(new PsiReference[psiReferences.size()]);
    }

    protected Set<PsiFileSystemItem> getContext(String packageName) {
        final PsiPackage psiPackage = JavaPsiFacadeImpl.getInstance(getProject()).findPackage(packageName.replaceAll(NameIdentifier.SEPARATOR(), "."));
        final HashSet<PsiFileSystemItem> result = new HashSet<>();
        if (psiPackage != null) {
            final PsiDirectory[] directories = psiPackage.getDirectories();
            result.addAll(Arrays.asList(directories));
        }
        return result;
    }

    @NotNull
    private Project getProject() {
        return getElement().getProject();
    }


    protected List<WeaveModulePsiReference> createReferences(final TextRange range, String fqn, final int index) {
        WeaveModulePsiReference reference = new WeaveModulePsiReference(this, fqn, range, index);
        return Collections.singletonList(reference);
    }


    public WeaveModulePsiReference getReference(int index) {
        return getPsiReferences().get(index);
    }

}
