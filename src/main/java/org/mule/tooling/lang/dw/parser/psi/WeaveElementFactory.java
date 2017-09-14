package org.mule.tooling.lang.dw.parser.psi;


import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFileFactory;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveKeyValuePair;

public class WeaveElementFactory {

  public static WeaveIdentifier createIdentifier(Project project, String name) {
    WeaveBody body = createFile(project, name).getBody();
    return ((WeaveVariableReferenceExpression) body.getExpression()).getFqnIdentifier().getIdentifier();
  }


  public static WeaveDocument createFile(Project project, String text) {
    String name = "dummy.wev";
    return (WeaveDocument) PsiFileFactory.getInstance(project).
            createFileFromText(name, WeaveFileType.getInstance(), text).getChildren()[0];
  }
}
