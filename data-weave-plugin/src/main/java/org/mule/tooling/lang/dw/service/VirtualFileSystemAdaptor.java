package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Alarm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.editor.ChangeListener;
import org.mule.weave.v2.editor.VirtualFileSystem;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.WeaveResource;
import org.mule.weave.v2.sdk.WeaveResource$;
import org.mule.weave.v2.sdk.WeaveResourceResolver;
import scala.Option;

import java.util.ArrayList;
import java.util.List;

public class VirtualFileSystemAdaptor implements VirtualFileSystem, Disposable {

    private Project project;

    private Alarm myDocumentAlarm;
    private List<ChangeListener> listeners;

    public VirtualFileSystemAdaptor(Project project) {
        this.project = project;
        this.myDocumentAlarm = new Alarm(Alarm.ThreadToUse.POOLED_THREAD, this);
        this.listeners = new ArrayList<>();

        PsiManager.getInstance(this.project).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile file) {
                if (file != null) {
                    VirtualFile virtualFile = file.getVirtualFile();
                    onFileChanged(virtualFile);
                }
            }
        });

        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void contentsChanged(@NotNull VirtualFileEvent event) {
                onFileChanged(event.getFile());
            }
        });
    }

    @Override
    public org.mule.weave.v2.editor.VirtualFile file(String path) {
        final VirtualFile fileByUrl = VirtualFileManager.getInstance().findFileByUrl(path);
        return new IntellijVirtualFileAdaptor(this, fileByUrl, this.project, null);
    }

    @Override
    public void changeListener(ChangeListener cl) {
        this.listeners.add(cl);
    }

    private void onFileChanged(VirtualFile virtualFile) {
        //We cancel all request
        myDocumentAlarm.cancelAllRequests();
        myDocumentAlarm.addRequest(() -> {
            ApplicationManager.getApplication().runReadAction(() -> {
                final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(virtualFile);
                if (contentRootForFile != null) {
                    //If it is a file from the project
                    final IntellijVirtualFileAdaptor intellijVirtualFile = new IntellijVirtualFileAdaptor(VirtualFileSystemAdaptor.this, virtualFile, project, null);
                    for (ChangeListener listener : listeners) {
                        listener.onChanged(intellijVirtualFile);
                    }
                }
            });

        }, WeaveConstants.MODIFICATIONS_DELAY);

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
        public Option<WeaveResource> resolve(NameIdentifier name) {
            VirtualFile resolve = VirtualFileSystemUtils.resolve(name, project);
            if (resolve == null) {
                return Option.empty();
            } else {
                IntellijVirtualFileAdaptor intellijVirtualFileAdaptor = new IntellijVirtualFileAdaptor(this.fs, resolve, project, name);
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
                this.name = VirtualFileSystemUtils.calculateNameIdentifier(project, vfs);
            }
            return this.name;

        }


    }
}
