package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.InstrumentationTestContext

internal fun AndroidArgs.createInstrumentationConfig(
    testApk: InstrumentationTestContext
) = AndroidTestConfig.Instrumentation(
    appApkGcsPath = testApk.app.gcs,
    testApkGcsPath = testApk.test.gcs,
    testRunnerClass = testRunnerClass,
    orchestratorOption = "USE_ORCHESTRATOR".takeIf { useOrchestrator },
    disableSharding = disableSharding,
    numUniformShards = numUniformShards,
    testShards = testApk.shards.map { it.testsList },
    keepTestTargetsEmpty = disableSharding && testTargets.isEmpty()
)
