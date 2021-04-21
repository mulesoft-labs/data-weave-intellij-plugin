package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.concurrency.SequentialTaskExecutor;
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
import java.util.concurrent.ExecutorService;

public class IJVirtualFileSystemAdaptor implements VirtualFileSystem, Disposable {

    private final Project project;

    private final List<ChangeListener> listeners;

    private static final ExecutorService ourExecutor = SequentialTaskExecutor.createSequentialApplicationPoolExecutor("Console Filters");

    public IJVirtualFileSystemAdaptor(Project project) {
        this.project = project;
        this.listeners = new ArrayList<>();

        PsiManager.getInstance(this.project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile file) {
                if (file != null) {
                    VirtualFile virtualFile = file.getVirtualFile();
                    onFileChanged(virtualFile);
                }
            }
        }, this);

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent event) {
                onFileDeleted(event.getFile());
            }

            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                onFileCreated(event.getFile());
            }

            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                onFileChanged(event.getFile());
            }
        }, this);
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
        ReadAction.run(() -> {
            if (project.isDisposed()) {
                return;
            }
            //If it is a file from the project
            final IJVirtualFileAdaptor intellijVirtualFile = new IJVirtualFileAdaptor(IJVirtualFileSystemAdaptor.this, virtualFile, project, null);
            onChanged(intellijVirtualFile);
        });
    }

    private void onFileDeleted(VirtualFile virtualFile) {
        ReadAction.run(() -> {
            if (project.isDisposed()) {
                return;
            }

            //If it is a file from the project
            final IJVirtualFileAdaptor intellijVirtualFile = new IJVirtualFileAdaptor(IJVirtualFileSystemAdaptor.this, virtualFile, project, null);
            for (ChangeListener listener : listeners) {
                if (listener != null) {
                    listener.onDeleted(intellijVirtualFile);
                }
            }
        });
    }

    private void onFileCreated(VirtualFile virtualFile) {
        ReadAction.run(() -> {
            if (project.isDisposed()) {
                return;
            }

            //If it is a file from the project
            final IJVirtualFileAdaptor intellijVirtualFile = new IJVirtualFileAdaptor(IJVirtualFileSystemAdaptor.this, virtualFile, project, null);
            for (ChangeListener listener : listeners) {
                if (listener != null) {
                    listener.onCreated(intellijVirtualFile);
                }
            }
        });
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
                return Option.empty();
            } else {
                IJVirtualFileAdaptor intellijVirtualFileAdaptor = new IJVirtualFileAdaptor(this.fs, resolve, project, name);
                return Option.apply(intellijVirtualFileAdaptor.asResource());
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
            this.name = "Memory_" + UUID.randomUUID() + ".dwl";
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
        public boolean write(String content) {
            this.content = content;
            return true;
        }

        @Override
        public boolean readOnly() {
            return true;
        }

        @Override
        public String url() {
            return "mock://" + name;
        }

        @Override
        public String path() {
            return name;
        }

        @Override
        public WeaveResource asResource() {
            return org.mule.weave.v2.editor.VirtualFile.super.asResource();
        }

        @Override
        public NameIdentifier getNameIdentifier() {
            return NameIdentifierHelper.fromWeaveFilePath(name);
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
        public boolean write(String content) {
            if (document != null) {
                WriteAction.run(() -> {
                    document.setText(content);
                });
                return true;
            } else {
                return false;
            }

        }

        @Override
        public boolean readOnly() {
            return false;
        }

        @Override
        public String url() {
            return vfs.getUrl();
        }

        @Override
        public String path() {
            return vfs.getPath();
        }

        @Override
        public WeaveResource asResource() {
            return org.mule.weave.v2.editor.VirtualFile.super.asResource();
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
