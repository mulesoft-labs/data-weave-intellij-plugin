package org.mule.tooling.lang.dw.parser.psi;


import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiParserFacade;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.refactor.utils.RefactorUtils;
import org.mule.tooling.lang.dw.refactor.utils.WeaveRefactorFunctionData;

import java.util.List;

public class WeaveElementFactory {

    public static WeaveIdentifier createIdentifier(Project project, String name) {
        WeaveBody body = createFile(project, name).getBody();
        return ((WeaveVariableReferenceExpression) body.getExpression()).getFqnIdentifier().getIdentifier();
    }

    public static WeaveVariableReferenceExpression createVariableRef(Project project, String name) {
        WeaveBody body = createFile(project, name).getBody();
        return (WeaveVariableReferenceExpression) body.getExpression();
    }

    public static WeaveReferenceType createTypeReference(Project project, String name) {
        WeaveHeader header = createFile(project, "var x: " + name + " = ???").getHeader();
        WeaveVariableDirective weaveVariableDirective = (WeaveVariableDirective) header.getDirectiveList().get(0);
        return (WeaveReferenceType) weaveVariableDirective.getVariableDefinition().getType();
    }

    public static WeaveVariableDirective createVarDirective(Project project, String name, PsiElement value) {
        final WeaveHeader header = createFile(project, "var " + name + " = " + value.getText()).getHeader();
        assert header != null;
        return (WeaveVariableDirective) header.getDirectiveList().get(0);
    }

    public static WeaveTypeDirective createTypeDirective(Project project, String name, PsiElement value) {
        final WeaveHeader header = createFile(project, "type " + name + " = " + value.getText()).getHeader();
        assert header != null;
        return (WeaveTypeDirective) header.getDirectiveList().get(0);
    }

    public static WeaveHeader createHeader(Project project) {
        WeaveDocument file = createFile(project, "%dw 2.0 --- null");
        return file.getHeader();
    }


    public static WeaveDocument createFile(Project project, String text) {
        String name = "dummy.dwl";
        return (WeaveDocument) PsiFileFactory.getInstance(project).
                createFileFromText(name, WeaveFileType.getInstance(), text).getChildren()[0];
    }

    public static WeaveDoExpression createDoBlock(Project project, PsiElement value) {
        WeaveDocument file = createFile(project, "do { \n" + value.getText() + "\n }");
        return (WeaveDoExpression) file.getBody().getExpression();
    }

    public static PsiElement createBlockSeparator(Project project) {
        WeaveDocument file = createFile(project, "%dw 2.0 --- null");
        return file.getChildren()[1].getPrevSibling().getPrevSibling();
    }

    @NotNull
    public static PsiElement createNewLine(Project project) {
        PsiParserFacade helper = PsiParserFacade.SERVICE.getInstance(project);
        return helper.createWhiteSpaceFromText("\n");
    }

    @NotNull
    public static WeaveFunctionCallExpression createFunctionCall(Project project, String functionName, List<String> argNames) {
        WeaveDocument file = createFile(project, functionName + "(" + StringUtil.join(argNames, ",") + ")");
        return (WeaveFunctionCallExpression) file.getBody().getExpression();
    }

    @NotNull
    public static WeaveFunctionDirective createFunctionDirective(Project project, WeaveRefactorFunctionData functionDefinition, PsiElement body) {
        WeaveDocument file = createFile(project, RefactorUtils.calculateSignature(functionDefinition) + " = " + body.getText());
        return (WeaveFunctionDirective) file.getHeader().getDirectiveList().get(0);
    }
}
