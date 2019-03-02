package org.mule.tooling.runtime.launcher.configuration.archive;


import com.intellij.execution.ExecutionException;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.io.File;

public class MuleAppMavenHandler implements MuleAppHandler {

    @NotNull
    @Override
    public File getMuleApp(final Module module) throws ExecutionException {
        String outputPath = null;
        final File outputDir;

        final CompilerModuleExtension compilerModuleExtension = CompilerModuleExtension.getInstance(module);
        final VirtualFile moduleFile = module.getModuleFile();
        VirtualFile compilerOutputPath = null;

        //First try by checking the compiler settings
        if (compilerModuleExtension != null) {
            compilerOutputPath = compilerModuleExtension.getCompilerOutputPath();
        }

        if (compilerOutputPath != null && compilerOutputPath.getParent() != null) {
            outputPath = compilerOutputPath.getParent().getCanonicalPath();
            outputDir = new File(outputPath);
        } else if (moduleFile != null) { //Find by relative path to the module root
            //Refresh file system
            module.getModuleFile().refresh(false, true);
            outputPath = module.getModuleFile().getParent().getCanonicalPath() + File.separator + "target";
            outputDir = new File(module.getModuleFile().getParent().getCanonicalPath(), "target");
        } else {
            throw new ExecutionException("Unable to create application. No module found.");
        }

        File applicationZip = null;
        final String suffix = MuleModuleUtils.isMuleDomainModule(module) ? MuleAppHandler.MULE_DOMAIN_SUFFIX : MuleAppHandler.MULE_APP_SUFFIX;
        final File[] zips = outputDir.listFiles((dir, name) -> name.endsWith(suffix));
        if (zips.length > 0) {
            applicationZip = zips[0];
        }
        if (applicationZip == null) {
            throw new ExecutionException("Unable to create application. Application was not found at " + outputPath);
        }
        return applicationZip;
    }
}

