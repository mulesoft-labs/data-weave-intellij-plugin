package org.mule.tooling.lang.dw.util;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeaveFqnIdentifier;
import org.mule.tooling.lang.dw.parser.psi.WeaveHeader;
import org.mule.tooling.lang.dw.parser.psi.WeaveImportDirective;


public class WeaveUtils {

    public static boolean isTestFile(@Nullable WeaveDocument document) {
        if (document == null)
            return false;
        WeaveHeader header = document.getHeader();
        if (header != null) {
            return header.getDirectiveList().stream().anyMatch((directive) -> {
                if (directive instanceof WeaveImportDirective) {
                    @Nullable WeaveFqnIdentifier moduleReference = ((WeaveImportDirective) directive).getFqnIdentifier();
                    if (moduleReference != null) {
                        String moduleFQN = moduleReference.getModuleFQN();
                        return moduleFQN.equals("dw::test::Tests");
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            });
        }
        return false;
    }

    @Nullable
    public static VirtualFile getDWITFolder(@Nullable Module module) {
        if (module == null) {
            return null;
        }

        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        VirtualFile moduleRoot = rootManager.getContentRoots()[0].findChild("src");
        if (moduleRoot == null) {
            return null;
        }
        //Create it here
        return moduleRoot.findFileByRelativePath(WeaveConstants.TEST_BASE_FOLDER_NAME + "/" + WeaveConstants.INTEGRATION_TEST_FOLDER_NAME);
    }

    @Nullable
    public static VirtualFile getDWMITFolder(@Nullable Module module) {
        if (module == null) {
            return null;
        }

        final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
        VirtualFile moduleRoot = rootManager.getContentRoots()[0].findChild("src");
        if (moduleRoot == null) {
            return null;
        }
        //Create it here
        return moduleRoot.findFileByRelativePath(WeaveConstants.TEST_BASE_FOLDER_NAME + "/" + WeaveConstants.MODULE_INTEGRATION_TEST_FOLDER_NAME);
    }

}
