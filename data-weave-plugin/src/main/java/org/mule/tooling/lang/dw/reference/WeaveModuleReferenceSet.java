package org.mule.tooling.lang.dw.reference;

import com.intellij.openapi.util.Comparing;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveModuleReference;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;

import java.util.*;


public class WeaveModuleReferenceSet {

    private final List<WeaveModulePsiReference> psiReferences;
    private WeaveModuleReference moduleReference;
    private final GlobalSearchScope mySearchScope;

    public WeaveModuleReferenceSet(@NotNull WeaveModuleReference moduleReference) {
        this.moduleReference = moduleReference;
        this.mySearchScope = GlobalSearchScope.allScope(moduleReference.getProject());
        this.psiReferences = this.parse(moduleReference.getModuleFQN());
    }


    public GlobalSearchScope getSearchScope() {
        return mySearchScope;
    }

    @NotNull
    protected List<WeaveModulePsiReference> parse(String str) {
        final List<WeaveModulePsiReference> references = new ArrayList<>();
        int current = 0;
        int index = 0;
        int next = 0;
        while (next >= 0) {
            next = findNextSeparator(str, current);
            final TextRange range = new TextRange(current, (next >= 0 ? next : str.length()));
            references.addAll(createReferences(range, index++));
            current = next + getSeparator().length();
        }
        return references;
    }

    private String getSeparator() {
        return NameIdentifier.SEPARATOR();
    }

    protected int findNextSeparator(final String str, final int current) {
        final int next;
        next = str.indexOf(getSeparator(), current);
        return next;
    }

    public WeaveModuleReference getElement() {
        return moduleReference;
    }

    public List<WeaveModulePsiReference> getPsiReferences() {
        return psiReferences;
    }

    public PsiReference[] getAllReferences() {
        return psiReferences.toArray(new PsiReference[psiReferences.size()]);
    }

    public Set<PsiPackage> getInitialContext() {
        return Collections.singleton(JavaPsiFacade.getInstance(getElement().getProject()).findPackage(""));
    }

    protected List<WeaveModulePsiReference> createReferences(final TextRange range, final int index) {
        WeaveModulePsiReference reference = new WeaveModulePsiReference(this, range, index);
        return reference == null ? Collections.emptyList() : Collections.singletonList(reference);
    }


    public WeaveModulePsiReference getReference(int index) {
        return getPsiReferences().get(index);
    }

    public Collection<PsiPackage> resolvePackageName(@Nullable PsiPackage context, final String packageName) {
        if (context != null) {
            return ContainerUtil.filter(context.getSubPackages(mySearchScope), aPackage -> Comparing.equal(aPackage.getName(), packageName));
        }
        return Collections.emptyList();
    }
}
