package org.mule.tooling.restsdk.utils;

import amf.core.client.common.remote.Content;
import amf.core.client.platform.resource.LoaderWithExecutionContext;
import amf.core.client.platform.resource.ResourceLoader;
import amf.core.internal.remote.JvmPlatform;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import scala.compat.java8.FutureConverters;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

import java.io.FileNotFoundException;
import java.util.concurrent.CompletableFuture;

/**
 * An AMF {@link ResourceLoader} that uses IntelliJ APIs to load files.
 * <p>
 * Using IntelliJ APIs provides caching (and the in-memory file text is shared with the IDE), applies
 * the project config while converting to text and supports the temp: scheme that appears on tests.
 */
public class IntellijResourceLoader implements ResourceLoader, LoaderWithExecutionContext {
    private final ExecutionContext executionContext;

    public IntellijResourceLoader(ExecutionContext executionContext) {
        this.executionContext = executionContext;
    }

    @Override
    public CompletableFuture<Content> fetch(String resource) {
        Future<Content> future = Future.apply(() -> {
            String text = ReadAction.compute(() -> {
                VirtualFile virtualFile = VirtualFileManager.getInstance().findFileByUrl(resource);
                if (virtualFile == null)
                    throw new RuntimeException(new FileNotFoundException(resource));
                Document document = FileDocumentManager.getInstance().getDocument(virtualFile);
                if (document == null)
                    throw new RuntimeException("Couldn't get document for " + resource);
                return document.getText();
            });
            return new Content(text, ensureFileAuthority(resource),
                    JvmPlatform.instance().extension(resource)
                            .flatMap(ext -> JvmPlatform.instance().mimeFromExtension(ext)));
        }, executionContext);
        return FutureConverters.toJava(future).toCompletableFuture();
    }

    @Override
    public boolean accepts(String resource) {
        String protocol = VirtualFileManager.extractProtocol(resource);
        return VirtualFileManager.getInstance().getFileSystem(protocol) != null;
    }

    private String ensureFileAuthority(String str) {
        return str.matches("[a-z]+:.*") ? str : "file://" + str;
    }

    @Override
    public ResourceLoader withExecutionContext(ExecutionContext ec) {
        return new IntellijResourceLoader(ec);
    }
}
