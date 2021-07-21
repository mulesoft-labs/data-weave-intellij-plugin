package org.mule.tooling.lang.dw.util;

import com.intellij.ide.scratch.RootType;
import com.intellij.ide.scratch.ScratchFileService;
import com.intellij.ide.scratch.ScratchRootType;
import com.intellij.ide.scratch.ScratchUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class VirtualFileSystemUtils {

    @NotNull
    public static NameIdentifier calculateNameIdentifier(Project project, PsiFile vfs) {
        Optional<NameIdentifier> nameIdentifier = NameIdentifierService.resolveNameIdentifier(vfs);
        return nameIdentifier.orElseGet(() -> calculateNameIdentifier(project, vfs.getVirtualFile()));
    }

    @NotNull
    public static NameIdentifier calculateNameIdentifier(Project project, VirtualFile vfs) {
        final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(vfs);
        if (contentRootForFile != null) {
            return getNameIdentifierWithRelative(vfs, contentRootForFile);
        } else {
            final VirtualFile classRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getClassRootForFile(vfs);
            if (classRootForFile != null) {
                return getNameIdentifierWithRelative(vfs, classRootForFile);
            } else if (ScratchUtil.isScratch(vfs)) {
                final String relativePath = scratchPath(project, vfs);
                return NameIdentifierHelper.fromWeaveFilePath(relativePath);
            } else {
                PsiFile file = PsiManager.getInstance(project).findFile(vfs);
                if (file != null) {
                    Optional<NameIdentifier> nameIdentifier = NameIdentifierService.resolveNameIdentifier(file);
                    if (nameIdentifier.isPresent()) {
                        return nameIdentifier.get();
                    }
                }
                return NameIdentifierHelper.fromWeaveFilePath(vfs.getName());
            }
        }
    }

    public static String scratchPath(Project project, VirtualFile file) {
        RootType rootType = Objects.requireNonNull(RootType.forFile(file));
        String rootPath = ScratchFileService.getInstance().getRootPath(rootType);
        VirtualFile rootFile = LocalFileSystem.getInstance().findFileByPath(rootPath);
        if (rootFile == null || !VfsUtilCore.isAncestor(rootFile, file, false)) {
            throw new AssertionError(file.getPath());
        }
        StringBuilder sb = new StringBuilder();
        for (VirtualFile o = file; !rootFile.equals(o); o = o.getParent()) {
            String part = StringUtil.notNullize(rootType.substituteName(project, o), o.getName());
            if (sb.length() == 0 && part.indexOf('/') > -1) {
                // db console root type adds folder here, trim it
                part = part.substring(part.lastIndexOf('/') + 1);
            }
            sb.insert(0, "/" + part);
        }
        return sb.toString();
    }

    @Nullable
    public static VirtualFile resolve(Project project, NameIdentifier name) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        VirtualFile resolve = resolve(project, name, scope);
        if (resolve == null) {
            final String path = NameIdentifierHelper.toWeaveFilePath(name);
            try {
                resolve = ScratchFileService.getInstance().findFile(ScratchRootType.getInstance(), path, ScratchFileService.Option.existing_only);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resolve;
    }

    @Nullable
    public static VirtualFile resolve(Module module, NameIdentifier name) {
        GlobalSearchScope scope = module.getModuleScope();
        return resolve(module.getProject(), name, scope);

    }

    @Nullable
    private static VirtualFile resolve(Project project, NameIdentifier name, GlobalSearchScope scope) {
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
            }, scope);
        });

        return fileList.isEmpty() ? null : fileList.get(0);
    }

    public static NameIdentifier getNameIdentifierWithRelative(VirtualFile vfs, VirtualFile contentRootForFile) {
        final String relPath = VfsUtil.getRelativePath(vfs, contentRootForFile);
        if (relPath != null) {
            return NameIdentifierHelper.fromWeaveFilePath(relPath);
        } else {
            return NameIdentifierHelper.fromWeaveFilePath(contentRootForFile.getPath());
        }
    }
}
