package org.mule.tooling.lang.dw.parser.psi;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.psi.util.PsiUtil;
import com.intellij.xdebugger.XDebuggerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;


public class WeavePsiUtils {

    public static List<IElementType> KeyWordsToken = Arrays.asList(WeaveTypes.IF,
            WeaveTypes.CASE_KEYWORD,
            WeaveTypes.IS,
            WeaveTypes.AS,
            WeaveTypes.UNLESS,
            WeaveTypes.ELSE,
            WeaveTypes.USING,
            WeaveTypes.DEFAULT,
            WeaveTypes.MATCHES_KEYWORD,
            WeaveTypes.MATCH_KEYWORD,
            WeaveTypes.DO_KEYWORD
    );

    public static List<IElementType> DirectivesToken =
            Arrays.asList(WeaveTypes.INPUT_DIRECTIVE_KEYWORD, WeaveTypes.OUTPUT_DIRECTIVE_KEYWORD, WeaveTypes.NAMESPACE_DIRECTIVE_KEYWORD, WeaveTypes.TYPE_DIRECTIVE_KEYWORD
                    , WeaveTypes.VAR_DIRECTIVE_KEYWORD, WeaveTypes.VERSION_DIRECTIVE_KEYWORD, WeaveTypes.FUNCTION_DIRECTIVE_KEYWORD, WeaveTypes.IMPORT_DIRECTIVE_KEYWORD);

    @NotNull
    public static String stripQuotes(@NotNull String text) {
        if (text.length() > 0) {
            final char firstChar = text.charAt(0);
            final char lastChar = text.charAt(text.length() - 1);
            if (firstChar == '\'' || firstChar == '"') {
                if (text.length() > 1 && firstChar == lastChar && !isEscapedChar(text, text.length() - 1)) {
                    return text.substring(1, text.length() - 1);
                }
                return text.substring(1);
            }
        }
        return text;
    }


    @Nullable
    public static PsiElement getFirstWeaveElement(Project project, Document document, int line) {
        final Ref<PsiElement> result = Ref.create();
        XDebuggerUtil.getInstance().iterateLine(project, document, line, element -> {
            if (!(element instanceof PsiWhiteSpace)) {
                result.set(element);
                return false;
            }
            return true;
        });
        return result.get();
    }

    public static boolean isEscapedChar(@NotNull String text, int position) {
        int count = 0;
        for (int i = position - 1; i >= 0 && text.charAt(i) == '\\'; i--) {
            count++;
        }
        return count % 2 != 0;
    }

    @Nullable
    public static WeaveDocument getDocument(@NotNull PsiElement element) {
        PsiElement result = element;
        while (!(result instanceof WeaveDocument) && result != null) {
            result = result.getParent();
        }
        return (WeaveDocument) result;
    }

    public static List<String> getAllLocalNames(WeaveDoExpression document) {
        if (document == null) {
            return Collections.emptyList();
        } else {
            return collectVariables(document.getDirectiveList());
        }
    }

    public static List<String> getAllGlobalNames(WeaveDocument document) {
        WeaveHeader header = document.getHeader();
        if (header != null) {
            final List<WeaveDirective> directiveList = header.getDirectiveList();
            return collectVariables(directiveList);
        } else {
            return Collections.emptyList();
        }
    }

    public static List<String> collectVariables(List<WeaveDirective> directiveList) {
        return directiveList.stream()
                .map((directive) -> {
                    if (directive instanceof WeaveFunctionDirective) {
                        return ((WeaveFunctionDirective) directive).getFunctionDefinition().getName();
                    } else if (directive instanceof WeaveVariableDirective) {
                        return ((WeaveVariableDirective) directive).getVariableDefinition().getName();
                    } else if (directive instanceof WeaveTypeDirective) {
                        return ((WeaveTypeDirective) directive).getTypeDefinition().getName();
                    } else if (directive instanceof WeaveNamespaceDirective) {
                        return ((WeaveNamespaceDirective) directive).getNamespaceDefinition().getName();
                    } else {
                        return null;
                    }
                })
                .filter(Objects::nonNull).collect(Collectors.toList());
    }

    public static List<WeaveNamedElement> findFunctions(@NotNull PsiElement element) {
        final List<WeaveNamedElement> result = new ArrayList<>();
        PsiElement parent = element.getParent();
        while (isNotWeaveFile(parent)) {
            if (parent instanceof WeaveDocument) {
                final Collection<WeaveVariableDefinition> vars = PsiTreeUtil.findChildrenOfType(((WeaveDocument) parent).getHeader(), WeaveVariableDefinition.class);
                final Collection<WeaveFunctionDefinition> functionDirectives = PsiTreeUtil.findChildrenOfType(((WeaveDocument) parent).getHeader(), WeaveFunctionDefinition.class);
                result.addAll(functionDirectives);
                result.addAll(vars.stream().filter(var -> var.getExpression() instanceof WeaveLambdaLiteral).collect(Collectors.toList()));
                break;
            }
            parent = parent.getParent();
        }
        return result;
    }

    public static List<WeaveNamedElement> findTypes(@NotNull PsiElement element) {
        final List<WeaveNamedElement> result = new ArrayList<>();
        PsiElement parent = element.getParent();
        while (isNotWeaveFile(parent)) {
            if (parent instanceof WeaveDocument) {
                final Collection<WeaveTypeDefinition> functionDirectives = PsiTreeUtil.findChildrenOfType(((WeaveDocument) parent).getHeader(), WeaveTypeDefinition.class);
                result.addAll(functionDirectives);
                break;
            }
            parent = parent.getParent();
        }
        return result;
    }

    public static Optional<? extends PsiElement> findType(PsiElement element, final String functionName) {
        final List<WeaveNamedElement> types = findTypes(element);
        return types.stream().filter(weaveVariableDefinition -> functionName.equals(weaveVariableDefinition.getName())).findFirst();
    }

    public static Optional<? extends PsiElement> findFunction(PsiElement element, final String functionName) {
        final List<WeaveNamedElement> variables = findFunctions(element);
        return variables.stream().filter(weaveVariableDefinition -> functionName.equals(weaveVariableDefinition.getName())).findFirst();
    }

    public static boolean isArrayItem(PsiElement element) {
        return element.getParent() instanceof WeaveArrayExpression;
    }


    private static boolean isNotWeaveFile(PsiElement parent) {
        return parent != null && !(parent instanceof WeaveFile);
    }

    @Nullable
    public static PsiElement resolveReference(WeaveDocument document, String searchElement) {
        WeaveHeader header = document.getHeader();
        if (header != null) {
            List<WeaveDirective> directiveList = header.getDirectiveList();
            for (WeaveDirective weaveDirective : directiveList) {
                if (weaveDirective instanceof WeaveFunctionDirective) {
                    WeaveFunctionDefinition functionDefinition = ((WeaveFunctionDirective) weaveDirective).getFunctionDefinition();
                    if (functionDefinition != null && searchElement.equals(functionDefinition.getName())) {
                        return functionDefinition;
                    }
                } else if (weaveDirective instanceof WeaveVariableDirective) {
                    WeaveVariableDefinition functionDefinition = ((WeaveVariableDirective) weaveDirective).getVariableDefinition();
                    if (functionDefinition != null && searchElement.equals(functionDefinition.getName())) {
                        return functionDefinition;
                    }
                } else if (weaveDirective instanceof WeaveTypeDirective) {
                    WeaveTypeDefinition functionDefinition = ((WeaveTypeDirective) weaveDirective).getTypeDefinition();
                    if (functionDefinition != null && searchElement.equals(functionDefinition.getName())) {
                        return functionDefinition;
                    }
                } else if (weaveDirective instanceof WeaveNamespaceDirective) {
                    WeaveNamespaceDefinition functionDefinition = ((WeaveNamespaceDirective) weaveDirective).getNamespaceDefinition();
                    if (functionDefinition != null && searchElement.equals(functionDefinition.getName())) {
                        return functionDefinition;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    public static WeaveDocument getWeaveDocument(PsiFile psiFile) {
        PsiElement[] children = psiFile.getChildren();
        if (children.length > 0) {
            for (PsiElement child : children) {
                if (child instanceof WeaveDocument) {
                    return (WeaveDocument) child;
                }
            }
        }
        return null;
    }


    public static PsiElement getParent(@Nullable PsiElement element, Predicate<PsiElement> filter) {

        if (element == null) {
            return null;
        }
        PsiElement current = element.getParent();
        while (current != null) {
            if (filter.test(current)) {
                return current;
            }
            if (current instanceof PsiFile) {
                return null;
            }
            current = current.getParent();
        }
        return null;
    }

    /**
     * Return the most inner element at that range. If the selected element is a whitespace it return null
     *
     * @param file        the file
     * @param startOffset The start
     * @param endOffset   the end
     * @return The most inner element that is at the start and the end offset is less or equals
     */
    @Nullable
    public static PsiElement findInnerElementRange(@NotNull PsiFile file,
                                                   int startOffset,
                                                   int endOffset) {
        PsiElement elementAtOffset = PsiUtil.getElementAtOffset(file, startOffset);
        while (elementAtOffset.getTextRange().getStartOffset() == startOffset && elementAtOffset.getTextRange().getEndOffset() < endOffset) {
            elementAtOffset = elementAtOffset.getParent();
        }
        if (elementAtOffset instanceof PsiFile) {
            return getWeaveDocument(file);
        }
        if (elementAtOffset instanceof LeafPsiElement) {
            return elementAtOffset.getParent();
        }
        if (elementAtOffset instanceof PsiWhiteSpace) {
            return null;
        }
        return elementAtOffset;
    }

    @Nullable
    public static PsiElement findUpperElementRange(@NotNull PsiFile file,
                                                   int startOffset,
                                                   int endOffset) {
        PsiElement elementAtOffset = PsiUtil.getElementAtOffset(file, startOffset);
        while (!(elementAtOffset.getParent() instanceof PsiFile) && elementAtOffset.getParent() != null && isParentBetweenRange(startOffset, endOffset, elementAtOffset)) {
            elementAtOffset = elementAtOffset.getParent();
        }
        if (elementAtOffset instanceof PsiFile) {
            return getWeaveDocument(file);
        }
        return elementAtOffset;
    }

    private static boolean isParentBetweenRange(int startOffset, int endOffset, PsiElement elementAtOffset) {
        TextRange textRange = elementAtOffset.getParent().getTextRange();
        return textRange.getStartOffset() == startOffset && textRange.getEndOffset() <= endOffset;
    }

}
