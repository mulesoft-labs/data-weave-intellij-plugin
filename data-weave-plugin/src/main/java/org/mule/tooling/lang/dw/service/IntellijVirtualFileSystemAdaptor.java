package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.mule.weave.v2.editor.ChangeListener;
import org.mule.weave.v2.editor.VirtualFileSystem;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;
import org.mule.weave.v2.sdk.WeaveResource;
import org.mule.weave.v2.sdk.WeaveResource$;
import org.mule.weave.v2.sdk.WeaveResourceResolver;
import scala.Option;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class IntellijVirtualFileSystemAdaptor implements VirtualFileSystem {

    private Project project;

    public IntellijVirtualFileSystemAdaptor(Project project) {
        this.project = project;
    }

    @Override
    public org.mule.weave.v2.editor.VirtualFile file(String path) {
        final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(path);
        return new IntellijVirtualFileAdaptor(this, fileByUrl, this.project, null);
    }

    @Override
    public void changeListener(ChangeListener cl) {
        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileAdapter() {
            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                IntellijVirtualFileAdaptor intellijVirtualFileAdaptor = new IntellijVirtualFileAdaptor(IntellijVirtualFileSystemAdaptor.this, event.getFile(), project, null);
                cl.onChanged(intellijVirtualFileAdaptor);
            }
        });
    }

    @Override
    public void onChanged(String path) {
        //nothing to do here
    }

    @Override
    public WeaveResourceResolver asResourceResolver() {
        return new IntellijWeaveResourceResolver(project, this);
    }

    @Override
    public org.mule.weave.v2.editor.VirtualFile[] list() {
        //TODO review what is needed here
        return new org.mule.weave.v2.editor.VirtualFile[0];
    }

    public static class IntellijWeaveResourceResolver implements WeaveResourceResolver {

        private Project project;
        private VirtualFileSystem fs;

        public IntellijWeaveResourceResolver(Project project, VirtualFileSystem fs) {
            this.project = project;
            this.fs = fs;
        }

        @Override
        public Option<WeaveResource> resolve(NameIdentifier name) {
            String fileRelativePath = NameIdentifierHelper.toWeaveFilePath(name);
            String relativePath = fileRelativePath.startsWith("/") ? fileRelativePath : "/" + fileRelativePath;
            Set<FileType> fileTypes = Collections.singleton(FileTypeManager.getInstance().getFileTypeByFileName(relativePath));
            final List<VirtualFile> fileList = new ArrayList<>();
            FileBasedIndex.getInstance().processFilesContainingAllKeys(FileTypeIndex.NAME, fileTypes, GlobalSearchScope.allScope(project), null, virtualFile -> {
                if (virtualFile.getPath().endsWith(relativePath)) {
                    fileList.add(virtualFile);
                }
                return true;
            });
            if (fileList.isEmpty()) {
                return Option.empty();
            } else {
                VirtualFile virtualFile = fileList.get(0);
                IntellijVirtualFileAdaptor intellijVirtualFileAdaptor = new IntellijVirtualFileAdaptor(this.fs, virtualFile, project, name);
                return Option.apply(intellijVirtualFileAdaptor.asResource());
            }
        }
    }

    public static class IntellijVirtualFileAdaptor implements org.mule.weave.v2.editor.VirtualFile {

        private VirtualFileSystem fs;
        private VirtualFile vfs;
        private Document document;
        private Project project;
        private NameIdentifier name;

        public IntellijVirtualFileAdaptor(VirtualFileSystem fs, VirtualFile vfs, Project project, NameIdentifier name) {
            this.fs = fs;
            this.vfs = vfs;
            this.document = FileDocumentManager.getInstance().getDocument(vfs);
            this.project = project;
            this.name = name;
        }

        @Override
        public VirtualFileSystem fs() {
            return fs;
        }

        @Override
        public String read() {
            return document.getText();
        }

        @Override
        public void write(String content) {
            if (document != null) {
                document.setText(content);
            }
        }

        @Override
        public boolean readOnly() {
            return false;
        }

        @Override
        public String path() {
            return vfs.getUrl();
        }

        @Override
        public WeaveResource asResource() {
            return WeaveResource$.MODULE$.apply(path(), read());
        }

        @Override
        public NameIdentifier getNameIdentifier() {
            if (this.name == null) {
                this.name = calculateNameIdentifier();
            }
            return this.name;

        }

        private NameIdentifier calculateNameIdentifier() {
            final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(vfs);
            if (contentRootForFile != null) {
                final String relPath = VfsUtil.getRelativePath(vfs, contentRootForFile);
                return NameIdentifierHelper.fromWeaveFilePath(relPath);
            } else {
                return NameIdentifierHelper.fromWeaveFilePath(path());
            }
        }
    }
}
