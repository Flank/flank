package ftl.shard

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import java.lang.IllegalStateException
import kotlin.math.roundToInt

data class TestMethod(
    val name: String,
    val time: Double
)

data class TestShard(
    var time: Double,
    val testMethods: MutableList<TestMethod>
)

/** List of shards containing test names as a string. */
typealias StringShards = MutableList<MutableList<String>>

fun List<TestShard>.stringShards(): StringShards {
    return this.map { shard ->
        shard.testMethods.map { it.name }.toMutableList()
    }.toMutableList()
}

/*

iOS:
<dict>
  <key>StudentUITests</key>
  <key>OnlyTestIdentifiers</key>
    <array>
      <string>GREYError/testCaseClassName</string>

Android:
class com.foo.ClassName#testMethodToSkip

*/

object Shard {

    private fun JUnitTestCase.androidKey(): String {
        return "class $classname#$name"
    }

    private fun JUnitTestCase.iosKey(): String {
        // FTL iOS XML appends `()` to each test name. ex: `testBasicSelection()`
        // xctestrun file requires classname/name with no `()`
        val testName = name?.substringBefore('(')
        return "$classname/$testName"
    }

    data class Bucket(var remaining: Double, val tests: MutableList<String>)

    // take in the XML with timing info then return list of shards based on the time per shard
    fun createShardsByShardTime(
        testsToRun: List<String>,
        oldTestResult: JUnitTestResult,
        args: IArgs
    ): MutableList<MutableList<String>> {
        if (args.shardTime == -1) {
            throw IllegalStateException("shardTime == -1")
        }

        val junitMap = createJunitMap(oldTestResult, args)
        val testsTotalTime = testsToRun.sumByDouble { junitMap[it] ?: 0.0 }

        val minShards = Math.ceil(testsTotalTime / args.shardTime)

        if (minShards > args.testShards) {
            throw IllegalStateException(
                "Total test time is: $testsTotalTime and max number of shards is ${args.testShards}. " +
                        "Min amount of shards needed is ${minShards.roundToInt()}")
        }

        // Start with one bucket
        val buckets = mutableListOf(Bucket(args.shardTime.toDouble(), mutableListOf()))

        testsToRun.forEach testForEach@{ test ->

            var added = false
            val testTime = junitMap[test] ?: 10.0

            // We sort to make sure we try first the most empty bucket
            buckets.sortByDescending { it.remaining }

            // Find a bucket where to fit the test
            buckets.forEach { bucket ->
                if (bucket.remaining >= testTime) {
                    bucket.remaining -= testTime
                    bucket.tests.add(test)
                    added = true

                    return@testForEach
                }
            }

            // If we couldn't fit the test in a bucket, add to a new one
            if (!added) {

                if (args.shardTime - testTime < 0) {
                    throw IllegalStateException("Test $test takes longer than the max time per shard: ${args.shardTime}")
                }

                buckets.add(Bucket(args.shardTime - testTime, mutableListOf(test)))
            }
        }

        println()
        println("  Shard times: " + buckets.joinToString(", ") {
            "${(args.shardTime - it.remaining).roundToInt()}s"
        } + "\n")

        return buckets.map { it.tests }.toMutableList()
    }

    // take in the XML with timing info then return list of shards based on the amount of shards to use
    fun createShardsByShardCount(
        testsToRun: List<String>,
        oldTestResult: JUnitTestResult,
        args: IArgs
    ): List<TestShard> {
        val maxShards = args.testShards
        val junitMap = createJunitMap(oldTestResult, args)

        var cacheMiss = 0
        val testcases = mutableListOf<TestMethod>()

        testsToRun.forEach { key ->
            val previousTime = junitMap[key]
            val time = previousTime ?: 10.0

            if (previousTime == null) {
                cacheMiss += 1
            }

            testcases.add(TestMethod(key, time))
        }

        val testCount = testcases.size

        // If maxShards is infinite or we have more shards than tests, let's match it
        val shardsCount = if (maxShards == -1 || maxShards > testCount) testCount else maxShards

        // Create the list of shards we will return
        var shards = List(shardsCount) { TestShard(0.0, mutableListOf()) }

        // We want to iterate over testcase going from slowest to fastest
        testcases.sortByDescending { it.time }

        testcases.forEach { testMethod ->
            val shard = shards.first()

            shard.testMethods.add(testMethod)
            shard.time += testMethod.time

            // Sort the shards to keep the most empty shard first
            shards = shards.sortedBy { it.time }
        }

        val allTests = testsToRun.size
        val cacheHit = allTests - cacheMiss
        val cachePercent = cacheHit.toDouble() / allTests * 100.0
        println()
        println("  Smart Flank cache hit: ${cachePercent.roundToInt()}% ($cacheHit / $allTests)")
        println("  Shard times: " + shards.joinToString(", ") { "${it.time.roundToInt()}s" } + "\n")

        return shards
    }

    fun createJunitMap(junitResult: JUnitTestResult, args: IArgs): Map<String, Double> {
        val junitMap = mutableMapOf<String, Double>()

        // Create a map with information from previous junit run
        junitResult.testsuites?.forEach { testsuite ->
            testsuite.testcases?.forEach { testcase ->
                if (!testcase.empty() && testcase.time != null) {
                    val key = if (args is AndroidArgs) testcase.androidKey() else testcase.iosKey()
                    junitMap[key] = testcase.time.toDouble()
                }
            }
        }

        return junitMap
    }
}
