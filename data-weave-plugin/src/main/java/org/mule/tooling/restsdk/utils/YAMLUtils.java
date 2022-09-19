package org.mule.tooling.restsdk.utils;

import com.intellij.psi.PsiElement;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLMapping;
import org.jetbrains.yaml.psi.YAMLValue;

import java.util.Collection;

/** YAML related tools.
 */
public class YAMLUtils {
    private YAMLUtils() { }

    /** Merges a YAML value into an existing {@link YAMLKeyValue}.
     *
     * @param target the target KeyValue
     * @param what the YAML value to add
     * @return what has been added
     */
    public static PsiElement mergeInto(YAMLKeyValue target, YAMLValue what) {
        var targetValue = target.getValue();
        if (!(what instanceof YAMLMapping) || !(targetValue instanceof YAMLMapping))  {
            target.setValue(what);
            return target.getValue();
        }

        YAMLMapping targetMapping = (YAMLMapping) targetValue;
        YAMLMapping sourceMapping = (YAMLMapping) what;
        PsiElement added = null;
        Collection<YAMLKeyValue> sourceKeyValues = sourceMapping.getKeyValues();
        for (YAMLKeyValue kv : sourceKeyValues) {
            var k = kv.getKeyText();
            YAMLKeyValue targetKv = targetMapping.getKeyValueByKey(k);
            if (targetKv == null) {
                targetMapping.putKeyValue(kv);
                added = targetMapping.getKeyValueByKey(k);
            } else {
                added = mergeInto(targetKv, kv.getValue());
            }
        }
        return sourceKeyValues.size() > 1 ? target : added;
    }
}
