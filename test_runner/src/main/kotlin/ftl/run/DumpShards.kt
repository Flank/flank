package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.args.isInstrumentationTest
import ftl.run.common.prettyPrint
import ftl.run.exception.FlankConfigurationError
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.platform.android.getAndroidMatrixShards
import ftl.util.obfuscatePrettyPrinter
import java.nio.file.Files
import java.nio.file.Paths

suspend fun dumpShards(
    args: AndroidArgs,
    shardFilePath: String = ANDROID_SHARD_FILE,
    obfuscatedOutput: Boolean = false
) {
    if (!args.isInstrumentationTest) throw FlankConfigurationError(
        "Cannot dump shards for non instrumentation test, ensure test apk has been set."
    )
    val shards: AndroidMatrixTestShards = args.getAndroidMatrixShards()
    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = shards,
        size = shards.size,
        obfuscatedOutput = obfuscatedOutput
    )
}

fun dumpShards(
    args: IosArgs,
    shardFilePath: String = IOS_SHARD_FILE,
    obfuscatedOutput: Boolean = false
) {
    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = args.testShardChunks.map { it.testStringList },
        size = args.testShardChunks.size,
        obfuscatedOutput = obfuscatedOutput
    )
}

private fun saveShardChunks(
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
