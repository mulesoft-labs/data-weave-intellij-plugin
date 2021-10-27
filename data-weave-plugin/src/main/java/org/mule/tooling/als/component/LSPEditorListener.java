package org.mule.tooling.als.component;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorKind;
import com.intellij.openapi.editor.event.EditorFactoryEvent;
import com.intellij.openapi.editor.event.EditorFactoryListener;
import org.jetbrains.annotations.NotNull;

public class LSPEditorListener implements EditorFactoryListener {

  private ALSLanguageService languageService;

  public LSPEditorListener(ALSLanguageService languageService) {
    this.languageService = languageService;
  }

  public void editorReleased(@NotNull EditorFactoryEvent editorFactoryEvent) {
    Editor editor = editorFactoryEvent.getEditor();
    if (editor.getEditorKind() == EditorKind.MAIN_EDITOR) {
      languageService.closeEditor(editor);
    }
  }

  public void editorCreated(@NotNull EditorFactoryEvent editorFactoryEvent) {
    Editor editor = editorFactoryEvent.getEditor();
    if (editor.getEditorKind() == EditorKind.MAIN_EDITOR) {
      languageService.openEditor(editor);
    }
  }
}