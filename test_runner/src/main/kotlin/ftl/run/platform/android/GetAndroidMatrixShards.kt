package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.model.AndroidTestShards
import ftl.run.model.InstrumentationTestContext
import ftl.shard.testCases

suspend fun AndroidArgs.getAndroidMatrixShards(): AndroidMatrixTestShards = this
    .createAndroidTestContexts()
    .filterIsInstance<InstrumentationTestContext>()
    .asMatrixTestShards()

fun List<InstrumentationTestContext>.asMatrixTestShards(): AndroidMatrixTestShards =
    map { testApks ->
        AndroidTestShards(
            app = testApks.app.local,
            test = testApks.test.local,
            shards = testApks.shards.testCases.mapIndexed { index, testCases ->
                "shard-$index" to testCases
            }.toMap(),
            junitIgnored = testApks.ignoredTestCases
        )
    }.mapIndexed { index, androidTestShards ->
        "matrix-$index" to androidTestShards
    }.toMap()
