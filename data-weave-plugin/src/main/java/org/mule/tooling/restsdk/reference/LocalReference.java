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

import java.util.Objects;

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
            YAMLKeyValue kv = rootMapping.getKeyValueByKey(rootElement);
            if (kv != null) {
                YAMLValue value = kv.getValue();
                if (value instanceof YAMLMapping) {
                    YAMLKeyValue keyValueByKey = ((YAMLMapping) value).getKeyValueByKey(myElement.getText());
                    if (keyValueByKey != null) {
                        return keyValueByKey.getKey();
                    }
                }
            }
        }
        return null;
    }

    @Override
    public Object @NotNull [] getVariants() {
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
                            .map(YAMLKeyValue::getKey)
                            .filter(Objects::nonNull)
                            .map(PsiElement::getText)
                            .toArray();
                }
            }
        }
        return new Object[0];
    }


}
