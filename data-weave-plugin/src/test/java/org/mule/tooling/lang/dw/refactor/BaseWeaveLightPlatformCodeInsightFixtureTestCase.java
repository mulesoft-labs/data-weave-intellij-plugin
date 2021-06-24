package org.mule.tooling.lang.dw.refactor;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.refactoring.RefactoringActionHandler;
import com.intellij.testFramework.EdtTestUtilKt;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestLoggerFactory;
import com.intellij.testFramework.TestRunnerUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.util.ThrowableRunnable;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.mule.tooling.lang.dw.refactor.variable.IntroduceLocalVariableTest;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.mule.tooling.lang.dw.parser.DataWeaveLangParserTest.DWL;

public abstract class BaseWeaveLightPlatformCodeInsightFixtureTestCase extends BasePlatformTestCase {

    public static final String POST_DWL = "_post.dwl";
    public static final String DWL_EXTENSION = ".dwl";

    @NotNull
    public static Iterable<Object[]> createPrePostScenarios(Class<?> dataWeaveLangParserTestClass) {
        File parentFile = getResourcesTestFolder(dataWeaveLangParserTestClass);
        String[] list = parentFile.list((dir, name) -> name.endsWith(DWL) && !name.endsWith("_post.dwl"));
        List<Object[]> result = new ArrayList<>();
        if (list != null) {
            for (String testName : list) {
                result.add(new Object[]{FilenameUtils.getBaseName(testName)});
            }
        }
        result.sort(Comparator.comparing(o -> o[0].toString()));
        return result;
    }

    public static File getResourcesTestFolder(Class<?> dataWeaveLangParserTestClass) {
        String path = dataWeaveLangParserTestClass.getPackage().getName().replace('.', File.separatorChar);
        String anchorFileName = path + File.separatorChar + dataWeaveLangParserTestClass.getSimpleName() + ".txt";
        URL resource = dataWeaveLangParserTestClass.getClassLoader().getResource(anchorFileName);
        if (resource == null) {
            throw new RuntimeException("Anchor not found " + anchorFileName + " create this file so that test cases can be located.");
        }
        return new File(resource.getPath()).getParentFile();
    }

    public void runIntroduceHandlerTest(String testName, RefactoringActionHandler constantHandler) throws Throwable {
        final ThrowableRunnable<Throwable> runnable = () -> {
            myFixture.configureByFile(new File(testName + DWL_EXTENSION).getName());
            constantHandler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
            myFixture.checkResultByFile(new File(testName + POST_DWL).getName(), true);
            TestLoggerFactory.onTestFinished(true);
        };
        runTest(runnable);
    }

    @Before
    public void before() throws Throwable {
        EdtTestUtilKt.runInEdtAndWait(() -> {
            String sourceFilePath = getSdkRelativePath();
            myFixture.copyDirectoryToProject(sourceFilePath, "");
            return null;
        });
    }

    @NotNull
    protected String getSdkRelativePath() {
        String name = IntroduceLocalVariableTest.class.getPackage().getName();
        int i = StringUtil.countChars(name, '.');
        String repeat = StringUtil.repeat("../", i + 1);
        return repeat + "sdk";
    }

    @After
    public void after() throws Throwable {

    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new DefaultLightProjectDescriptor();
    }

    protected void runTest(ThrowableRunnable<Throwable> runnable) throws Throwable {
        if (runInDispatchThread()) {
            TestRunnerUtil.replaceIdeEventQueueSafely();
            runTestRunnable(runnable);
        } else {
            runTestRunnable(runnable);
        }
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath();
    }

    protected String getTestDataPath() {
        return getTestFolder().getParent();
    }

    @NotNull
    private File getTestFolder() {
        return new File(getClass().getResource(getClass().getSimpleName() + ".txt").getPath());
    }
}
