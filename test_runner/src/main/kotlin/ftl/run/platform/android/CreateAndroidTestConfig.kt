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
        testShards = testShards!!
    )
    isRoboTest -> createRoboConfig(
        uploadedApks = uploadedApks,
        runGcsPath = runGcsPath!!
    )
    else -> throw FlankFatalError("No testShards xor runGcsPath are specified")
}
