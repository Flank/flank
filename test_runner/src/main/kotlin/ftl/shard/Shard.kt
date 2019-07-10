package ftl.shard

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.Utils.fatalError
import kotlin.math.ceil
import kotlin.math.min
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

    // take in the XML with timing info then return the shard count based on execution time
    fun shardCountByTime(
        testsToRun: List<String>,
        oldTestResult: JUnitTestResult,
        args: IArgs
    ): Int {
        if (args.shardTime == -1) return -1

        val junitMap = createJunitMap(oldTestResult, args)
        val testsTotalTime = testsToRun.sumByDouble { junitMap[it] ?: 10.0 }

        val shardsByTime = ceil(testsTotalTime / args.shardTime).toInt()

        // If there is no limit, use the calculated amount
        if (args.maxTestShards == -1) {
            return shardsByTime
        }

        // We need to respect the maxTestShards
        return min(shardsByTime, args.maxTestShards)
    }

    // take in the XML with timing info then return list of shards based on the amount of shards to use
    fun createShardsByShardCount(
        testsToRun: List<String>,
        oldTestResult: JUnitTestResult,
        args: IArgs,
        forcedShardCount: Int = -1
    ): List<TestShard> {
        if (forcedShardCount < -1) fatalError("Invalid forcedShardCount value $forcedShardCount")

        val maxShards = if (forcedShardCount == -1) args.maxTestShards else forcedShardCount
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
        if (shardsCount <= 0) {
            val platform = if (args is IosArgs) "ios" else "android"
            fatalError(
                """Invalid shard count. To debug try: flank $platform run --dump-shards
                    | args.maxTestShards: ${args.maxTestShards}
                    | forcedShardCount: $forcedShardCount 
                    | testCount: $testCount 
                    | maxShards: $maxShards 
                    | shardsCount: $shardsCount""".trimMargin()
            )
        }
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

        val allTests = testsToRun.size // zero when test targets is empty
        val cacheHit = allTests - cacheMiss
        val cachePercent = if (allTests == 0) 0.0 else cacheHit.toDouble() / allTests * 100.0
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
