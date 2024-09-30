package org.mule.tooling.als.utils;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.util.text.LineColumn;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.commons.AnypointNotification;
import org.mulesoft.lsp.feature.common.Position;
import org.mulesoft.lsp.feature.common.TextDocumentIdentifier;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.io.File;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LSPUtils {

  @Nullable
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

  public static <T> Optional<T> resultOf(Future<T> objectFuture) {
    try {
      return Optional.of(Await.result(objectFuture, Duration.apply(30, TimeUnit.SECONDS)));
    } catch (InterruptedException | TimeoutException e) {
      Notifications.Bus.notify(new Notification(AnypointNotification.ANYPOINT_NOTIFICATION, "Error while executing future", "Unable to execute ALS Future. Reason: \n" + e.getMessage(), NotificationType.ERROR));
      return Optional.empty();
    }
  }

  public static String toLSPUrl(File file) {
    return "file://" + file.toURI().getPath();
  }

  public static String toLSPUrl(String filePath) {
    return toLSPUrl(new File(filePath));
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
