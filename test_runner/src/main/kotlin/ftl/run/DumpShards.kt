package ftl.run

import ftl.args.AndroidArgs
import ftl.args.IosArgs
import ftl.run.common.prettyPrint
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.platform.android.getAndroidMatrixShards
import ftl.util.FlankFatalError
import java.nio.file.Files
import java.nio.file.Paths

suspend fun dumpShards(
    args: AndroidArgs,
    shardFilePath: String = ANDROID_SHARD_FILE
) {
    if (!args.isInstrumentationTest) throw FlankFatalError(
        "Cannot dump shards for non instrumentation test, ensure test apk has been set."
    )
    val shards: AndroidMatrixTestShards = args.getAndroidMatrixShards()
    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = shards,
        size = shards.size
    )
}

fun dumpShards(
    args: IosArgs,
    shardFilePath: String = IOS_SHARD_FILE
) {
    saveShardChunks(
        shardFilePath = shardFilePath,
        shards = args.testShardChunks,
        size = args.testShardChunks.size
    )
}

private fun saveShardChunks(
    shardFilePath: String,
    shards: Any,
    size: Int
) {
    Files.write(
        Paths.get(shardFilePath),
        prettyPrint.toJson(shards).toByteArray()
    )
    println("Saved $size shards to $shardFilePath")
}

const val ANDROID_SHARD_FILE = "android_shards.json"
const val IOS_SHARD_FILE = "ios_shards.json"
