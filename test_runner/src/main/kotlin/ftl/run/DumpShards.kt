package ftl.run

import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.args.isInstrumentationTest
import ftl.run.common.prettyPrint
import ftl.run.exception.FlankConfigurationError
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.platform.android.getAndroidMatrixShards
import ftl.shard.testCases
import ftl.util.obfuscatePrettyPrinter
import java.nio.file.Files
import java.nio.file.Paths

suspend fun AndroidArgs.dumpShards(
    @VisibleForTesting
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
    @VisibleForTesting
    shardFilePath: String = IOS_SHARD_FILE,
) {
    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = testShardChunks.testCases,
        size = testShardChunks.size,
        obfuscatedOutput = obfuscateDumpShards
    )
}

fun saveShardChunks(
    shardFilePath: String,
    shards: Any,
    size: Int,
    obfuscatedOutput: Boolean
) {
    Files.write(
        Paths.get(shardFilePath),
        getGson(obfuscatedOutput).toJson(shards).toByteArray()
    )
    println("Saved $size shards to $shardFilePath")
}

private fun getGson(obfuscatedOutput: Boolean) = if (obfuscatedOutput) obfuscatePrettyPrinter else prettyPrint

const val ANDROID_SHARD_FILE = "android_shards.json"
const val IOS_SHARD_FILE = "ios_shards.json"
