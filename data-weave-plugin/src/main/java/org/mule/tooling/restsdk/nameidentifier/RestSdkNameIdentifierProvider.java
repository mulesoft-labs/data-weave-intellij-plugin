package org.mule.tooling.restsdk.nameidentifier;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.mule.tooling.lang.dw.util.NameIdentifierProvider;
import org.mule.tooling.restsdk.utils.RestSdkHelper;
import org.mule.tooling.restsdk.utils.YamlPath;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier;
import scala.Option;

public class RestSdkNameIdentifierProvider implements NameIdentifierProvider {
    @Override
    public boolean support(PsiFile file) {
        return RestSdkHelper.isInRestSdkContextFile(file);
    }

    @Override
    public NameIdentifier resolveNameIdentifier(PsiFile file) {
        PsiElement context = file.getContext();
        YamlPath yamlPath = YamlPath.pathOf(context);
        assert context != null;
        return toNameIdentifier(yamlPath, context.getContainingFile());
    }

    private NameIdentifier toNameIdentifier(YamlPath yamlPath, PsiFile file) {
        if (yamlPath.getParent() == null || yamlPath.getKind() == YamlPath.Kind.DOCUMENT) {
            return new NameIdentifier(file.getVirtualFile().getNameWithoutExtension(), Option.<String>empty());
        } else {
            return toNameIdentifier(yamlPath.getParent(), file).child(yamlPath.getName());
        }
    }
}
