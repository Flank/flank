package ftl.shard

import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.shard.CacheReport.printCacheInfo
import ftl.shard.TestCasesCreator.createTestCases
import ftl.shard.TestMethodDuration.createTestMethodDurationMap
import ftl.util.FlankTestMethod
import ftl.util.FlankFatalError
import kotlin.math.ceil
import kotlin.math.min
import kotlin.math.roundToInt

data class TestShard(
    var time: Double,
    val testMethods: MutableList<TestMethod>
)
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

    // take in the XML with timing info then return list of shards based on the amount of shards to use
    fun createShardsByShardCount(
        testsToRun: List<FlankTestMethod>,
        oldTestResult: JUnitTestResult,
        args: IArgs,
        forcedShardCount: Int = -1
    ): List<TestShard> {
        if (forcedShardCount < -1 || forcedShardCount == 0) throw FlankFatalError("Invalid forcedShardCount value $forcedShardCount")
        val maxShards = if (forcedShardCount == -1) args.maxTestShards else forcedShardCount

        val previousMethodDurations = createTestMethodDurationMap(oldTestResult, args)
        val testCases: List<TestMethod> = createTestCases(testsToRun, previousMethodDurations)
            .sortedByDescending(TestMethod::time)  // We want to iterate over testcase going from slowest to fastest

        val testCount = getNumberOfNotIgnoredTestCases(testCases)

        // If maxShards is infinite or we have more shards than tests, let's match it
        val shardsCount = matchNumberOfShardsWithTestCount(maxShards, testCount)

        // Create the list of shards we will return
        if (shardsCount <= 0) throw FlankFatalError(
            """Invalid shard count. To debug try: flank ${args.platformName} run --dump-shards
                    | args.maxTestShards: ${args.maxTestShards}
                    | forcedShardCount: $forcedShardCount 
                    | testCount: $testCount 
                    | maxShards: $maxShards 
                    | shardsCount: $shardsCount""".trimMargin()
        )
        
        val shards = createShardsForTestCases(testCases, shardsCount)
        
        printCacheInfo(testsToRun, previousMethodDurations)
        printShardsInfo(shards)
        return shards
    }
    
    private fun createShardsForTestCases(testCases: List<TestMethod>, shardsCount: Int): List<TestShard> {
        var shards = createListOfShards(shardsCount)
        testCases.forEach { testMethod ->
            addTestMethodToMostEmptyShard(shards, testMethod)
            shards = shards.mostEmptyFirst()
        }
        
        return shards
    }

    private fun printShardsInfo(shards: List<TestShard>) {
        println("  Shard times: " + shards.joinToString(", ") { "${it.time.roundToInt()}s" } + "\n")
    }

    private fun addTestMethodToMostEmptyShard(shards: List<TestShard>, testMethod: TestMethod) {
        val mostEmptyShard = shards.first()
        mostEmptyShard.testMethods.add(testMethod)
        mostEmptyShard.time += testMethod.time
    }

    private val IArgs.platformName
        get() = if (this is IosArgs) "ios" else "android"

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
}
