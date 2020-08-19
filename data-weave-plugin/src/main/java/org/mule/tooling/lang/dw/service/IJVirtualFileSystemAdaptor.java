package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.util.ScalaUtils;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.editor.ChangeListener;
import org.mule.weave.v2.editor.VirtualFileSystem;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.NameIdentifierHelper;
import org.mule.weave.v2.sdk.WeaveResource;
import org.mule.weave.v2.sdk.WeaveResourceResolver;
import scala.Option;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class IJVirtualFileSystemAdaptor implements VirtualFileSystem, Disposable {

    private Project project;

    private List<ChangeListener> listeners;

    public IJVirtualFileSystemAdaptor(Project project) {
        this.project = project;
        this.listeners = new ArrayList<>();

        PsiManager.getInstance(this.project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile file) {
                ReadAction.nonBlocking(() -> {
                    if (file != null) {
                        VirtualFile virtualFile = file.getVirtualFile();
                        onFileChanged(virtualFile);
                    }
                });
            }
        }, this);

        ApplicationManager.getApplication().getMessageBus().connect(this).subscribe(
                VirtualFileManager.VFS_CHANGES, new BulkFileListener() {

                    @Override
                    public void after(@NotNull List<? extends VFileEvent> events) {
                        ReadAction.nonBlocking(() -> {
                            for (VFileEvent event : events) {
                                if (event instanceof VFileContentChangeEvent) {
                                    onFileChanged(event.getFile());
                                }
                            }
                        });
                    }
                }
        );
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        this.listeners.remove(listener);
    }

    @Override
    public org.mule.weave.v2.editor.VirtualFile file(String path) {
        final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(path);
        if (fileByUrl == null) {
            return null;
        } else {
            return new IJVirtualFileAdaptor(this, fileByUrl, this.project, null);
        }

    }

    @Override
    public void changeListener(@NotNull ChangeListener cl) {
        this.listeners.add(cl);
    }

    @Override
    public void onChanged(org.mule.weave.v2.editor.VirtualFile vf) {
        for (ChangeListener listener : listeners) {
            if (listener != null) {
                //TODO this should never happen but ....
                listener.onChanged(vf);
            }
        }
    }

    private void onFileChanged(VirtualFile virtualFile) {
        if (project.isDisposed()) {
            return;
        }
        final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(virtualFile);
        if (contentRootForFile != null) {
            //If it is a file from the project
            final IJVirtualFileAdaptor intellijVirtualFile = new IJVirtualFileAdaptor(IJVirtualFileSystemAdaptor.this, virtualFile, project, null);
            onChanged(intellijVirtualFile);
        }

    }

    @Override
    public org.mule.weave.v2.editor.VirtualFile[] listFilesByNameIdentifier(String filter) {
        final List<org.mule.weave.v2.editor.VirtualFile> fileList = new ArrayList<>();
        FileTypeIndex.processFiles(WeaveFileType.getInstance(), virtualFile -> {
            NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(project, virtualFile);
            if (nameIdentifier.toString().contains(filter)) {
                fileList.add(new IJVirtualFileAdaptor(this, virtualFile, project, null));
                return true;
            } else {
                return true;
            }
        }, GlobalSearchScope.allScope(project));
        return fileList.toArray(new org.mule.weave.v2.editor.VirtualFile[0]);
    }

    @Override
    public WeaveResourceResolver asResourceResolver() {
        return new IntellijWeaveResourceResolver(project, this);
    }

    @Override
    public void dispose() {

    }

    public static class IntellijWeaveResourceResolver implements WeaveResourceResolver {

        private Project project;
        private VirtualFileSystem fs;

        public IntellijWeaveResourceResolver(Project project, VirtualFileSystem fs) {
            this.project = project;
            this.fs = fs;
        }

        @Override
        public Option<WeaveResource> resolvePath(String path) {
            final NameIdentifier theFileToSearch = NameIdentifierHelper.fromWeaveFilePath(path);
            final String theFileName = theFileToSearch.localName().name() + NameIdentifierHelper.extensionOf(theFileToSearch);
            final Optional<VirtualFile> first = FilenameIndex.getVirtualFilesByName(project, theFileName, GlobalSearchScope.everythingScope(project))
                    .stream()
                    .filter((vf) -> {
                        NameIdentifier nameIdentifier = VirtualFileSystemUtils.calculateNameIdentifier(project, vf);
                        return nameIdentifier.equals(theFileToSearch);
                    }).findFirst();
            final Optional<WeaveResource> ijVirtualFileAdaptor = first.map((vf) -> {
                return new IJVirtualFileAdaptor(this.fs, vf, project, theFileToSearch).asResource();
            });
            return ScalaUtils.toOption(ijVirtualFileAdaptor);
        }

        @Override
        public Option<WeaveResource> resolve(NameIdentifier name) {
            VirtualFile resolve = VirtualFileSystemUtils.resolve(project, name);
            if (resolve == null) {
                return Option.<WeaveResource>empty();
            } else {
                IJVirtualFileAdaptor intellijVirtualFileAdaptor = new IJVirtualFileAdaptor(this.fs, resolve, project, name);
                return Option.<WeaveResource>apply(intellijVirtualFileAdaptor.asResource());
            }
        }
    }

    public static class IJInMemoryFileAdaptor implements org.mule.weave.v2.editor.VirtualFile {

        private String content;
        private VirtualFileSystem fs;
        private String name;

        public IJInMemoryFileAdaptor(String content, VirtualFileSystem fs) {
            this.content = content;
            this.fs = fs;
            this.name = "Memory_" + UUID.randomUUID().toString() + ".dwl";
        }

        @Override
        public VirtualFileSystem fs() {
            return fs;
        }

        @Override
        public String read() {
            return content;
        }

        @Override
        public void write(String content) {
            this.content = content;
        }

        @Override
        public boolean readOnly() {
            return true;
        }

        @Override
        public String path() {
            return "mock://" + name;
        }


    }

    public static class IJVirtualFileAdaptor implements org.mule.weave.v2.editor.VirtualFile {

        private VirtualFileSystem fs;
        private VirtualFile vfs;
        @Nullable
        private Document document;
        private Project project;
        private NameIdentifier name;

        public IJVirtualFileAdaptor(VirtualFileSystem fs, @NotNull VirtualFile vfs, @NotNull Project project, @Nullable NameIdentifier name) {
            this.fs = fs;
            this.vfs = vfs;
            this.document = ReadAction.compute(() -> FileDocumentManager.getInstance().getDocument(vfs));
            this.project = project;
            this.name = name;
        }

        @Override
        public VirtualFileSystem fs() {
            return fs;
        }

        @Override
        public String read() {
            return ReadAction.compute(() -> {
                if (document != null) {
                    return document.getText();
                } else {
                    return "";
                }
            });
        }

        @Override
        public void write(String content) {
            if (document != null) {
                WriteAction.run(() -> document.setText(content));
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
        public NameIdentifier getNameIdentifier() {
            if (this.name == null) {
                this.name = VirtualFileSystemUtils.calculateNameIdentifier(project, vfs);
            }
            return this.name;
        }

    }
}
