package ftl.shard

import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestResult
import ftl.shard.TestMethodDuration.createTestMethodDurationMap
import ftl.shard.TestMethodDuration.testTotalTime
import ftl.util.FlankFatalError
import ftl.util.FlankTestMethod
import kotlin.math.ceil
import kotlin.math.min

private const val SINGLE_SHARD = 1
private const val NO_LIMIT = -1

object ShardCount {
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
    ) : Int {
        val oldDurations = createTestMethodDurationMap(oldTestResult, args)
        val testsTotalTime = testTotalTime(testsToRun, oldDurations)

        // Use a single shard unless total test time is greater than shardTime.
        if (testsTotalTime <= args.shardTime) {
            return SINGLE_SHARD
        }
        val shardsByTime = ceil(testsTotalTime / args.shardTime).toInt()

        // If there is no limit, use the calculated amount
        if (args.maxTestShards == NO_LIMIT) {
            return shardsByTime
        }

        // We need to respect the maxTestShards
        val shardCount = min(shardsByTime, args.maxTestShards)

        if (shardCount <= 0) throw FlankFatalError("Invalid shard count $shardCount")

        return shardCount
    }
}