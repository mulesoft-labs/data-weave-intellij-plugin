package org.mule.tooling.commons.wizard.maven;

import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

public class MavenCreatorUtils {

    public static void createMavenStructure(VirtualFile root) {
        try {
            VfsUtil.createDirectories(root.getPath() + "/src/main/java");
            VfsUtil.createDirectories(root.getPath() + "/src/main/resources");
            VfsUtil.createDirectories(root.getPath() + "/src/test/java");
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
