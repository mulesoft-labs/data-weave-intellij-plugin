package org.mule.tooling.lang.dw.refactor.variable;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mule.tooling.lang.dw.refactor.BaseWeaveLightPlatformCodeInsightFixtureTestCase;
import org.mule.tooling.lang.dw.refactor.IntroduceLocalVariableHandler;

@RunWith(Parameterized.class)
public class IntroduceLocalVariableTest extends BaseWeaveLightPlatformCodeInsightFixtureTestCase {
    private String testName;

    public IntroduceLocalVariableTest(String testName) {
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "Introduce Variable in {0}")
    public static Iterable<Object[]> data() {
        return createPrePostScenarios(IntroduceLocalVariableTest.class);
    }

    @Test
    public void check() throws Throwable {
        runIntroduceHandlerTest(testName, new IntroduceLocalVariableHandler());
    }

}
