package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.editor.LogicalPosition;
import org.eclipse.lsp4j.Position;

public class LSPUtils {
    public static Position logicalToLSPPos(LogicalPosition position) {
        return new Position(position.line, position.column);
    }
}
