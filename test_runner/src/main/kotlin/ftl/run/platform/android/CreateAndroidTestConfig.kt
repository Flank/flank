package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import ftl.args.yml.UploadedApks
import ftl.util.FlankFatalError

internal fun AndroidArgs.createAndroidTestConfig(
    uploadedApks: UploadedApks,
    testShards: ShardChunks? = null,
    runGcsPath: String? = null
): AndroidTestConfig = when {
    isInstrumentationTest -> createInstrumentationConfig(
        uploadedApks = uploadedApks,
        testShards = getTestShards(testShards)
    )
    isRoboTest
    -> createRoboConfig(
        uploadedApks = uploadedApks,
        runGcsPath = runGcsPath ?: throw FlankFatalError("Arg runGcsPath is required for robo test.")
    )
    else -> throw FlankFatalError("Cannot create AndroidTestConfig, invalid AndroidArgs.")
}

private fun AndroidArgs.getTestShards(testShards: ShardChunks?): ShardChunks =
    if (disableSharding && testTargets.isNullOrEmpty())
        emptyList()
    else
        testShards ?: throw FlankFatalError("Arg testShards is required for instrumentation test.")
