package org.mule.tooling.lang.dw.service;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.application.WriteAction;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.util.ProgressIndicatorUtils;
import com.intellij.openapi.progress.util.ReadTask;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModifiableRootModel;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.intellij.util.concurrency.FutureResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mule.tooling.lang.dw.WeaveConstants;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.service.agent.WeaveAgentService;
import org.mule.tooling.lang.dw.util.ModuleUtils;
import org.mule.tooling.lang.dw.util.WeaveUtils;
import org.mule.weave.v2.agent.api.event.WeaveDataFormatDescriptor;
import org.mule.weave.v2.agent.api.event.WeaveTypeEntry;
import org.mule.weave.v2.editor.ImplicitInput;
import org.mule.weave.v2.ts.AnyType;
import org.mule.weave.v2.ts.WeaveType;
import scala.Tuple2;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import static com.intellij.openapi.command.WriteCommandAction.runWriteCommandAction;
import static java.util.Optional.ofNullable;
import static org.mule.tooling.lang.dw.parser.psi.WeavePsiUtils.getWeaveDocument;

@Service(Service.Level.PROJECT)
public final class WeaveRuntimeService implements Disposable {

    private static final Logger LOG = Logger.getInstance(WeaveRuntimeService.class);

    private Map<String, Scenario> selectedScenariosByMapping = new HashMap<>();
    private Map<Scenario, ImplicitInputStatus> implicitInputTypes = new ConcurrentHashMap<>();
    private Map<Scenario, WeaveType> expectedOutputType = new HashMap<>();
    private Map<String, VirtualFile> dwitFolders = new HashMap<>();

    private String[] modules = new String[0];
    private Project myProject;
    private volatile boolean started;

    public static WeaveRuntimeService getInstance(Project myProject) {
        return myProject.getService(WeaveRuntimeService.class);
    }

    public WeaveRuntimeService(Project project) {
        myProject = project;
        initComponent();
    }

    public void initComponent() {
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
        final Application app = ApplicationManager.getApplication();
        ReadAction.nonBlocking(() -> {
                    return ModuleUtil.findModuleForFile(modifiedFile, myProject);
                })
                .submit(AppExecutorUtil.getAppExecutorService())
                .onSuccess((moduleForFile) -> {
                    app.invokeLater(() -> {
                        WriteAction.run(() -> {
                            final VirtualFile dwitFolder = WeaveUtils.getDWTestResourceFolder(moduleForFile);
                            if (dwitFolder != null && VfsUtil.isAncestor(dwitFolder, modifiedFile, true)) {
                                VirtualFile scenario = findScenario(modifiedFile, dwitFolder);
                                onModified(new Scenario(scenario));
                            }
                        });
                    });
                });

    }

    public void availableDataFormat(Consumer<WeaveDataFormatDescriptor[]> callback) {
        FutureResult<WeaveDataFormatDescriptor[]> futureResult = new FutureResult<>();
        WeaveAgentService.getInstance(myProject).dataFormats((dataFormatEvent) -> {
            WeaveDataFormatDescriptor[] formats = dataFormatEvent.formats();
            callback.accept(formats);
        });
    }


    @NotNull
    public String[] getAvailableModule() {
        if (modules.length > 0) {
            return modules;
        } else {
            FutureResult<String[]> futureResult = new FutureResult<>();
            WeaveAgentService.getInstance(myProject).availableModules((modulesEvent) -> {
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
        ImplicitInputStatus implicitInputStatus = implicitInputTypes.get(scenario);
        if (implicitInputStatus != null) {
            implicitInputTypes.put(scenario, new ImplicitInputStatus(implicitInputStatus.implicitInput(), false, false));
        }
    }

    public void setCurrentScenario(WeaveDocument weaveDocument, Scenario scenario) {
        this.selectedScenariosByMapping.put(getTestFolderName(weaveDocument), scenario);
    }

    @NotNull
    private String getTestFolderName(WeaveDocument weaveDocument) {
        // Replace directory separator with a "-".  Support Mac, Unix and Windows directory separators
        return weaveDocument.getQualifiedName().replaceAll("::", "/");
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
                WeaveAgentService.getInstance(myProject).calculateWeaveType(expectedOutput.getPath(), event -> {
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
        if (weaveDocument == null || currentScenario == null) {
            return null;
        }
        if (implicitInputTypes.containsKey(currentScenario)
                && (implicitInputTypes.get(currentScenario).active() || implicitInputTypes.get(currentScenario).resolving())) {
            return implicitInputTypes.get(currentScenario).implicitInput();
        } else {
            final FutureResult<ImplicitInput> futureResult = new FutureResult<>();
            if (WeaveAgentService.getInstance(myProject).isWeaveRuntimeInstalled()) {
                //We create Dummy inputs
                createDummyInputs(currentScenario, false);
                VirtualFile inputs = currentScenario.getInputs();
                if (inputs != null) {
                    AppExecutorUtil.getAppExecutorService().submit(() -> {
                                WeaveAgentService.getInstance(myProject)
                                        .calculateImplicitInputTypes(inputs.getPath(), event -> {
                                            final ImplicitInput implicitInput = new ImplicitInput();
                                            final WeaveTypeEntry[] weaveTypeEntries = event.types();
                                            final WeaveToolingService dataWeaveServiceManager = getWeaveServiceManager();
                                            for (WeaveTypeEntry weaveTypeEntry : weaveTypeEntries) {
                                                WeaveType weaveType = dataWeaveServiceManager.parseType(weaveTypeEntry.wtypeString());
                                                if (weaveType == null) {
                                                    //If no type was infer the use any
                                                    weaveType = AnyType.apply();
                                                }
                                                implicitInput.addInput(weaveTypeEntry.name(), weaveType);
                                            }
                                            implicitInputTypes.put(currentScenario, new ImplicitInputStatus(implicitInput, true, false));
                                            futureResult.set(implicitInput);
                                        });
                            }
                    );
                    try {
                        return futureResult.get(WeaveConstants.SERVER_TIMEOUT, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | ExecutionException | TimeoutException e) {
                        LOG.warn("Unable Infer Input Types. Reason: \n" + e.getMessage(), e);
                        if (implicitInputTypes.containsKey(currentScenario)) {
                            return implicitInputTypes.get(currentScenario).implicitInput();
                        } else {
                            return createDummyInputs(currentScenario, false);
                        }
                    }
                }
            }
            return null;
        }
    }

    private @Nullable ImplicitInput createDummyInputs(Scenario currentScenario, boolean resolving) {
        final VirtualFile scenarioInputs = currentScenario.getInputs();
        if (scenarioInputs != null) {
            final VirtualFile[] children = scenarioInputs.getChildren();
            final HashMap<String, WeaveType> inputTypes = new HashMap<>();
            for (VirtualFile child : children) {
                inputTypes.put(child.getNameWithoutExtension(), AnyType.apply());
            }
            implicitInputTypes.put(currentScenario, new ImplicitInputStatus(ImplicitInput.apply(toScalaImmutableMap(inputTypes)), false, resolving));
        } else {
            implicitInputTypes.put(currentScenario, new ImplicitInputStatus(null, false, resolving));
        }
        return implicitInputTypes.get(currentScenario).implicitInput();
    }

    @SuppressWarnings("unchecked")
    private static <K, V> scala.collection.immutable.Map<K, V> toScalaImmutableMap(java.util.Map<K, V> javaMap) {
        final java.util.List<scala.Tuple2<K, V>> list = new java.util.ArrayList<>(javaMap.size());
        for (final java.util.Map.Entry<K, V> entry : javaMap.entrySet()) {
            list.add(scala.Tuple2.apply(entry.getKey(), entry.getValue()));
        }
        final scala.collection.Seq<Tuple2<K, V>> seq = scala.collection.JavaConverters.asScalaBufferConverter(list).asScala().toSeq();
        return (scala.collection.immutable.Map<K, V>) scala.collection.immutable.Map$.MODULE$.apply(seq);
    }

    public WeaveToolingService getWeaveServiceManager() {
        return WeaveToolingService.getInstance(myProject);
    }

    @Nullable
    public Scenario getCurrentScenarioFor(@Nullable WeaveDocument weaveDocument) {
        if (weaveDocument == null) {
            return null;
        }
        String qName = ReadAction.compute(() -> getTestFolderName(weaveDocument));
        Scenario scenario = selectedScenariosByMapping.get(qName);
        if (scenario == null || !scenario.isValid()) {
            selectedScenariosByMapping.remove(qName);
            List<Scenario> scenarios = ReadAction.compute(() -> getScenariosFor(weaveDocument));
            if (!scenarios.isEmpty()) {
                scenario = scenarios.get(0);
            }
        }
        return scenario;
    }

    public List<Scenario> getScenariosFor(@Nullable VirtualFile file) {
        if (file == null) {
            return Collections.emptyList();
        }
        PsiFile psiFile = PsiManager.getInstance(myProject).findFile(file);
        if (psiFile != null) {
            final WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
            if (weaveDocument != null) {
                return getScenariosFor(weaveDocument);
            } else {
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }

    @Nullable
    public Scenario getScenarioWithName(VirtualFile file, String name) {
        List<Scenario> scenariosFor = getScenariosFor(file);
        for (Scenario scenario : scenariosFor) {
            if (scenario.getName().equals(name)) {
                return scenario;
            }
        }
        return null;
    }

    @NotNull
    public List<Scenario> getScenariosFor(WeaveDocument weaveDocument) {
        if (weaveDocument == null) {
            return Collections.emptyList();
        }
        final List<Scenario> result = new ArrayList<>();
        final PsiFile weaveFile = weaveDocument.getContainingFile();
        final Module moduleForFile = ModuleUtils.findModule(weaveFile);
        if (moduleForFile != null) {
            List<VirtualFile> scenarios = findScenarios(weaveFile);
            result.addAll(scenarios.stream().map(Scenario::new).toList());
        }
        return result;
    }

    private List<VirtualFile> findScenarios(PsiFile psiFile) {
        VirtualFile mappingTestFolder = findMappingTestResourcesFolder(psiFile);
        if (mappingTestFolder != null) {
            return Arrays.asList(mappingTestFolder.getChildren());
        }
        return new ArrayList<>();
    }

    @Nullable
    public VirtualFile findMappingTestResourcesFolder(PsiFile psiFile) {
        final WeaveDocument document = getWeaveDocument(psiFile);
        if (document != null) {
            final String qualifiedName = ReadAction.compute(() -> getTestFolderName(document));
            final VirtualFile scenariosRootFolder = getScenariosResourceFolder(psiFile);
            if (scenariosRootFolder != null && scenariosRootFolder.isValid()) {
                return scenariosRootFolder.findFileByRelativePath(qualifiedName);
            }
        }
        return null;
    }


    @Nullable
    public VirtualFile findTestFolder(PsiFile dwFile) {
        final WeaveDocument document = getWeaveDocument(dwFile);
        if (document != null) {
            final String qualifiedName = ReadAction.compute(() -> getTestFolderName(document));
            final VirtualFile scenariosRootFolder = getScenariosTestFolder(dwFile);
            if (scenariosRootFolder != null && scenariosRootFolder.isValid()) {
                try {
                    return createDirectories(qualifiedName, scenariosRootFolder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }

    private VirtualFile createDirectories(String qualifiedName, VirtualFile scenariosRootFolder) throws IOException {
        final String[] directories = qualifiedName.split("/");
        VirtualFile childDirectory = scenariosRootFolder;
        for (String directory : directories) {
            VirtualFile child = childDirectory.findChild(directory);
            if (child != null) {
                childDirectory = child;
            } else {
                childDirectory = childDirectory.createChildDirectory(this, directory);
            }
        }
        return childDirectory;
    }

    @Nullable
    public VirtualFile findOrCreateMappingResourceFolder(PsiFile psiFile) {
        VirtualFile testFolder = findMappingTestResourcesFolder(psiFile);
        if (testFolder == null) {
            testFolder = createMappingResourceFolder(psiFile);
        }
        return testFolder;
    }


    @Nullable
    public Scenario createScenario(PsiFile psiFile, String scenarioName) {
        final VirtualFile testFolder = findOrCreateMappingResourceFolder(psiFile);
        if (testFolder == null) {
            return null;
        }
        try {
            final VirtualFile scenarioFolder = WriteAction.compute(() -> testFolder.createChildDirectory(this, scenarioName));
            Scenario scenario = new Scenario(scenarioFolder);
            WeaveDocument weaveDocument = WeavePsiUtils.getWeaveDocument(psiFile);
            if (weaveDocument != null) {
                setCurrentScenario(weaveDocument, scenario);
                return scenario;
            } else {
                return null;
            }
        } catch (IOException e) {
            LOG.error(e);
            return null;
        }
    }

    @Nullable
    public VirtualFile createMappingResourceFolder(PsiFile weaveFile) {
        return WriteAction.compute(() -> {
            try {
                VirtualFile testResourceFolder = getScenariosResourceFolder(weaveFile);
                if (testResourceFolder == null) {
                    //See if "src/test/resources exists, if not, create it
                    final Module module = ModuleUtils.findModule(weaveFile);
                    if (module == null) {
                        return null;
                    }
                    final ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                    VirtualFile moduleRoot = rootManager.getContentRoots()[0].findChild("src");
                    if (moduleRoot == null) {
                        return null;
                    }
                    //Create it here
                    VirtualFile testFolder = moduleRoot.findChild(WeaveConstants.TEST_BASE_FOLDER_NAME);
                    if (testFolder == null) {
                        testFolder = moduleRoot.createChildDirectory(this, WeaveConstants.TEST_BASE_FOLDER_NAME);
                    } else if (!testFolder.isDirectory()) {
                        return null;
                    }
                    testResourceFolder = testFolder.findChild(WeaveConstants.RESOURCES_FOLDER);
                    if (testResourceFolder == null) {
                        testResourceFolder = testFolder.createChildDirectory(this, WeaveConstants.RESOURCES_FOLDER);
                    } else if (!testResourceFolder.isDirectory()) {
                        return null;
                    }
                    ModifiableRootModel model = ModuleRootManager.getInstance(module).getModifiableModel();
                    ContentEntry[] entries = model.getContentEntries();
                    for (ContentEntry entry : entries) {
                        if (Objects.equals(entry.getFile(), moduleRoot))
                            entry.addSourceFolder(testResourceFolder, true);
                    }
                    model.commit();
                }
                final WeaveDocument document = WeavePsiUtils.getWeaveDocument(weaveFile);
                if (document != null) {
                    String qName = getTestFolderName(document);
                    VirtualFile child = testResourceFolder.findChild(qName);
                    if (child == null) {
                        return createDirectories(qName, testResourceFolder);
                    } else {
                        if (child.isDirectory()) {
                            return child;
                        } else {
                            return null;
                        }
                    }
                } else {
                    return null;
                }
            } catch (IOException e) {
                LOG.error(e);
                return null;
            }
        });
    }

    @Nullable
    public VirtualFile getScenariosResourceFolder(PsiFile weaveFile) {
        final Module module = ModuleUtils.findModule(weaveFile);
        if (module != null) {
            return WeaveUtils.getDWTestResourceFolder(module);
        }
        return null;
    }

    @Nullable
    public VirtualFile getScenariosTestFolder(PsiFile weaveFile) {
        final Module module = ModuleUtils.findModule(weaveFile);
        if (module != null) {
            return WeaveUtils.getDWTestFolder(module);
        }
        return null;
    }


    @Override
    public void dispose() {
    }

    public void createTest(PsiFile mappingFile, Scenario scenario) {
        try {
            WriteAction.run(() -> {
                VirtualFile testFolder = findTestFolder(mappingFile);
                if (testFolder == null) {
                    return;
                }
                WeaveDocument weaveDocument = getWeaveDocument(mappingFile);
                if (weaveDocument != null) {
                    final String testFolderName = getTestFolderName(weaveDocument);
                    final String testModule = weaveDocument.getName() + "Test.dwl";
                    final VirtualFile childData = testFolder.createChildData(this, testModule);
                    final String scenarioPath = testFolderName + "/" + scenario.getName();
                    final String mimeType = ofNullable(weaveDocument.getOutput())
                            .flatMap((out) ->
                                    ofNullable(out.getDataFormat())
                                            .map((o) -> o.getText())
                                            .or(() ->
                                                    ofNullable(out.getIdentifier())
                                                            .map((i) -> i.getText())))
                            .orElse("json");

                    final String testCase = "\t\"Assert " + scenario.getName() + "\" in do {\n" +
                            "\t\t\tevalPath(\"" + testFolderName + ".dwl\", inputsFrom(\"" + scenarioPath + "\"), '" + mimeType + "') \n\t\t\t\tmust equalTo(outputFrom(\"" + scenarioPath + "\")) \n " +
                            "\t\t}\n";
                    VirtualFile testFile = testFolder.findChild(testModule);
                    if (testFile == null) {
                        final String testSuite = "import * from dw::test::Tests\n" +
                                "import * from dw::test::Asserts\n" +
                                "---\n" +
                                "\"Test " + weaveDocument.getName() + "\" describedBy [\n" +
                                testCase +
                                "]";
                        childData.setBinaryContent(testSuite.getBytes(StandardCharsets.UTF_8));

                        Notifications.Bus.notify(new Notification(WeaveAgentService.WEAVE_NOTIFICATION, "New Test " + childData.getPath() + " was created", NotificationType.INFORMATION));
                    } else {
                        PsiFile testPsiFile = PsiManager.getInstance(myProject).findFile(testFile);
                        WeaveDocument testWeaveDocument = getWeaveDocument(testPsiFile);
                        WeaveBody body = testWeaveDocument.getBody();
                        if (body != null) {
                            WeaveExpression expression = body.getExpression();
                            if (expression instanceof WeaveBinaryExpression) {
                                List<WeaveExpression> expressionList = ((WeaveBinaryExpression) expression).getExpressionList();
                                if (expressionList.size() == 2) {
                                    WeaveExpression weaveExpression = expressionList.get(1);
                                    if (weaveExpression instanceof WeaveArrayExpression) {

                                        runWriteCommandAction(myProject, () -> {
                                            String text = weaveExpression.getText();
                                            int i = text.lastIndexOf("]");
                                            String s = text.substring(0, i).trim() + ",\n\t" + testCase + "]";
                                            WeaveExpression textCaseExpression = WeaveElementFactory.createExpression(myProject, s);
                                            weaveExpression.replace(textCaseExpression);
                                        });
                                        Notifications.Bus.notify(new Notification(WeaveAgentService.WEAVE_NOTIFICATION, "New Test case was added to " + childData.getPath() + ".", NotificationType.INFORMATION));
                                    }
                                }
                            }
                        }

                    }


                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


}

record ImplicitInputStatus(ImplicitInput implicitInput, boolean active, boolean resolving) {
}

