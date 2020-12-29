package ftl.gc.ios

import com.google.testing.model.FileReference
import com.google.testing.model.IosTestLoop
import com.google.testing.model.IosXcTest
import com.google.testing.model.TestSpecification
import ftl.run.model.GameloopTestContext
import ftl.run.model.IosTestContext
import ftl.run.model.XcTestContext

fun TestSpecification.setupIosTest(context: IosTestContext) = apply {
    when (context) {
        is XcTestContext -> iosXcTest = setupXcTest(context)
        is GameloopTestContext -> iosTestLoop = setupGameLoopTest(context)
    }
}

private fun setupXcTest(context: XcTestContext) = IosXcTest().apply {
    testsZip = FileReference().setGcsPath(context.xcTestGcsPath)
    xctestrun = FileReference().setGcsPath(context.xcTestRunFileGcsPath)
    xcodeVersion = context.xcodeVersion
    testSpecialEntitlements = context.testSpecialEntitlements
}

private fun setupGameLoopTest(context: GameloopTestContext) = IosTestLoop().apply {
    appIpa = FileReference().setGcsPath(context.appGcsPath)
    scenarios = context.scenarios
}
