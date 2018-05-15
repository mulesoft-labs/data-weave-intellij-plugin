package org.mule.tooling.lang.dw.refactor;

import com.intellij.testFramework.TestLoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.mule.tooling.lang.dw.parser.DataWeaveLangParserTest.DWL;

@RunWith(Parameterized.class)
public class ExtractLocalVariableTest extends BaseWeaveLightPlatformCodeInsightFixtureTestCase {

    public static final String POST_DWL = "_post.dwl";
    public static final String DWL_EXTENSION = ".dwl";

    private String testName;

    public ExtractLocalVariableTest(String testName) {
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "Extract Variable in {0}")
    public static Iterable<Object[]> data() {
        Class<?> dataWeaveLangParserTestClass = ExtractLocalVariableTest.class;
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

    @Test
    public void checkRefactor() throws Throwable {
        Runnable runnable = () -> {
            myFixture.configureByFile(new File(testName + DWL_EXTENSION).getName());
            IntroduceLocalVariableHandler introduceLocalVariableHandler = new IntroduceLocalVariableHandler();
            introduceLocalVariableHandler.invoke(myFixture.getProject(), myFixture.getEditor(), myFixture.getFile(), null);
            myFixture.checkResultByFile(new File(testName + POST_DWL).getName(), true);
            TestLoggerFactory.onTestFinished(true);
        };
        runTest(runnable);
    }

}
