package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext

internal fun AndroidArgs.createAndroidTestConfig(
    testContext: AndroidTestContext
): AndroidTestConfig = when (testContext) {
    is InstrumentationTestContext -> createInstrumentationConfig(testContext)
    is RoboTestContext -> createRoboConfig(testContext)
}
