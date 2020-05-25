package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.model.AndroidTestShards
import ftl.run.model.InstrumentationTestApk
import ftl.util.asFileReference

fun getAndroidMatrixShards(
    args: AndroidArgs
): AndroidMatrixTestShards =
    getInstrumentationShardChunks(
        args = args,
        testApks = args.createInstrumentationTestApks()
    ).asMatrixTestShards()

private fun AndroidArgs.createInstrumentationTestApks(): List<InstrumentationTestApk> =
    listOfNotNull(
        testApk?.let { testApk ->
            InstrumentationTestApk(
                app = appApk.asFileReference(),
                test = testApk.asFileReference()
            )
        }
    ) + additionalAppTestApks.map {
        InstrumentationTestApk(
            app = (it.app ?: appApk).asFileReference(),
            test = it.test.asFileReference()
        )
    }

private fun Map<InstrumentationTestApk, ShardChunks>.asMatrixTestShards(): AndroidMatrixTestShards =
    map { (testApks, shards: List<List<String>>) ->
        AndroidTestShards(
            app = testApks.app.local,
            test = testApks.test.local,
            shards = shards.mapIndexed { index, testCases ->
                "shard-$index" to testCases
            }.toMap()
        )
    }.mapIndexed { index, androidTestShards ->
        "matrix-$index" to androidTestShards
    }.toMap()
