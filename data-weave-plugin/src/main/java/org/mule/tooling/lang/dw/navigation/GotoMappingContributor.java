package org.mule.tooling.lang.dw.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.codeStyle.MinusculeMatcher;
import com.intellij.psi.codeStyle.NameUtil;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier$;

import java.util.Collection;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class GotoMappingContributor implements GotoClassContributor {
    @Nullable
    @Override
    public String getQualifiedName(NavigationItem navigationItem) {
        if (navigationItem instanceof WeaveDocument) {
            return ((WeaveDocument) navigationItem).getQualifiedName();
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedNameSeparator() {
        return NameIdentifier$.MODULE$.SEPARATOR();
    }

    @NotNull
    @Override
    public String[] getNames(Project project, boolean includeNonProjectItems) {
        final Collection<VirtualFile> allDWFiles = getAllWeaveFiles(project, includeNonProjectItems);
        return allDWFiles
                .stream()
                .map((vf) -> {
                    PsiFile file = PsiManager.getInstance(project).findFile(vf);
                    if (file != null) {
                        WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(file);
                        if (weaveDocument != null) {
                            return weaveDocument.getName();
                        } else {
                            return null;
                        }
                    } else {
                        System.out.println("Virtual file was nof found for " + vf);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(String[]::new);
    }

    @NotNull
    public Collection<VirtualFile> getAllWeaveFiles(Project project, boolean includeNonProjectItems) {
        GlobalSearchScope searchScope = includeNonProjectItems ? GlobalSearchScope.everythingScope(project) : GlobalSearchScope.projectScope(project);
        return FilenameIndex.getAllFilesByExt(project, WeaveFileType.WeaveFileExtension, searchScope);
    }

    @NotNull
    @Override
    public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems) {
        return getAllWeaveFiles(project, includeNonProjectItems)
                .stream()
                .map((vf) -> {
                    PsiFile file = PsiManager.getInstance(project).findFile(vf);
                    if (file != null) {
                        return WeavePsiUtils.getWeaveDocument(file);
                    } else {
                        System.out.println("file not found " + vf.getPath());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter((vf) -> {
                    final MinusculeMatcher matcher = NameUtil.buildMatcher("*" + StringUtil.replace(pattern, ".", ".*")).build();
                    return matcher.matches(requireNonNull(vf.getName()));
                })
                .toArray(WeaveDocument[]::new);
    }
}
