package org.mule.tooling.lang.dw.util;

import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;

public class ModuleUtils {

    @Nullable
    public static Module findModule(PsiFile currentFile) {
        Module moduleForFile = ModuleUtil.findModuleForFile(currentFile);
        if (moduleForFile == null) {
            PsiElement context = ReadAction.compute(() -> currentFile.getContext());
            if (context != null) {
                return ReadAction.compute(() -> ModuleUtil.findModuleForFile(context.getContainingFile()));
            }
        }
        return moduleForFile;

    }
}
