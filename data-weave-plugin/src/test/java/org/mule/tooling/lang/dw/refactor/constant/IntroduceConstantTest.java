package org.mule.tooling.lang.dw.refactor.constant;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mule.tooling.lang.dw.refactor.BaseWeaveLightPlatformCodeInsightFixtureTestCase;
import org.mule.tooling.lang.dw.refactor.IntroduceConstantHandler;

@RunWith(Parameterized.class)
public class IntroduceConstantTest extends BaseWeaveLightPlatformCodeInsightFixtureTestCase {


    private String testName;

    public IntroduceConstantTest(String testName) {
        this.testName = testName;
    }

    @Parameterized.Parameters(name = "Introduce Constant in {0}")
    public static Iterable<Object[]> data() {
        return createPrePostScenarios(IntroduceConstantTest.class);
    }

    @Test
    public void check() throws Throwable {
        runIntroduceHandlerTest(testName, new IntroduceConstantHandler());
    }


}
