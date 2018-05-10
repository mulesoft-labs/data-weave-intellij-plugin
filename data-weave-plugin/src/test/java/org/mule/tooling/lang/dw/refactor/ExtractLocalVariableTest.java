package org.mule.tooling.lang.dw.refactor;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiUtil;
import com.intellij.testFramework.EdtTestUtilKt;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.TestLoggerFactory;
import com.intellij.testFramework.fixtures.DefaultLightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import org.apache.commons.io.FilenameUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mule.tooling.lang.dw.parser.psi.*;
import org.mule.tooling.lang.dw.util.ResultHolder;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.mule.tooling.lang.dw.parser.DataWeaveLangParserTest.DWL;

@RunWith(Parameterized.class)
public class ExtractLocalVariableTest extends LightPlatformCodeInsightFixtureTestCase {

    private String testName;

    public ExtractLocalVariableTest(String testName) {
        this.testName = testName;
    }

    @Before
    public void before() throws Throwable {
        super.setUp();
    }

    @After
    public void after() throws Throwable {
        super.tearDown();
    }


    @Override
    protected LightProjectDescriptor getProjectDescriptor() {
        return new DefaultLightProjectDescriptor();
    }

    @Test
    @Ignore
    public void checkRefactor() throws Throwable {

        final ResultHolder<Throwable> result = new ResultHolder<>();

        EdtTestUtilKt.runInEdtAndWait(() -> {
            myFixture.configureByFile(new File(testName + ".dwl").getName());
            IntroduceLocalVariableHandler introduceLocalVariableHandler = new IntroduceLocalVariableHandler() {
                @Override
                public PsiElement getRootScope(PsiFile psiFile, int selectionStart) {
                    PsiElement elementAtOffset = PsiUtil.getElementAtOffset(psiFile, selectionStart);
                    //Dummy implementation of scope search
                    return WeavePsiUtils.getParent(elementAtOffset, element -> {
                        if (element instanceof WeaveDocument) {
                            return true;
                        } else if (element instanceof WeaveDoExpression) {
                            return true;
                        } else if (element.getParent() instanceof WeaveFunctionDefinition) {
                            return true;
                        } else if (element.getParent() instanceof WeaveBinaryExpression) {
                            return ((WeaveBinaryExpression) element.getParent()).getRight() == element;
                        }

                        return false;
                    });

                }
            };
            PsiElement valueToReplace = introduceLocalVariableHandler.getValueToReplace(myFixture.getFile(), myFixture.getEditor());
            PsiElement rootScope = introduceLocalVariableHandler.getRootScope(myFixture.getFile(), myFixture.getEditor().getSelectionModel().getSelectionStart());
            introduceLocalVariableHandler.getWeaveInplaceVariableIntroducer(myFixture.getProject(), myFixture.getEditor(), valueToReplace, rootScope, Collections.singletonList("myVar"));
            myFixture.checkResultByFile(new File(testName + "_post.dwl").getName(), true);
            TestLoggerFactory.onTestFinished(true);
            return null;
        });

        if (result.nonEmpty()) {
            throw result.get();
        }
    }

    @Override
    protected String getBasePath() {
        return super.getBasePath();
    }

    @Parameterized.Parameters(name = "Parsing {0}")
    public static Iterable<Object[]> data() {
        Class<?> dataWeaveLangParserTestClass = ExtractLocalVariableTest.class;
        String path = dataWeaveLangParserTestClass.getPackage().getName().replace('.', File.separatorChar);
        String anchorFileName = path + File.separatorChar + dataWeaveLangParserTestClass.getSimpleName() + ".txt";
        URL resource = dataWeaveLangParserTestClass.getClassLoader().getResource(anchorFileName);
        if (resource == null) {
            throw new RuntimeException("Unable to resolve anchor file " + anchorFileName);
        }
        File parentFile = new File(resource.getPath()).getParentFile();
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


    protected String getTestDataPath() {
        return new File(getClass().getResource("ExtractLocalVariableTest.txt").getPath()).getParent();
    }
}
