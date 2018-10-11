package org.mule.tooling.lang.dw.navigation;

import com.intellij.navigation.GotoClassContributor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileSystemItem;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Processor;
import com.intellij.util.Processors;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveFileType;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.weave.v2.parser.ast.variables.NameIdentifier$;

import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Stream;

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
    String[] allFilenames = FilenameIndex.getAllFilenames(project);
    return Stream.of(allFilenames).filter((name) -> name.endsWith("." + WeaveFileType.WeaveFileExtension))
        .toArray(String[]::new);
  }

  @NotNull
  private GlobalSearchScope createSearchScope(Project project, boolean includeNonProjectItems) {
    return includeNonProjectItems ? GlobalSearchScope.everythingScope(project) : GlobalSearchScope.projectScope(project);
  }

  @NotNull
  private Processor<PsiFileSystemItem> createProcessor(ArrayList<PsiFileSystemItem> result) {
    final Processor<PsiFileSystemItem> processor = Processors.cancelableCollectProcessor(result);
    return item -> {
      if (WeaveFileType.WeaveFileExtension.equals(item.getVirtualFile().getExtension())) {
        return processor.process(item);
      } else {
        return false;
      }
    };
  }


  @NotNull
  @Override
  public NavigationItem[] getItemsByName(String name, final String pattern, Project project, boolean includeNonProjectItems) {
    final GlobalSearchScope searchScope = createSearchScope(project, includeNonProjectItems);
    final ArrayList<PsiFileSystemItem> result = new ArrayList<>();
    final Processor<PsiFileSystemItem> wrapper = createProcessor(result);
    FilenameIndex.processFilesByName(
        name, false, wrapper, searchScope, project, null
    );
    return result.stream()
        .map((vf) -> {
          VirtualFile virtualFile = vf.getVirtualFile();
          PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
          if (file != null) {
            return WeavePsiUtils.getWeaveDocument(file);
          } else {
            System.out.println("file not found " + virtualFile.getPath());
            return null;
          }
        })
        .filter(Objects::nonNull)
        .toArray(NavigationItem[]::new);
  }
}
