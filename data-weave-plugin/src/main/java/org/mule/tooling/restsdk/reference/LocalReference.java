package org.mule.tooling.restsdk.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLValue;

public class LocalReference extends PsiReferenceBase<PsiElement> {


    private final String rootElement;

    public LocalReference(@NotNull final PsiElement element, final String rootElement) {
        super(element);
        this.rootElement = rootElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        YAMLDocument parentOfType = PsiTreeUtil.getParentOfType(myElement, YAMLDocument.class);
        YAMLMapping rootMapping = PsiTreeUtil.getChildOfType(parentOfType, YAMLMapping.class);
        if (rootMapping != null) {
            YAMLKeyValue sampleData = rootMapping.getKeyValueByKey(rootElement);
            if (sampleData != null) {
                YAMLValue sampleDataValue = sampleData.getValue();
                if (sampleDataValue instanceof YAMLMapping) {
                    YAMLKeyValue keyValueByKey = ((YAMLMapping) sampleDataValue).getKeyValueByKey(myElement.getText());
                    if (keyValueByKey != null) {
                        return keyValueByKey.getKey();
                    }
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        YAMLDocument parentOfType = PsiTreeUtil.getParentOfType(myElement, YAMLDocument.class);
        YAMLMapping rootMapping = PsiTreeUtil.getChildOfType(parentOfType, YAMLMapping.class);
        if (rootMapping != null) {
            YAMLKeyValue sampleData = rootMapping.getKeyValueByKey(rootElement);
            if (sampleData != null) {
                YAMLValue sampleDataValue = sampleData.getValue();
                if (sampleDataValue instanceof YAMLMapping) {
                    return ((YAMLMapping) sampleDataValue)
                            .getKeyValues()
                            .stream()
                            .map((kv) -> kv.getKey().getText())
                            .toArray();
                }
            }
        }
        return new Object[0];
    }


}
