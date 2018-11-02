package org.mule.tooling.runtime.framework;

import com.intellij.openapi.roots.libraries.LibraryPresentationProvider;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.runtime.RuntimeIcons;
import org.mule.tooling.runtime.sdk.MuleSdk;
import org.mule.tooling.runtime.sdk.MuleSdkManagerStore;

import javax.swing.*;
import java.util.List;

public class MuleLibraryPresentationProvider extends LibraryPresentationProvider<MuleLibraryProperties> {

    public MuleLibraryPresentationProvider() {
        super(MuleLibraryKind.MULE_LIBRARY_KIND);
    }

    @Override
    public String getDescription(@NotNull MuleLibraryProperties properties) {
        return String.format(MuleLibraryKind.MULE_LIBRARY_KIND_ID + "-%s", properties.getVersion());
    }

    @Override
    public MuleLibraryProperties detect(@NotNull List<VirtualFile> classesRoots) {
        final VirtualFile[] libraryFiles = VfsUtilCore.toVirtualFileArray(classesRoots);
        for (VirtualFile libraryFile : libraryFiles) {
            final String muleHome = MuleSdk.getMuleHome(libraryFile.getCanonicalFile());
            if(muleHome != null){
                return new MuleLibraryProperties(MuleSdkManagerStore.getInstance().findSdk(muleHome));
            }
        }
        return null;
    }

    @Nullable
    public Icon getIcon(@Nullable MuleLibraryProperties properties) {
        return RuntimeIcons.MuleRunConfigIcon;
    }

}
