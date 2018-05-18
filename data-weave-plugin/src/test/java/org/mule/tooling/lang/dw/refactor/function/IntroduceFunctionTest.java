package org.mule.tooling.lang.dw.refactor.function;

import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiFile;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mule.tooling.lang.dw.refactor.BaseWeaveLightPlatformCodeInsightFixtureTestCase;
import org.mule.tooling.lang.dw.refactor.IntroduceFunctionHandler;
import org.mule.weave.v2.scope.VariableScope;

import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(Parameterized.class)
public class IntroduceFunctionTest extends BaseWeaveLightPlatformCodeInsightFixtureTestCase {

    static Pattern SCOPE_NUMBER = Pattern.compile(".*_scope_([0-9]+)");

    private String testName;

    public IntroduceFunctionTest(String testName) {
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "Introduce Function in {0}")
    public static Iterable<Object[]> data() {
        return createPrePostScenarios(IntroduceFunctionTest.class);
    }

    @Test
    public void check() throws Throwable {
        IntroduceFunctionHandler constantHandler = new IntroduceFunctionHandler() {
            @Override
            protected void selectScope(PsiFile file, Editor editor, List<VariableScope> variableScopes, Function<VariableScope, Void> selectionCallback) {
                //Mock user interaction
                Matcher matcher = SCOPE_NUMBER.matcher(testName);
                if (matcher.find()) {
                    int indexNumber = Integer.parseInt(matcher.group(1));
                    selectionCallback.apply(variableScopes.get(indexNumber));
                } else {
                    selectionCallback.apply(variableScopes.get(0));
                }
            }
        };

        runIntroduceHandlerTest(testName, constantHandler);
    }
}
