package ftl.shard

import ftl.args.IArgs
import ftl.args.IArgs.Companion.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE
import ftl.reports.xml.model.JUnitTestResult
import ftl.run.exception.FlankConfigurationError
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
    args.shardTime < NO_LIMIT || args.shardTime == 0 -> throw FlankConfigurationError("Invalid shard time ${args.shardTime}")
    else -> calculateShardCount(testsToRun, oldTestResult, args)
}

private fun calculateShardCount(
    testsToRun: List<FlankTestMethod>,
    oldTestResult: JUnitTestResult,
    args: IArgs
): Int {
    val previousMethodDurations = createTestMethodDurationMap(oldTestResult, args)
    return calculateShardCount(
        args = args,
        testsTotalTime = testTotalTime(
            testsToRun, previousMethodDurations, args.fallbackTestTime(previousMethodDurations),
            args.defaultClassTestTime
        ),
        testsToRunCount = testsToRun.size
    )
}

private fun testTotalTime(
    testsToRun: List<FlankTestMethod>,
    previousMethodDurations: Map<String, Double>,
    defaultTestTime: Double,
    defaultClassTestTime: Double
) = testsToRun.sumByDouble { flankTestMethod ->
    getTestMethodTime(
        flankTestMethod,
        previousMethodDurations,
        defaultTestTime,
        defaultClassTestTime
    )
}

private fun calculateShardCount(
    args: IArgs,
    testsTotalTime: Double,
    testsToRunCount: Int
): Int = when {
    testsTotalTime <= args.shardTime -> SINGLE_SHARD
    args.maxTestShards == NO_LIMIT -> min(AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last, testsToRunCount)
    else -> shardCount(testsTotalTime, args).also { count ->
        if (count <= 0) throw FlankConfigurationError("Invalid shard count $count")
    }
}

private fun shardCount(testsTotalTime: Double, args: IArgs) =
    min(shardsByTime(testsTotalTime, args), args.maxTestShards)

private fun shardsByTime(testsTotalTime: Double, args: IArgs) = ceil(testsTotalTime / args.shardTime).toInt()
