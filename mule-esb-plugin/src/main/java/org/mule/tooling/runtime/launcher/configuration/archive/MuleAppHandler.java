package org.mule.tooling.runtime.launcher.configuration.archive;


import com.intellij.execution.ExecutionException;
import com.intellij.openapi.module.Module;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface MuleAppHandler {
    public static String MULE_APP_SUFFIX = "-mule-application.jar";

    @NotNull
    File getMuleApp(Module module) throws ExecutionException;
}
