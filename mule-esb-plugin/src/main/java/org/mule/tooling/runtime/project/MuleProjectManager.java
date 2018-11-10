package org.mule.tooling.runtime.project;

import com.intellij.facet.FacetManager;
import com.intellij.facet.FacetTypeRegistry;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileTypes.StdFileTypes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;
import org.mule.tooling.runtime.framework.facet.MuleFacet;
import org.mule.tooling.runtime.framework.facet.MuleFacetType;
import org.mule.tooling.runtime.util.MuleConfigUtils;
import org.mule.tooling.runtime.util.MuleDirectoriesUtils;
import org.mule.tooling.runtime.util.MuleModuleUtils;

import java.io.IOException;
import java.nio.file.Paths;

public class MuleProjectManager extends AbstractProjectComponent {

    static final Logger logger = Logger.getInstance(MuleProjectManager.class);

    protected MuleProjectManager(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        VirtualFileManager manager = VirtualFileManager.getInstance();
        manager.addVirtualFileListener(new VirtualFileAdapter() {
            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                try {
                    if (MuleProjectManager.this.myProject != null && MuleProjectManager.this.myProject.isInitialized() && MuleProjectManager.this.myProject.isOpen()) {
                        ProjectRootManager mgr = ProjectRootManager.getInstance(MuleProjectManager.this.myProject);
                        if (mgr != null) {
                            ProjectFileIndex projectIndex = mgr.getFileIndex();
                            Module module = projectIndex.getModuleForFile(event.getFile());
                            if (module != null) { // File belongs to this module

                                String fileAbsolutePath = event.getFile().getPath();

                                String moduleContentRoot = projectIndex.getContentRootForFile(event.getFile()).getCanonicalPath();
                                String appPath = Paths.get(moduleContentRoot, MuleDirectoriesUtils.SRC_MAIN_MULE).toString();

                                PsiFile psiFile = PsiManager.getInstance(MuleProjectManager.this.myProject).findFile(event.getFile());
                                if (psiFile != null && fileAbsolutePath.startsWith(appPath)) { //The file was created in src/main/mule
                                    if (psiFile.getFileType() == StdFileTypes.XML) { //This is config file
                                        String pathRelative = MuleDirectoriesUtils.getRelativePath(fileAbsolutePath, appPath);
                                        MuleFacet facet = getMuleFacetForModule(module);
                                        addConfigFile(facet, pathRelative);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error(e);
                }
            }

            @Override
            public void beforeFileDeletion(@NotNull VirtualFileEvent event) {
                if (MuleProjectManager.this.myProject != null && MuleProjectManager.this.myProject.isInitialized() && MuleProjectManager.this.myProject.isOpen()) {
                    ProjectRootManager mgr = ProjectRootManager.getInstance(MuleProjectManager.this.myProject);
                    if (mgr != null) {

                        ProjectFileIndex projectIndex = mgr.getFileIndex();
                        Module module = projectIndex.getModuleForFile(event.getFile());

                        if (module != null) { // File belongs to this module
                            String fileAbsolutePath = event.getFile().getPath();

                            String moduleContentRoot = projectIndex.getContentRootForFile(event.getFile()).getCanonicalPath();
                            String appPath = Paths.get(moduleContentRoot, MuleDirectoriesUtils.SRC_MAIN_MULE).toString();
                            PsiFile psiFile = PsiManager.getInstance(MuleProjectManager.this.myProject).findFile(event.getFile());
                            if (fileAbsolutePath.startsWith(appPath)) { //The file was deleted in src/main/mule
                                //if (MuleConfigUtils.isMuleFile(psiFile)) { //This is config file
                                    String pathRelative = MuleDirectoriesUtils.getRelativePath(fileAbsolutePath, appPath);
                                    MuleFacet facet = getMuleFacetForModule(module);
                                    deleteConfigFile(facet, pathRelative);
                                //} else if (event.getFile().isDirectory()) {
                            /* TODO
                            In `VirtualFileAdapter.beforeFileDeletion` the virtual directory still exists and you may invoke `getChildren()` on it.
                            */
                                //}
                            }
                        }
                    }
                }
            }

            @Override
            public void beforeFileMovement(@NotNull VirtualFileMoveEvent event) {

                if (MuleProjectManager.this.myProject != null && MuleProjectManager.this.myProject.isInitialized() && MuleProjectManager.this.myProject.isOpen()) {
                    ProjectRootManager mgr = ProjectRootManager.getInstance(MuleProjectManager.this.myProject);
                    if (mgr != null) {

                        ProjectFileIndex projectIndex = mgr.getFileIndex();
                        Module module = projectIndex.getModuleForFile(event.getFile());

                        if (module != null) { // File belongs to this module

                            String fileName = event.getFileName();

                            String oldAbsolutePath = Paths.get(event.getOldParent().getPath(), fileName).toString();
                            String newAbsolutePath = Paths.get(event.getNewParent().getPath(), fileName).toString();

                            String moduleContentRoot = projectIndex.getContentRootForFile(event.getFile()).getCanonicalPath();
                            String appPath = Paths.get(moduleContentRoot, MuleDirectoriesUtils.SRC_MAIN_MULE).toString();

                            PsiFile psiFile = PsiManager.getInstance(MuleProjectManager.this.myProject).findFile(event.getFile());
                            if (MuleConfigUtils.isMuleFile(psiFile)) { //This is config file

                                MuleFacet facet = getMuleFacetForModule(module);

                                if (oldAbsolutePath.startsWith(appPath)) { //The file was in src/main/mule, remove
                                    String pathRelative = MuleDirectoriesUtils.getRelativePath(oldAbsolutePath, appPath);
                                    deleteConfigFile(facet, pathRelative);
                                }
                                if (newAbsolutePath.startsWith(appPath)) { //The file now is in src/main/mule, add
                                    String pathRelative = MuleDirectoriesUtils.getRelativePath(newAbsolutePath, appPath);
                                    addConfigFile(facet, pathRelative);
                                }
                            }
                        }
                    }
                }
            }
            //=======================================



        });
    }

    @Nullable
    private MuleFacet getMuleFacetForModule(Module m) {
        FacetManager facetManager = FacetManager.getInstance(m);
        MuleFacetType type = (MuleFacetType) FacetTypeRegistry.getInstance().findFacetType(MuleFacet.ID);
        MuleFacet muleFacet = facetManager.getFacetByType(type.getId());
        return muleFacet;
    }

    private void addConfigFile(MuleFacet muleFacet, String configPath) {
        JSONObject muleArtifact = muleFacet.getConfiguration().getMuleArtifact();
        JSONArray configs = null;
        if (muleArtifact.has("configs"))
            configs = muleArtifact.getJSONArray("configs");
        else {
            configs = new JSONArray();
        }
        configs.put(configPath);
        muleArtifact.put("configs", configs);

        VirtualFile artifactFile = MuleModuleUtils.getMuleArtifactJson(muleFacet.getModule());
        try {
            VfsUtil.saveText(artifactFile, muleArtifact.toString(2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteConfigFile(MuleFacet muleFacet, String configPath) {
        JSONObject muleArtifact = muleFacet.getConfiguration().getMuleArtifact();

        if (muleArtifact.has("configs")) {
            JSONArray configs = muleArtifact.getJSONArray("configs");

            JSONArray newConfigs = new JSONArray();

            for (int i = 0; i < configs.length(); i++) {
                if (!configPath.equalsIgnoreCase(configs.getString(i))) {
                    newConfigs.put(configs.getString(i));
                }
            }

            muleArtifact.put("configs", newConfigs);
            VirtualFile artifactFile = MuleModuleUtils.getMuleArtifactJson(muleFacet.getModule());
            try {
                VfsUtil.saveText(artifactFile, muleArtifact.toString(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
