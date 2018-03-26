package org.mule.tooling.lang.dw.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.FilenameIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier$;

import java.util.stream.Collectors;

public class GotoMappingContributor implements GotoClassContributor {
    @Nullable
    @Override
    public String getQualifiedName(NavigationItem navigationItem) {
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return NameIdentifier$.MODULE$.SEPARATOR();
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean b) {
        return FilenameIndex.getAllFilesByExt(project, "dwl")
                .stream()
                .map(VirtualFile::getPresentableName)
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems) {
        return new NavigationItem[0];
    }
}
