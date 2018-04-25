package org.mule.tooling.lang.dw.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;

public class VirtualFileSystemUtils {

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

    public static NameIdentifier getNameIdentifierWithRelative(VirtualFile vfs, VirtualFile contentRootForFile) {
        final String relPath = VfsUtil.getRelativePath(vfs, contentRootForFile);
        return NameIdentifierHelper.fromWeaveFilePath(relPath);
    }
}
