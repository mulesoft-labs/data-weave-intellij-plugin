package org.mule.tooling.restsdk.reference;

import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLScalar;

public class NativeOperationReference  extends PsiReferenceBase<YAMLScalar> {
    public NativeOperationReference(@NotNull YAMLScalar element) {
        super(element);
    }

    @Override
    public @NotNull String getValue() {
        return myElement.getTextValue();
    }

    @Override
    public @Nullable PsiElement resolve() {
        var fqn = getValue();
        Project project = myElement.getProject();
        var module = ModuleUtil.findModuleForPsiElement(myElement);
        if (module == null)
            return null;
        return JavaPsiFacade.getInstance(project).findClass(fqn, GlobalSearchScope.moduleWithDependenciesAndLibrariesScope(module, false));
    }
}
