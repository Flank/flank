package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import ftl.args.yml.UploadedApks

internal fun AndroidArgs.createInstrumentationConfig(
    uploadedApks: UploadedApks,
    keepTestTargetsEmpty: Boolean,
    testShards: ShardChunks
) = AndroidTestConfig.Instrumentation(
    appApkGcsPath = uploadedApks.app,
    testApkGcsPath = uploadedApks.test!!,
    testRunnerClass = testRunnerClass,
    orchestratorOption = "USE_ORCHESTRATOR".takeIf { useOrchestrator },
    disableSharding = disableSharding,
    numUniformShards = numUniformShards,
    testShards = testShards,
    keepTestTargetsEmpty = keepTestTargetsEmpty
)
