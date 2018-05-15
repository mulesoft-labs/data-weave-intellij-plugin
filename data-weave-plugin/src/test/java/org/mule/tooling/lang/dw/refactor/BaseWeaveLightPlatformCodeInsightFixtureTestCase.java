package org.mule.tooling.lang.dw.refactor;

import com.intellij.openapi.util.text.StringUtil;
import com.intellij.testFramework.EdtTestUtilKt;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestRunnerUtil;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;

import java.io.File;
import java.net.URL;

public abstract class BaseWeaveLightPlatformCodeInsightFixtureTestCase extends LightPlatformCodeInsightFixtureTestCase {

    public static File getResourcesTestFolder(Class<?> dataWeaveLangParserTestClass) {
        String path = dataWeaveLangParserTestClass.getPackage().getName().replace('.', File.separatorChar);
        String anchorFileName = path + File.separatorChar + dataWeaveLangParserTestClass.getSimpleName() + ".txt";
        URL resource = dataWeaveLangParserTestClass.getClassLoader().getResource(anchorFileName);
        if (resource == null) {
            throw new RuntimeException("Unable to resolve anchor file " + anchorFileName);
        }
        return new File(resource.getPath()).getParentFile();
    }

    @Before
    public void before() throws Throwable {
        setUp();
        EdtTestUtilKt.runInEdtAndWait(() -> {
            String sourceFilePath = getSdkRelativePath();
            myFixture.copyDirectoryToProject(sourceFilePath, "");
            return null;
        });
    }

    @NotNull
    protected String getSdkRelativePath() {
        String name = ExtractLocalVariableTest.class.getPackage().getName();
        int i = StringUtil.countChars(name, '.');
        String repeat = StringUtil.repeat("../", i + 1);
        return repeat + "sdk";
    }

    @After
    public void after() throws Throwable {
        tearDown();
    }

    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new DefaultLightProjectDescriptor();
    }

    protected void runTest(Runnable runnable) throws Throwable {
        if (runInDispatchThread()) {
            TestRunnerUtil.replaceIdeEventQueueSafely();
            invokeTestRunnable(runnable);
        } else {
            invokeTestRunnable(runnable);
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
