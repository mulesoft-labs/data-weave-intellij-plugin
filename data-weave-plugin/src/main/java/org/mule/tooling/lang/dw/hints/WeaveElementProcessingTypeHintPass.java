package org.mule.tooling.lang.dw.hints;

import com.intellij.codeInsight.daemon.impl.HintRenderer;
import com.intellij.codeInsight.hints.ElementProcessingHintPass;
import com.intellij.codeInsight.hints.ModificationStampHolder;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveFunctionDefinition;
import org.mule.tooling.lang.dw.parser.psi.WeaveTypes;
import org.mule.tooling.lang.dw.parser.psi.WeaveVariableDefinition;
import org.mule.tooling.lang.dw.service.WeaveToolingService;
import org.mule.weave.v2.parser.phase.listener.ParsingNotificationManager;
import org.mule.weave.v2.ts.*;
import scala.collection.Iterator;
import scala.collection.Seq;

public class WeaveElementProcessingTypeHintPass extends ElementProcessingHintPass {

    public static Key<Boolean> WEAVE_TYPE_INLAY_KEY = Key.create("WEAVE_TYPE_INLAY_KEY");

    public WeaveElementProcessingTypeHintPass(@NotNull PsiElement rootElement, @NotNull Editor editor, @NotNull ModificationStampHolder modificationStampHolder) {
        super(rootElement, editor, modificationStampHolder);
    }

    @Override
    public void collectElementHints(@NotNull PsiElement psiElement, @NotNull Function2<? super Integer, ? super String, Unit> collector) {
        PsiElement parent = psiElement.getParent();
        if (psiElement instanceof WeaveVariableDefinition) {
            WeaveToolingService instance = WeaveToolingService.getInstance(parent.getProject());
            WeaveVariableDefinition variableDefinition = (WeaveVariableDefinition) psiElement;
            if (variableDefinition.getType() == null) {
                WeaveType weaveType = instance.typeOf(variableDefinition.getExpression());
                if (weaveType != null ) {
                    final WeaveType baseWeaveType = weaveType.baseType();
                    if(isSimpleType(baseWeaveType) && variableDefinition.getNameIdentifier() != null) {
                        collector.invoke(variableDefinition.getNameIdentifier().getTextRange().getEndOffset(), ": " + baseWeaveType.baseType().toString(false, true));
                    }
                }
            }
        } else if (psiElement instanceof WeaveFunctionDefinition) {
            WeaveToolingService instance = WeaveToolingService.getInstance(parent.getProject());
            WeaveFunctionDefinition functionDefinition = (WeaveFunctionDefinition) psiElement;
            if (functionDefinition.getType() == null) {
                WeaveType weaveType = instance.typeOf(functionDefinition.getExpression());
                if (weaveType != null) {
                    final WeaveType baseWeaveType = weaveType.baseType();
                    if(isSimpleType(baseWeaveType) && functionDefinition.getExpression() != null) {
                        ASTNode[] children = functionDefinition.getNode().getChildren(TokenSet.create(WeaveTypes.R_PARREN));
                        if (children.length > 0) {
                            collector.invoke(children[0].getTextRange().getEndOffset(), ": " + baseWeaveType.toString(false, true));
                        }
                    }

                }
            }
        }
    }

    //Determines if a type is simple enough to be shown
    //Primitive types, named types simple arrays, union with less than 3 simple types
    private boolean isSimpleType(WeaveType weaveType) {
        if (weaveType.label().isDefined()) {
            return true;
        } else if (weaveType instanceof ArrayType) {
            return isSimpleType(((ArrayType) weaveType).of());
        } else if (weaveType instanceof UnionType) {
            final Seq<WeaveType> of = ((UnionType) weaveType).of();
            if (of.size() <= 3) {
                final Iterator<WeaveType> iterator = of.iterator();
                while (iterator.hasNext()) {
                    final WeaveType next = iterator.next();
                    if (!isSimpleType(next)) {
                        return false;
                    }
                }
                return true;
            } else {
                return false;
            }
        } else if (weaveType instanceof ObjectType) {
            final ObjectType objectType = (ObjectType) weaveType;
            return objectType.properties().isEmpty();
        } else {
            return new TypeHelper(new ParsingNotificationManager()).isPrimitiveType(weaveType);
        }
    }

    @NotNull
    @Override
    public HintRenderer createRenderer(@NotNull String text) {
        return new HintRenderer(text);
    }

    @NotNull
    @Override
    public Key<Boolean> getHintKey() {
        return WEAVE_TYPE_INLAY_KEY;
    }

    @Override
    public boolean isAvailable(@NotNull VirtualFile virtualFile) {
        return virtualFile.getFileType() == WeaveFileType.getInstance();
    }
}
