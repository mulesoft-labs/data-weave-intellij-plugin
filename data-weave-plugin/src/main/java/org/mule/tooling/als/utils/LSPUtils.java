package org.mule.tooling.als.utils;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.text.LineColumn;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.mulesoft.lsp.feature.common.Position;
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeoutException;

public class LSPUtils {

  public static VirtualFile virtualFileFromEditor(Editor editor) {
    return FileDocumentManager.getInstance().getFile(editor.getDocument());
  }

  public static Position positionOf(PsiElement element) {
    final LineColumn lineNumber = StringUtil.offsetToLineColumn(element.getContainingFile().getText(), element.getTextOffset());
    return new Position(lineNumber.line, lineNumber.column);
  }

  public static TextDocumentIdentifier documentIdentifierOf(PsiFile element) {
    return new TextDocumentIdentifier(getUrl(element));
  }

  public static <T> T resultOf(Future<T> objectFuture) {
    try {
      return Await.result(objectFuture, Duration.Inf());
    } catch (InterruptedException | TimeoutException e) {
      throw new RuntimeException(e);
    }
  }


  @Nullable
  public static String getUrl(PsiFile file) {
    final VirtualFile virtualFile = file.getOriginalFile().getVirtualFile();
    if (virtualFile == null) {
      return null;
    }
    return virtualFile.getUrl();
  }
}
