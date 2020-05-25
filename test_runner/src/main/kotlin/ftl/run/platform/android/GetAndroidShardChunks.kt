package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import ftl.run.model.InstrumentationTestApk
import ftl.util.asFileReference

fun getAndroidShardChunks(
    args: AndroidArgs,
    testApk: String
): ShardChunks =
    getInstrumentationShardChunks(
        args = args,
        testApks = listOf(InstrumentationTestApk(test = testApk.asFileReference()))
    ).flatMap { (_, shardChunks) -> shardChunks }
