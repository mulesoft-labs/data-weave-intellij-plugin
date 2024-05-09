package org.mule.tooling.lang.dw.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilder;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.WeaveParserDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypes;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WeaveFolding implements FoldingBuilder, DumbAware {
    @NotNull
    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull ASTNode node, @NotNull Document document) {
        final List<FoldingDescriptor> descriptors = new ArrayList<FoldingDescriptor>();
        collectDescriptorsRecursively(node, document, descriptors);
        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private static void collectDescriptorsRecursively(@NotNull ASTNode node,
                                                      @NotNull Document document,
                                                      @NotNull List<FoldingDescriptor> descriptors) {
        final IElementType type = node.getElementType();
        if ((isObject(type) || isObjectType(type) || isMultiLineComment(type) || isLineComment(type) || isArray(type) || isDoStatement(type)) && spanMultipleLines(node, document)) {
            descriptors.add(new FoldingDescriptor(node, node.getTextRange()));
        }
        for (ASTNode child : node.getChildren(null)) {
            collectDescriptorsRecursively(child, document, descriptors);
        }
    }

    private static boolean isMultiLineComment(IElementType type) {
        return type == WeaveParserDefinition.MULTILINE_COMMENT;
    }

    private static boolean isLineComment(IElementType type) {
        return type == WeaveTypes.LINE_COMMENT;
    }

    private static boolean isArray(IElementType type) {
        return type == WeaveTypes.ARRAY_EXPRESSION;
    }

    private static boolean isObject(IElementType type) {
        return type == WeaveTypes.OBJECT_EXPRESSION;
    }

    private static boolean isObjectType(IElementType type) {
        return type == WeaveTypes.OBJECT_TYPE;
    }

    private static boolean isDoStatement(IElementType type) {
        return type == WeaveTypes.DO_EXPRESSION;
    }

    @Nullable
    @Override
    public String getPlaceholderText(@NotNull ASTNode node) {
        final IElementType type = node.getElementType();
        if (isObject(type)) {
            return "{...}";
        } else if (isObjectType(type)) {
            return "{...}";
        } else if (isArray((type))) {
            return "[...]";
        } else if (isDoStatement(type)) {
            return "do { ... }";
        } else if (isMultiLineComment(type)) {
            return "/***/";
        } else if (isLineComment(type)) {
            return "//...";
        } else {
            return "...";
        }
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        final IElementType type = node.getElementType();
        return isMultiLineComment(type);
    }

    private static boolean spanMultipleLines(@NotNull ASTNode node, @NotNull Document document) {
        final TextRange range = node.getTextRange();
        return document.getLineNumber(range.getStartOffset()) < document.getLineNumber(range.getEndOffset());
    }
}
