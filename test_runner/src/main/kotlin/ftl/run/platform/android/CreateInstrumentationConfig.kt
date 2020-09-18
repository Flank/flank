package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.InstrumentationTestContext
import ftl.shard.testCases

internal fun AndroidArgs.createInstrumentationConfig(
    testApk: InstrumentationTestContext
) = AndroidTestConfig.Instrumentation(
    appApkGcsPath = testApk.app.gcs,
    testApkGcsPath = testApk.test.gcs,
    testRunnerClass = testRunnerClass,
    orchestratorOption = "USE_ORCHESTRATOR".takeIf { useOrchestrator },
    disableSharding = disableSharding,
    numUniformShards = numUniformShards,
    testShards = testApk.shards.testCases,
    keepTestTargetsEmpty = disableSharding && testTargets.isEmpty(),
    environmentVariables = testApk.environmentVariables
)
