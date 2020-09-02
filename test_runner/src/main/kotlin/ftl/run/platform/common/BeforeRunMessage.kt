package ftl.run.platform.common

import ftl.args.IArgs
import ftl.config.FtlConstants
import ftl.shard.Chunk
import ftl.shard.TestMethod

internal fun beforeRunMessage(args: IArgs, testShardChunks: List<Chunk>): String {
    val runCount = args.repeatTests
    val shardCount = testShardChunks.size
    val (classesCount, testsCount) = testShardChunks.partitionedTestCases.testAndClassesCount

    val result = StringBuilder()
    val testString = if (testsCount > 0) "$testsCount test${s(testsCount)}" else ""
    val classString = if (classesCount > 0) "$classesCount class${es(classesCount)}" else ""

    result.appendLine(
        "${FtlConstants.indent}$testString${if (testsCount * classesCount > 0) " + " else ""}$classString / $shardCount shard${s(shardCount)}"
    )

    if (runCount > 1) {
        result.appendLine("  Running ${runCount}x")
        val runDevices = runCount * shardCount
        val runTests = runCount * testsCount
        val runClasses = runCount * classesCount
        result.appendLine("    $runDevices total shard${s(runDevices)}")
        if (runTests > 0) result.appendLine("    $runTests total test${s(runTests)}")
        if (runClasses > 0) result.appendLine("    $runClasses total class${es(runClasses)}")
    }

    return result.toString()
}

private val List<Chunk>.partitionedTestCases
    get() = flatMap { it.testMethods }.partition { it.isParameterized }

private val Pair<List<TestMethod>, List<TestMethod>>.testAndClassesCount
    get() = first.count() to second.count()

private fun s(amount: Int): String =
    if (amount > 1) "s"
    else ""

private fun es(amount: Int): String =
    if (amount > 1) "es"
    else ""
