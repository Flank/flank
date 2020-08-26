package ftl.run.platform.common

import ftl.args.Chunk
import ftl.args.IArgs
import ftl.config.FtlConstants

internal fun beforeRunMessage(args: IArgs, testShardChunks: List<Chunk>): String {
    val runCount = args.repeatTests
    val shardCount = testShardChunks.size
    val (classesCount, testsCount) = testShardChunks.flatMap { it.testMethods }.partition { it.isParameterized }.run { first.count() to second.count() }

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
        result.appendLine("    $runTests total test${s(runTests)}")
        result.appendLine("    $runClasses total class${es(runClasses)}")
    }

    return result.toString()
}

private fun s(amount: Int): String {
    return if (amount > 1) {
        "s"
    } else {
        ""
    }
}

private fun es(amount: Int): String {
    return if (amount > 1) {
        "es"
    } else {
        ""
    }
}
