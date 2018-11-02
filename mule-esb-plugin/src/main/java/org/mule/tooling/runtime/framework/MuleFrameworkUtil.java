package org.mule.tooling.runtime.framework;

import com.intellij.openapi.module.JavaModuleType;
import com.intellij.openapi.module.ModuleType;

public class MuleFrameworkUtil {

    public static boolean isAcceptableModuleType(ModuleType type) {
        return type instanceof JavaModuleType;
    }
}
