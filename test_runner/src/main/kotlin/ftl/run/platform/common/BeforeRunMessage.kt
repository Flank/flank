package ftl.run.platform.common

import ftl.args.IArgs
import ftl.args.ShardChunks

internal fun beforeRunMessage(args: IArgs, testShardChunks: ShardChunks): String {
    val runCount = args.repeatTests
    val shardCount = testShardChunks.size
    val testsCount = testShardChunks.sumBy { it.size }

    val result = StringBuilder()
    result.appendLine(
        "  $testsCount test${s(testsCount)} / $shardCount shard${s(shardCount)}"
    )

    if (runCount > 1) {
        result.appendLine("  Running ${runCount}x")
        val runDevices = runCount * shardCount
        val runTests = runCount * testsCount
        result.appendLine("    $runDevices total shard${s(runDevices)}")
        result.appendLine("    $runTests total test${s(runTests)}")
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
