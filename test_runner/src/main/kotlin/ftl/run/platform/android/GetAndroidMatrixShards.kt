package ftl.run.platform.android

import ftl.args.AndroidArgs
import ftl.run.model.AndroidMatrixTestShards
import ftl.run.model.AndroidTestShards
import ftl.run.model.InstrumentationTestContext

suspend fun AndroidArgs.getAndroidMatrixShards(): AndroidMatrixTestShards = this
    .createAndroidTestContexts()
    .filterIsInstance<InstrumentationTestContext>()
    .asMatrixTestShards()

private fun List<InstrumentationTestContext>.asMatrixTestShards(): AndroidMatrixTestShards =
    map { testApks ->
        AndroidTestShards(
            app = testApks.app.local,
            test = testApks.test.local,
            shards = testApks.shards.map { it.testStringList }.mapIndexed { index, testCases ->
                "shard-$index" to testCases
            }.toMap(),
            junitIgnored = testApks.ignoredTestCases
        )
    }.mapIndexed { index, androidTestShards ->
        "matrix-$index" to androidTestShards
    }.toMap()
