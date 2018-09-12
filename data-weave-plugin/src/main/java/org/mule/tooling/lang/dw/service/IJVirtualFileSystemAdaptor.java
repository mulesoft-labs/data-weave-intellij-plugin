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
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeAnyChangeAbstractAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.util.VirtualFileSystemUtils;
import org.mule.weave.v2.editor.ChangeListener;
import org.mule.weave.v2.editor.VirtualFileSystem;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import org.mule.weave.v2.sdk.WeaveResource;
import org.mule.weave.v2.sdk.WeaveResourceResolver;
import scala.Option;

import java.util.ArrayList;
import java.util.List;
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
    ApplicationManager.getApplication().runReadAction(() -> {
      if (project.isDisposed()) {
        return;
      }
      final VirtualFile contentRootForFile = ProjectFileIndex.SERVICE.getInstance(project).getSourceRootForFile(virtualFile);
      if (contentRootForFile != null) {
        //If it is a file from the project
        final IJVirtualFileAdaptor intellijVirtualFile = new IJVirtualFileAdaptor(IJVirtualFileSystemAdaptor.this, virtualFile, project, null);
        onChanged(intellijVirtualFile);
      }
    });
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
    private Document document;
    private Project project;
    private NameIdentifier name;

    public IJVirtualFileAdaptor(VirtualFileSystem fs, @NotNull VirtualFile vfs, @NotNull Project project, NameIdentifier name) {
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
      return ReadAction.compute(() -> document.getText());
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
