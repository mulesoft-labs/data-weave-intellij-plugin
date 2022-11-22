package org.mule.tooling.restsdk.generation;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.restsdk.utils.RestSdkHelper;

@SuppressWarnings("MissingActionUpdateThread")
public class BulkEndpointOperationsGeneratorAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null)
            return;
        new BulkEndpointOperationsGeneratorDialog(project, findModule(event)).show();
    }

    @Nullable
    private static Module findModule(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null)
            return null;

        Module module = null;
        PsiElement psiElement = event.getData(CommonDataKeys.PSI_ELEMENT);

        if (psiElement != null) {
            module = ModuleUtil.findModuleForPsiElement(psiElement);
        }

        if (module == null) {
            VirtualFile virtualFile = event.getData(CommonDataKeys.VIRTUAL_FILE);
            if (virtualFile != null) {
                ModuleUtil.findModuleForFile(virtualFile, project);
            }
        }

        if (module == null) {
            Editor editor = event.getData(CommonDataKeys.EDITOR);
            if (editor != null) {
                PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(editor.getDocument());
                if (psiFile != null) {
                    module = ModuleUtil.findModuleForFile(psiFile);
                }
            }
        }
        return module;
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        Module module = findModule(e);
        e.getPresentation().setEnabled(module != null && RestSdkHelper.findDescriptorFile(module) != null);
    }
}
