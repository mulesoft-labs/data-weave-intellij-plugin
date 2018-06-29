package org.mule.tooling.lang.dw.service;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.progress.util.ReadTask;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.util.concurrency.FutureResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.parser.psi.WeaveDocument;
import org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentRuntimeManager;
import org.mule.weave.v2.debugger.event.WeaveDataFormatDescriptor;
import org.mule.weave.v2.debugger.event.WeaveTypeEntry;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.ts.AnyType;
import org.mule.weave.v2.ts.WeaveType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import static org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils.getWeaveDocument;

public class WeaveRuntimeContextManager extends AbstractProjectComponent implements Disposable {

  private Map<String, Scenario> selectedScenariosByMapping = new HashMap<>();
  private Map<Scenario, ImplicitInput> implicitInputTypes = new HashMap<>();
  private Map<Scenario, WeaveType> expectedOutputType = new HashMap<>();
  private Map<String, VirtualFile> dwitFolders = new HashMap<>();
  private WeaveDataFormatDescriptor[] dataFormat = new WeaveDataFormatDescriptor[0];
  private String[] modules = new String[0];

  private List<StatusChangeListener> listeners = new ArrayList<>();


  protected WeaveRuntimeContextManager(Project project) {
    super(project);
  }

  public static WeaveRuntimeContextManager getInstance(Project myProject) {
    return myProject.getComponent(WeaveRuntimeContextManager.class);
  }

  public void addListener(StatusChangeListener listener) {
    if (modules.length > 0) {
      listener.onModulesLoaded(modules);
    }
    if (dataFormat.length > 0) {
      listener.onDataFormatLoaded(dataFormat);
    }

    this.listeners.add(listener);
  }

  @Override
  public void initComponent() {
    super.initComponent();

    WeaveAgentRuntimeManager.getInstance(myProject).addStatusListener(() -> {
      WeaveAgentRuntimeManager.getInstance(myProject).dataFormats((dataFormatEvent) -> {
        dataFormat = dataFormatEvent.formats();
        for (StatusChangeListener listener: listeners) {
          listener.onDataFormatLoaded(dataFormat);
        }
      });
      WeaveAgentRuntimeManager.getInstance(myProject).availableModules((modulesEvent) -> {
        modules = modulesEvent.modules();
        for (StatusChangeListener listener: listeners) {
          listener.onModulesLoaded(modules);
        }
      });
    });

    PsiManager.getInstance(myProject).addPsiTreeChangeListener(new PsiTreeChangeAdapter() {
      @Override
      public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        ProgressIndicatorUtils.scheduleWithWriteActionPriority(new ReadTask() {
          @Override
          public void computeInReadAction(@NotNull ProgressIndicator indicator) throws ProcessCanceledException {
            onModified(event.getFile());
          }

          @Override
          public void onCanceled(@NotNull ProgressIndicator indicator) {

          }
        });

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

    final VirtualFile dwitFolder = getScenariosRootFolder(moduleForFile);
    if (dwitFolder != null && VfsUtil.isAncestor(dwitFolder, modifiedFile, true)) {
      VirtualFile scenario = findScenario(modifiedFile, dwitFolder);
      onModified(new Scenario(scenario));
    }
  }

  @NotNull
  public WeaveDataFormatDescriptor[] getAvailableDataFormat() {
    if (dataFormat.length > 0) {
      return dataFormat;
    } else {
      FutureResult<WeaveDataFormatDescriptor[]> futureResult = new FutureResult<>();
      WeaveAgentRuntimeManager.getInstance(myProject).dataFormats((dataFormatEvent) -> {
        WeaveDataFormatDescriptor[] formats = dataFormatEvent.formats();
        this.dataFormat = formats;
        futureResult.set(formats);
      });
      try {
        return futureResult.get(WeaveConstants.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        return dataFormat;
      }
    }
  }

  @NotNull
  public String[] getAvailableModule() {
    if (modules.length > 0) {
      return modules;
    } else {
      FutureResult<String[]> futureResult = new FutureResult<>();
      WeaveAgentRuntimeManager.getInstance(myProject).availableModules((modulesEvent) -> {
        String[] modules = modulesEvent.modules();
        this.modules = modules;
        futureResult.set(modules);
      });
      try {
        return futureResult.get(WeaveConstants.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
      } catch (InterruptedException | ExecutionException | TimeoutException e) {
        return modules;
      }
    }
  }

  @NotNull
  private VirtualFile findScenario(VirtualFile modifiedFile, VirtualFile dwitFolder) {
    VirtualFile scenario = modifiedFile;
    if (scenario.getParent().equals(dwitFolder)) {
      return scenario;
    }
    while (!scenario.getParent().getParent().equals(dwitFolder)) {
      scenario = scenario.getParent();
    }
    return scenario;
  }

  private void onModified(Scenario scenario) {
    implicitInputTypes.remove(scenario);
    expectedOutputType.remove(scenario);
  }

  public void setCurrentScenario(WeaveDocument weaveDocument, Scenario scenario) {
    this.selectedScenariosByMapping.put(weaveDocument.getQualifiedName(), scenario);
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
        WeaveAgentRuntimeManager.getInstance(myProject).calculateWeaveType(expectedOutput.getPath(), event -> {
          WeaveType result = getWeaveServiceManager().parseType(event.typeString());
          expectedOutputType.put(scenario, result);
          futureResult.set(result);
        });
        try {
          return futureResult.get(WeaveConstants.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
          return null;
        }
      }
    }
  }

  @Nullable
  public ImplicitInput getImplicitInputTypes(WeaveDocument weaveDocument) {
    final Scenario currentScenario = getCurrentScenarioFor(weaveDocument);
    if (weaveDocument == null) {
      return null;
    }
    if (implicitInputTypes.containsKey(currentScenario)) {
      return implicitInputTypes.get(currentScenario);
    } else {
      final FutureResult<ImplicitInput> futureResult = new FutureResult<>();
      if (currentScenario != null && WeaveAgentRuntimeManager.getInstance(myProject).isWeaveRuntimeInstalled()) {
        VirtualFile inputs = currentScenario.getInputs();
        if (inputs != null) {
          WeaveAgentRuntimeManager.getInstance(myProject).calculateImplicitInputTypes(inputs.getPath(), event -> {
            final ImplicitInput implicitInput = new ImplicitInput();
            final WeaveTypeEntry[] weaveTypeEntries = event.types();
            final WeaveEditorToolingAPI dataWeaveServiceManager = getWeaveServiceManager();
            for (WeaveTypeEntry weaveTypeEntry: weaveTypeEntries) {
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
            return futureResult.get(WeaveConstants.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
          } catch (InterruptedException | ExecutionException | TimeoutException e) {
            return null;
          }
        }
      }
      return null;
    }
  }

  public WeaveEditorToolingAPI getWeaveServiceManager() {
    return WeaveEditorToolingAPI.getInstance(myProject);
  }

  @Nullable
  public Scenario getCurrentScenarioFor(@Nullable WeaveDocument weaveDocument) {
    if (weaveDocument == null) {
      return null;
    }
    String qName = weaveDocument.getQualifiedName();
    Scenario scenario = selectedScenariosByMapping.get(qName);
    if (scenario == null || !scenario.isValid()) {
      selectedScenariosByMapping.remove(qName);
      List<Scenario> scenarios = getScenariosFor(weaveDocument);
      if (!scenarios.isEmpty()) {
        scenario = scenarios.get(0);
      }
    }
    return scenario;
  }

  public List<Scenario> getScenariosFor(VirtualFile file) {
    PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
    if (psiFile != null) {
      final WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
      return getScenariosFor(weaveDocument);
    } else {
      return Collections.emptyList();
    }
  }

  @Nullable
  public Scenario getScenarioWithName(VirtualFile file, String name) {
    List<Scenario> scenariosFor = getScenariosFor(file);
    for (Scenario scenario: scenariosFor) {
      if (scenario.getName().equals(name)) {
        return scenario;
      }
    }
    return null;
  }


  @NotNull
  public List<Scenario> getScenariosFor(WeaveDocument weaveDocument) {
    final List<Scenario> result = new ArrayList<>();
    final PsiFile weaveFile = weaveDocument.getContainingFile();
    final Module moduleForFile = ModuleUtil.findModuleForFile(weaveFile.getVirtualFile(), weaveFile.getProject());
    if (moduleForFile != null) {
      List<VirtualFile> scenarios = findScenarios(weaveFile);
      result.addAll(scenarios.stream().map(Scenario::new).collect(Collectors.toList()));
    }
    return result;
  }

  private List<VirtualFile> findScenarios(PsiFile psiFile) {
    VirtualFile mappingTestFolder = findMappingTestFolder(psiFile);
    if (mappingTestFolder != null) {
      return Arrays.asList(mappingTestFolder.getChildren());
    }
    return new ArrayList<>();
  }

  @Nullable
  public VirtualFile findMappingTestFolder(PsiFile psiFile) {
    WeaveDocument document = getWeaveDocument(psiFile);
    if (document != null) {
      String qualifiedName = document.getQualifiedName();
      VirtualFile scenariosRootFolder = getScenariosRootFolder(psiFile);
      if (scenariosRootFolder != null && scenariosRootFolder.isValid()) {
        return scenariosRootFolder.findChild(qualifiedName);
      }
    }
    return null;
  }

  public VirtualFile findOrCreateMappingTestFolder(PsiFile psiFile) {
    VirtualFile testFolder = findMappingTestFolder(psiFile);
    if (testFolder == null) {
      testFolder = createMappingTestFolder(psiFile);
    }
    return testFolder;
  }


  @Nullable
  public Scenario createScenario(PsiFile psiFile, String scenarioName) {
    VirtualFile testFolder = findOrCreateMappingTestFolder(psiFile);
    try {
      VirtualFile scenarioFolder = WriteAction.compute(() -> testFolder.createChildDirectory(this, scenarioName));
      Scenario scenario = new Scenario(scenarioFolder);
      WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
      setCurrentScenario(weaveDocument, scenario);
      return scenario;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }

  @Nullable
  public VirtualFile createMappingTestFolder(PsiFile weaveFile) {
    return WriteAction.compute(() -> {
      try {
        //TODO: handle creation of dwit folder
        VirtualFile dwitFolder = getScenariosRootFolder(weaveFile);
        WeaveDocument document = WeavePsiUtils.getWeaveDocument(weaveFile);
        String qName = document.getQualifiedName();
        return dwitFolder.createChildDirectory(this, qName);
      } catch (IOException e) {
        e.printStackTrace();
        return null;
      }
    });
  }

  @Nullable
  public VirtualFile getScenariosRootFolder(PsiFile weaveFile) {
    final Module module = ModuleUtil.findModuleForFile(weaveFile.getVirtualFile(), weaveFile.getProject());
    if (module != null) {
      return getScenariosRootFolder(module);
    }
    return null;
  }

  @Nullable
  private VirtualFile getScenariosRootFolder(@Nullable Module module) {
    if (module == null) {
      return null;
    }
    String moduleName = module.getName();
    VirtualFile maybeFolder = dwitFolders.get(moduleName);
    if (maybeFolder != null) {
      return maybeFolder;
    }
    VirtualFile[] sourceRoots = ModuleRootManager.getInstance(module).getSourceRoots(true);
    for (VirtualFile sourceRoot: sourceRoots) {
      if (sourceRoot.isDirectory() && sourceRoot.getName().endsWith(WeaveConstants.INTEGRATION_TEST_FOLDER_NAME)) {
        dwitFolders.put(moduleName, sourceRoot);
        return sourceRoot;
      }
    }
    return null;
  }


  @Override
  public void dispose() {

  }

  public interface StatusChangeListener {
    default void onDataFormatLoaded(WeaveDataFormatDescriptor[] dataFormatDescriptor) {

    }

    default void onModulesLoaded(String[] modules) {

    }

  }
}
