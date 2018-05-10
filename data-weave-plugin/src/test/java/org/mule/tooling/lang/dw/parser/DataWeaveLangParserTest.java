package org.mule.tooling.lang.dw.parser;


import com.intellij.openapi.fileEditor.impl.LoadTextUtil;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiErrorElement;
import com.intellij.testFramework.LightVirtualFile;
import com.intellij.testFramework.ParsingTestCase;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.io.IOException;
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

    @Test
    public void checkNoError() throws IOException {
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
        assertEmpty(name + " has errors \n" + toParseTreeText(myFile, true, true), errors);
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
        String[] list = parentFile.list((dir, name) -> name.endsWith(DWL));
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
