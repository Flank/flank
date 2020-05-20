package ftl.shard

import ftl.args.IArgs
import ftl.args.IArgs.Companion.AVAILABLE_SHARD_COUNT_RANGE
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.FlankFatalError
import ftl.util.FlankTestMethod
import kotlin.math.ceil
import kotlin.math.min

private const val SINGLE_SHARD = 1
private const val NO_LIMIT = -1

// take in the XML with timing info then return the shard count based on execution time
fun shardCountByTime(
    testsToRun: List<FlankTestMethod>,
    oldTestResult: JUnitTestResult,
    args: IArgs
): Int = when {
    args.shardTime == NO_LIMIT -> NO_LIMIT
    args.shardTime < NO_LIMIT || args.shardTime == 0 -> throw FlankFatalError("Invalid shard time ${args.shardTime}")
    else -> calculateShardCount(testsToRun, oldTestResult, args)
}

private fun calculateShardCount(
    testsToRun: List<FlankTestMethod>,
    oldTestResult: JUnitTestResult,
    args: IArgs
): Int {
    val oldDurations = createTestMethodDurationMap(oldTestResult, args)
    val testsTotalTime = testTotalTime(testsToRun, oldDurations)

    return calculateShardCount(args, testsTotalTime, testsToRun.size)
}

private fun testTotalTime(testsToRun: List<FlankTestMethod>, previousMethodDurations: Map<String, Double>): Double {
    return testsToRun.sumByDouble {
        if (it.ignored) IGNORE_TEST_TIME else previousMethodDurations[it.testName] ?: DEFAULT_TEST_TIME_SEC
    }
}

private fun calculateShardCount(
    args: IArgs,
    testsTotalTime: Double,
    testsToRunCount: Int
): Int = when {
    testsTotalTime <= args.shardTime -> SINGLE_SHARD
    args.maxTestShards == NO_LIMIT -> min(AVAILABLE_SHARD_COUNT_RANGE.last, testsToRunCount)
    else -> shardCount(testsTotalTime, args).also { count ->
        if (count <= 0) throw FlankFatalError("Invalid shard count $count")
    }
}

private fun shardCount(testsTotalTime: Double, args: IArgs) =
    min(shardsByTime(testsTotalTime, args), args.maxTestShards)

private fun shardsByTime(testsTotalTime: Double, args: IArgs) = ceil(testsTotalTime / args.shardTime).toInt()
