package ftl.shard

import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.util.FlankTestMethod
import ftl.util.FlankFatalError
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

data class CacheTestCounter(
    val allTestCount: Int,
    private val cacheMiss: Int
) {
    val cacheHit = allTestCount - cacheMiss
    val cachePercent: Double
        get() = if (allTestCount == 0) 0.0 else cacheHit.toDouble() / allTestCount * 100.0
}

/** List of shards containing test names as a string. */
typealias StringShards = List<MutableList<String>>

fun List<TestShard>.stringShards(): StringShards {
    return this.map { shard ->
        shard.testMethods.map { it.name }.toMutableList()
    }.toList()
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
    // When a test does not have previous results to reference, fall back to this run time.
    @VisibleForTesting
    const val DEFAULT_TEST_TIME_SEC = 120.0
    private const val IGNORE_TEST_TIME = 0.0

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
        testsToRun: List<FlankTestMethod>,
        oldTestResult: JUnitTestResult,
        args: IArgs
    ): Int {
        if (args.shardTime == -1) return -1
        assertNotTrue(args.shardTime < -1 || args.shardTime == 0) { FlankFatalError("Invalid shard time ${args.shardTime}") }

        val oldDurations = createTestMethodDurationMap(oldTestResult, args)
        val testsTotalTime = testsToRun.sumByDouble {
            if (it.ignored) IGNORE_TEST_TIME else oldDurations[it.testName] ?: DEFAULT_TEST_TIME_SEC
        }

        val shardsByTime = ceil(testsTotalTime / args.shardTime).toInt()

        // Use a single shard unless total test time is greater than shardTime.
        if (testsTotalTime <= args.shardTime) {
            return 1
        }

        // If there is no limit, use the calculated amount
        if (args.maxTestShards == -1) {
            return shardsByTime
        }

        // We need to respect the maxTestShards
        val shardCount = min(shardsByTime, args.maxTestShards)

        assertNotTrue(shardCount <= 0) { FlankFatalError("Invalid shard count $shardCount") }
        return shardCount
    }

    // take in the XML with timing info then return list of shards based on the amount of shards to use
    fun createShardsByShardCount(
        testsToRun: List<FlankTestMethod>,
        oldTestResult: JUnitTestResult,
        args: IArgs,
        forcedShardCount: Int = -1
    ): List<TestShard> {
        assertNotTrue((forcedShardCount < -1 || forcedShardCount == 0)) {
            FlankFatalError("Invalid forcedShardCount value $forcedShardCount")
        }
        val maxShards = if (forcedShardCount == -1) args.maxTestShards else forcedShardCount

        val previousMethodDurations = createTestMethodDurationMap(oldTestResult, args)
        var cacheMiss = 0
        val testCases: List<TestMethod> = testsToRun
            .map {
                TestMethod(
                    name = it.testName,
                    time = if (it.ignored) {
                        IGNORE_TEST_TIME
                    } else {
                        previousMethodDurations.getOrElse(it.testName) { DEFAULT_TEST_TIME_SEC.also { cacheMiss++ } }
                    }
                )
            }
            // We want to iterate over testcase going from slowest to fastest
            .sortedByDescending(TestMethod::time)

        val testCount = getNumberOfNotIgnoredTestCases(testCases)

        // If maxShards is infinite or we have more shards than tests, let's match it
        val shardsCount = matchNumberOfShardsWithTestCount(maxShards, testCount)

        // Create the list of shards we will return
        assertNotTrue(shardsCount <= 0) {
            FlankFatalError(
                """Invalid shard count. To debug try: flank ${args.platformName} run --dump-shards
                    | args.maxTestShards: ${args.maxTestShards}
                    | forcedShardCount: $forcedShardCount 
                    | testCount: $testCount 
                    | maxShards: $maxShards 
                    | shardsCount: $shardsCount""".trimMargin()
            )
        }
        var shards = createListOfShards(shardsCount)

        testCases.forEach { testMethod ->
            addTestMethodToMostEmptyShard(shards, testMethod)
            shards = shards.mostEmptyFirst()
        }

        val cacheTestCounter = CacheTestCounter(testsToRun.size, cacheMiss)
        println()
        println("  Smart Flank cache hit: ${cacheTestCounter.cachePercent.roundToInt()}% (${cacheTestCounter.cacheHit} / ${cacheTestCounter.allTestCount})")
        println("  Shard times: " + shards.joinToString(", ") { "${it.time.roundToInt()}s" } + "\n")

        return shards
    }

    private fun addTestMethodToMostEmptyShard(shards: List<TestShard>, testMethod: TestMethod) {
        val mostEmptyShard = shards.first()
        mostEmptyShard.testMethods.add(testMethod)
        mostEmptyShard.time += testMethod.time
    }

    private val IArgs.platformName
        get() = if (this is IosArgs) "ios" else "android"

    private fun assertNotTrue(condition: Boolean, lazyException: () -> Throwable) {
        if (condition) {
            throw lazyException()
        }
    }

    private fun List<TestShard>.mostEmptyFirst() = sortedBy { it.time }

    private fun getNumberOfNotIgnoredTestCases(testCases: List<TestMethod>): Int {
        // Ugly hotfix for case when all test cases are annotated with @Ignore
        // we need to filter them because they have time == 0.0 which cause empty shards creation, few lines later
        // and we don't need additional shards for ignored tests.
        return if (testCases.isEmpty()) 0 else testCases.filter { it.time > 0.0 }.takeIf { it.isNotEmpty() }?.size ?: 1
    }

    private fun matchNumberOfShardsWithTestCount(maxShards: Int, testCount: Int) =
        if (maxShards == -1 || maxShards > testCount) testCount else maxShards

    private fun createListOfShards(shardsCount: Int) = List(shardsCount) { TestShard(0.0, mutableListOf()) }

    fun createTestMethodDurationMap(junitResult: JUnitTestResult, args: IArgs): Map<String, Double> {
        val junitMap = mutableMapOf<String, Double>()

        // Create a map with information from previous junit run
        junitResult.testsuites?.forEach { testsuite ->
            testsuite.testcases?.forEach { testcase ->
                if (!testcase.empty() && testcase.time != null) {
                    val key = if (args is AndroidArgs) testcase.androidKey() else testcase.iosKey()
                    val time = testcase.time.toDouble()
                    if (time >= 0) junitMap[key] = time
                }
            }
        }

        return junitMap
    }
}
