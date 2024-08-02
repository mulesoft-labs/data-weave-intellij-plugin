package org.mule.tooling.lang.dw.parser;


import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.testFramework.TestApplicationManagerKt;
import com.intellij.testFramework.TestRunnerUtil;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mule.tooling.lang.dw.util.ResultHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@RunWith(Parameterized.class)
public class DataWeaveLangParserTest extends ParsingTestCase {

    public static final String DWL = ".dwl";

    private String name;

    public DataWeaveLangParserTest(String name) {
        super("", "dwl", new WeaveParserDefinition());
        this.name = name;
    }

    @Before
    public void before() throws Throwable {
        super.setUp();
    }


    @After
    public void after() throws Throwable {
        super.tearDown();
    }

    public void runTest(ThrowableRunnable<Throwable> runnable) throws Throwable {
        final ResultHolder<Throwable> result = new ResultHolder<>();
        TestApplicationManagerKt.replaceIdeEventQueueSafely();
        runTestRunnable(runnable);
        if (result.nonEmpty()) {
            throw result.get();
        }
    }

    @Test
    public void checkNoError() throws Throwable {
        runTest(() -> {
            try {
                System.out.println("name = " + name);
                String text = loadFile(name);
                myFile = createPsiFile(name, text);
                ensureParsed(myFile);
                assertEquals("light virtual file text mismatch", text, ((LightVirtualFile) myFile.getVirtualFile()).getContent().toString());
                assertEquals("virtual file text mismatch", text, LoadTextUtil.loadText(myFile.getVirtualFile()));
                assertEquals("doc text mismatch", text, myFile.getViewProvider().getDocument().getText());
                assertEquals("psi text mismatch", text, myFile.getText());
                ensureCorrectReparse(myFile);
                List<PsiErrorElement> errors = getErrors(myFile);
//                System.out.println(toParseTreeText(myFile, true, true));
                assertEmpty(name + " has errors \n" + toParseTreeText(myFile, true, true), errors);
            } catch (Exception e) {
                fail(e.getMessage());
            }
        });
    }

    public static List<PsiErrorElement> getErrors(@NotNull PsiElement element) {
        List<PsiErrorElement> errors = new ArrayList<>();
        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child instanceof PsiErrorElement) {
                errors.add((PsiErrorElement) child);
            }
            errors.addAll(getErrors(child));
        }
        return errors;
    }

    @Override
    protected String getTestDataPath() {
        return new File(getClass().getResource("array.dwl").getPath()).getParent();
    }

    @Parameterized.Parameters(name = "Parsing {0}")
    public static Iterable<Object[]> data() {
        Class<DataWeaveLangParserTest> dataWeaveLangParserTestClass = DataWeaveLangParserTest.class;
        String path = dataWeaveLangParserTestClass.getPackage().getName().replace('.', File.separatorChar);
        File parentFile = new File(dataWeaveLangParserTestClass.getClassLoader().getResource(path + File.separatorChar + "array.dwl").getPath()).getParentFile();
        String[] list = parentFile.list((dir, name) -> name.endsWith(DWL) || name.endsWith(".lwd"));
        List<Object[]> result = new ArrayList<>();
        if (list != null) {
            for (String testName : list) {
                result.add(new Object[]{testName});
            }
        }
        result.sort(Comparator.comparing(o -> o[0].toString()));
        return result;
    }


}
