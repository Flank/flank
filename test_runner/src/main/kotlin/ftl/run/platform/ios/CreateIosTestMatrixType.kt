package ftl.run.platform.ios

import ftl.api.TestMatrixIos
import ftl.args.IosArgs
import ftl.run.model.GameloopTestContext
import ftl.run.model.IosTestContext
import ftl.run.model.XcTestContext

internal fun IosArgs.createIosTestMatrixType(
    testContext: IosTestContext
): TestMatrixIos.Type = when (testContext) {
    is GameloopTestContext -> createIosGameLoopConfig(testContext)
    is XcTestContext -> createXCTestConfig(testContext)
}

internal fun IosArgs.createIosGameLoopConfig(
    testContext: GameloopTestContext
) = TestMatrixIos.Type.GameLoop(
    appGcsPath = testContext.appGcsPath,
    scenarios = testContext.scenarios,
    matrixGcsPath = testContext.matrixGcsPath
)

internal fun IosArgs.createXCTestConfig(
    testContext: XcTestContext
) = TestMatrixIos.Type.XcTest(
    xcTestGcsPath = testContext.xcTestGcsPath,
    xcTestRunFileGcsPath = testContext.xcTestRunFileGcsPath,
    xcodeVersion = testContext.xcodeVersion,
    testSpecialEntitlements = testContext.testSpecialEntitlements,
    matrixGcsPath = testContext.matrixGcsPath
)
