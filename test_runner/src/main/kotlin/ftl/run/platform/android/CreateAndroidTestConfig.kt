package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.model.SanityRoboTestContext

internal fun AndroidArgs.createAndroidTestConfig(
    testContext: AndroidTestContext
): AndroidTestConfig = when (testContext) {
    is InstrumentationTestContext -> createInstrumentationConfig(testContext)
    is RoboTestContext -> createRoboConfig(testContext)
    is SanityRoboTestContext -> createSanityRoboConfig(testContext)
}
