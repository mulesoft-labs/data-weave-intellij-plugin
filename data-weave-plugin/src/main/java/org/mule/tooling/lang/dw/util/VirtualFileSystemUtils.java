package org.mule.tooling.lang.dw.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;

import java.util.ArrayList;
import java.util.List;

public class VirtualFileSystemUtils {

    @NotNull
    public static NameIdentifier calculateNameIdentifier(Project project, VirtualFile vfs) {
        final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(vfs);
        if (contentRootForFile != null) {
            return getNameIdentifierWithRelative(vfs, contentRootForFile);
        } else {
            final VirtualFile classRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getClassRootForFile(vfs);
            if (classRootForFile != null) {
                return getNameIdentifierWithRelative(vfs, classRootForFile);
            } else {
                return NameIdentifierHelper.fromWeaveFilePath(vfs.getPath());
            }
        }
    }

    public static VirtualFile resolve(NameIdentifier name, Project project) {
        final List<VirtualFile> fileList = new ArrayList<>();
        ApplicationManager.getApplication().runReadAction(() -> {
            FileTypeIndex.processFiles(WeaveFileType.getInstance(), virtualFile -> {
                NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(project, virtualFile);
                if (name.equals(nameIdentifier)) {
                    fileList.add(virtualFile);
                    return false;
                } else {
                    return true;
                }
            }, GlobalSearchScope.allScope(project));
        });

        return fileList.isEmpty() ? null : fileList.get(0);

    }

    public static NameIdentifier getNameIdentifierWithRelative(VirtualFile vfs, VirtualFile contentRootForFile) {
        final String relPath = VfsUtil.getRelativePath(vfs, contentRootForFile);
        return NameIdentifierHelper.fromWeaveFilePath(relPath);
    }
}
