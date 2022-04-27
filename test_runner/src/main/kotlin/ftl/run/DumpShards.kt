package ftl.run

import flank.common.OutputLogLevel
import flank.common.logLn
import flank.common.toFile
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.args.isInstrumentationTest
import ftl.config.FtlConstants
import ftl.ios.xctest.common.XcTestRunVersion.V1
import ftl.ios.xctest.common.XcTestRunVersion.V2
import ftl.run.common.prettyPrint
import ftl.run.exception.FlankConfigurationError
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.platform.android.getAndroidMatrixShards
import ftl.util.obfuscatePrettyPrinter
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

suspend fun AndroidArgs.dumpShards(
    // VisibleForTesting
    shardFilePath: String = ANDROID_SHARD_FILE,
) {
    if (!isInstrumentationTest) throw FlankConfigurationError(
        "Cannot dump shards for non instrumentation test, ensure test apk has been set."
    )
    val shards: AndroidMatrixTestShards = getAndroidMatrixShards()
    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = shards,
        size = shards.flatMap { it.value.shards.values }.count(),
        obfuscatedOutput = obfuscateDumpShards
    )
}

fun IosArgs.dumpShards(
    // VisibleForTesting
    shardFilePath: String = IOS_SHARD_FILE,
) {
    val rawShards: Any = when (xcTestRunData.version) {
        // avoid crashing when shardTargets.values is null
        V1 -> xcTestRunData.shardTargets.values.firstOrNull() ?: xcTestRunData.shardTargets
        V2 -> xcTestRunData.shardTargets
    }

    val size = xcTestRunData.shardTargets.values
        .flatten().size

    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = rawShards,
        size = size,
        obfuscatedOutput = obfuscateDumpShards
    )
}

fun saveShardChunks(
    shardFilePath: String,
    shards: Any,
    size: Int,
    obfuscatedOutput: Boolean
) {
    shardFilePath.createDirectories()
    Files.write(
        Paths.get(shardFilePath),
        getGson(obfuscatedOutput).toJson(shards).toByteArray()
    )
    logLn("${FtlConstants.indent}Saved $size shards to $shardFilePath", OutputLogLevel.DETAILED)
}

private fun String.createDirectories() {
    if (this !in listOf(ANDROID_SHARD_FILE, IOS_SHARD_FILE))
        File(this).parent.toFile().mkdirs()
}

private fun getGson(obfuscatedOutput: Boolean) =
    if (obfuscatedOutput) obfuscatePrettyPrinter else prettyPrint

const val ANDROID_SHARD_FILE = "android_shards.json"
const val IOS_SHARD_FILE = "ios_shards.json"
