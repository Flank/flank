package ftl.client.google.run.ios

import com.google.testing.model.FileReference
import com.google.testing.model.IosTestLoop
import com.google.testing.model.IosXcTest
import com.google.testing.model.TestSpecification
import ftl.api.TestMatrixIos

internal fun TestSpecification.setupIosTest(config: TestMatrixIos.Type) = apply {
    when (config) {
        is TestMatrixIos.Type.XcTest -> iosXcTest = setupXcTest(config)
        is TestMatrixIos.Type.GameLoop -> iosTestLoop = setupGameLoopTest(config)
    }
}

private fun setupXcTest(context: TestMatrixIos.Type.XcTest) = IosXcTest().apply {
    testsZip = FileReference().setGcsPath(context.xcTestGcsPath)
    xctestrun = FileReference().setGcsPath(context.xcTestRunFileGcsPath)
    xcodeVersion = context.xcodeVersion
    testSpecialEntitlements = context.testSpecialEntitlements
}

private fun setupGameLoopTest(context: TestMatrixIos.Type.GameLoop) = IosTestLoop().apply {
    appIpa = FileReference().setGcsPath(context.appGcsPath)
    scenarios = context.scenarios
}
