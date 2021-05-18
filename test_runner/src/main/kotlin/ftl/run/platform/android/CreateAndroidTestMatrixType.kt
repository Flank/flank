package ftl.run.platform.android

import ftl.api.TestMatrixAndroid
import ftl.args.AndroidArgs
import ftl.run.model.AndroidTestContext
import ftl.run.model.GameLoopContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.model.RoboTestContext
import ftl.run.model.SanityRoboTestContext
import ftl.shard.testCases

internal fun AndroidArgs.createAndroidTestMatrixType(
    testContext: AndroidTestContext
): TestMatrixAndroid.Type = when (testContext) {
    is InstrumentationTestContext -> createInstrumentationConfig(testContext)
    is RoboTestContext -> createRoboConfig(testContext)
    is SanityRoboTestContext -> createSanityRoboConfig(testContext)
    is GameLoopContext -> createAndroidLoopConfig(testContext)
}

internal fun AndroidArgs.createAndroidLoopConfig(
    testApk: GameLoopContext
) = TestMatrixAndroid.Type.GameLoop(
    appApkGcsPath = testApk.app.remote,
    testRunnerClass = testRunnerClass,
    scenarioLabels = scenarioLabels,
    scenarioNumbers = scenarioNumbers
)

internal fun AndroidArgs.createInstrumentationConfig(
    testApk: InstrumentationTestContext
) = TestMatrixAndroid.Type.Instrumentation(
    appApkGcsPath = testApk.app.remote,
    testApkGcsPath = testApk.test.remote,
    testRunnerClass = testRunnerClass,
    orchestratorOption = "USE_ORCHESTRATOR".takeIf { useOrchestrator },
    disableSharding = disableSharding,
    numUniformShards = numUniformShards,
    testShards = testApk.shards.testCases,
    keepTestTargetsEmpty = disableSharding && testTargets.isEmpty(),
    environmentVariables = testApk.environmentVariables,
    testTargetsForShard = testTargetsForShard,
    clientDetail = testApk.clientDetail.apply {
        println("client details")
        println(this)
        println("/client details")
    }
)

internal fun AndroidArgs.createRoboConfig(
    testApk: RoboTestContext
) = TestMatrixAndroid.Type.Robo(
    appApkGcsPath = testApk.app.remote,
    flankRoboDirectives = roboDirectives,
    roboScriptGcsPath = testApk.roboScript.remote
)

internal fun createSanityRoboConfig(
    testApk: SanityRoboTestContext
) = TestMatrixAndroid.Type.Robo(
    appApkGcsPath = testApk.app.remote,
    flankRoboDirectives = null,
    roboScriptGcsPath = null
)
