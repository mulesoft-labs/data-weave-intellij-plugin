package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.util.concurrency.FutureResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentComponent;
import org.mule.weave.v2.debugger.event.WeaveTypeEntry;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.ts.AnyType;
import org.mule.weave.v2.ts.WeaveType;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils.getWeaveDocument;

public class DataWeaveScenariosManager extends AbstractProjectComponent implements Disposable {

    //TODO we should set this into a settings
    public static final String INTEGRATION_TEST_FOLDER_NAME = "dwit";

    private Map<String, Scenario> selectedScenario = new HashMap<>();
    private Map<Scenario, ImplicitInput> implicitInputTypes = new HashMap<>();
    private Map<Scenario, WeaveType> expectedOutputType = new HashMap<>();

    protected DataWeaveScenariosManager(Project project) {
        super(project);
    }

    public static DataWeaveScenariosManager getInstance(Project myProject) {
        return myProject.getComponent(DataWeaveScenariosManager.class);
    }

    @Override
    public void initComponent() {
        super.initComponent();

        PsiManager.getInstance(myProject).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
            @Override
            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                onModified(event.getFile());
            }
        }, this);


        VirtualFileManager.getInstance().addVirtualFileListener(new VirtualFileListener() {
            @Override
            public void fileCreated(@NotNull VirtualFileEvent event) {
                onFileChanged(event.getFile());
            }

            @Override
            public void fileDeleted(@NotNull VirtualFileEvent event) {
                onFileChanged(event.getFile());
            }

            @Override
            public void fileMoved(@NotNull VirtualFileMoveEvent event) {
                onFileChanged(event.getFile());
            }

            @Override
            public void fileCopied(@NotNull VirtualFileCopyEvent event) {
                onFileChanged(event.getFile());
            }
        });
    }

    private void onModified(PsiFile psiFile) {
        if (psiFile != null) {
            final VirtualFile modifiedFile = psiFile.getVirtualFile();
            onFileChanged(modifiedFile);
        }
    }

    private void onFileChanged(VirtualFile modifiedFile) {
        if (myProject.isDisposed()) {
            return;
        }

        final Module moduleForFile = ModuleUtil.findModuleForFile(modifiedFile, myProject);
        final VirtualFile testFolder = getScenariosTestFolder(moduleForFile);
        if (testFolder != null && VfsUtil.isAncestor(testFolder, modifiedFile, true)) {
            VirtualFile scenario = modifiedFile;
            while (!scenario.getParent().getParent().equals(testFolder)) {
                scenario = scenario.getParent();
            }
            onModified(new Scenario(scenario));
        }
    }

    private void onModified(Scenario scenario) {
        implicitInputTypes.remove(scenario);
        expectedOutputType.remove(scenario);
    }

    public void setCurrentScenario(WeaveDocument weaveDocument, Scenario scenario) {
        this.selectedScenario.put(weaveDocument.getQualifiedName(), scenario);
    }

    @Nullable
    public WeaveType getExpectedOutput(WeaveDocument weaveDocument) {
        Scenario scenario = getCurrentScenarioFor(weaveDocument);
        if (scenario == null) {
            return null;
        } else {
            if (expectedOutputType.containsKey(scenario)) {
                return expectedOutputType.get(scenario);
            }
            VirtualFile expectedOutput = scenario.getExpected();
            if (expectedOutput == null) {
                return null;
            } else {
                final FutureResult<WeaveType> futureResult = new FutureResult<>();
                WeaveAgentComponent.getInstance(myProject).calculateWeaveType(expectedOutput.getPath(), event -> {
                    WeaveType result = getWeaveServiceManager().parseType(event.typeString());
                    expectedOutputType.put(scenario, result);
                    futureResult.set(result);
                });
                try {
                    return futureResult.get(500, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return null;
                }
            }
        }
    }

    @Nullable
    public ImplicitInput getCurrentImplicitTypes(WeaveDocument weaveDocument) {
        final Scenario currentScenario = getCurrentScenarioFor(weaveDocument);
        if (weaveDocument == null) {
            return null;
        }
        if (implicitInputTypes.containsKey(currentScenario)) {
            return implicitInputTypes.get(currentScenario);
        } else {
            final FutureResult<ImplicitInput> futureResult = new FutureResult<>();
            if (currentScenario != null && WeaveAgentComponent.getInstance(myProject).isWeaveRuntimeInstalled()) {
                VirtualFile inputs = currentScenario.getInputs();
                if (inputs != null) {
                    WeaveAgentComponent.getInstance(myProject).calculateImplicitInputTypes(inputs.getPath(), event -> {
                        ImplicitInput implicitInput = new ImplicitInput();
                        WeaveTypeEntry[] weaveTypeEntries = event.types();
                        final DWEditorToolingAPI dataWeaveServiceManager = getWeaveServiceManager();
                        for (WeaveTypeEntry weaveTypeEntry : weaveTypeEntries) {
                            WeaveType weaveType = dataWeaveServiceManager.parseType(weaveTypeEntry.wtypeString());
                            if (weaveType == null) {
                                //If no type was infer the use any
                                weaveType = AnyType.apply();
                            }
                            implicitInput.addInput(weaveTypeEntry.name(), weaveType);
                        }
                        implicitInputTypes.put(currentScenario, implicitInput);
                        futureResult.set(implicitInput);
                    });
                    try {
                        return futureResult.get(500, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        return null;
                    }
                }
            }
            return null;

        }
    }

    public DWEditorToolingAPI getWeaveServiceManager() {
        return DWEditorToolingAPI.getInstance(myProject);
    }

    @Nullable
    public Scenario getCurrentScenarioFor(@Nullable WeaveDocument weaveDocument) {
        if (weaveDocument == null) {
            return null;
        }
        Scenario scenario = selectedScenario.get(weaveDocument.getQualifiedName());
        if (scenario == null) {
            List<Scenario> scenariosFor = getScenariosFor(weaveDocument);
            if (!scenariosFor.isEmpty()) {
                scenario = scenariosFor.get(0);
            }
        }
        return scenario;
    }


    @NotNull
    public List<Scenario> getScenariosFor(WeaveDocument weaveDocument) {
        final List<Scenario> result = new ArrayList<>();
        final PsiFile weaveFile = weaveDocument.getContainingFile();
        final Module moduleForFile = ModuleUtil.findModuleForFile(weaveFile.getVirtualFile(), weaveFile.getProject());
        if (moduleForFile != null) {
            VirtualFile integrationTestFolder = getScenariosTestFolder(moduleForFile);
            if (integrationTestFolder != null) {
                List<VirtualFile> scenarios = findScenarios(weaveFile, integrationTestFolder);
                result.addAll(scenarios.stream().map(Scenario::new).collect(Collectors.toList()));
            }
        }
        return result;
    }

    private List<VirtualFile> findScenarios(PsiFile psiFile, VirtualFile integrationTestFolder) {
        WeaveDocument weaveDocument = getWeaveDocument(psiFile);
        if (weaveDocument != null) {
            String qualifiedName = weaveDocument.getQualifiedName();
            if (qualifiedName != null) {
                VirtualFile testDirectory = integrationTestFolder.findChild(qualifiedName);
                if (testDirectory != null) {
                    return Arrays.asList(testDirectory.getChildren());
                }
            }
        }
        return new ArrayList<>();
    }

    @Nullable
    private VirtualFile getScenariosTestFolder(@Nullable Module module) {
        if (module == null) {
            return null;
        }
        VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(true);
        for (VirtualFile sourceRoot : sourceRoots) {
            if (sourceRoot.isDirectory() && sourceRoot.getName().endsWith(INTEGRATION_TEST_FOLDER_NAME)) {
                return sourceRoot;
            }
        }
        return null;
    }


    @Override
    public void dispose() {

    }
}
