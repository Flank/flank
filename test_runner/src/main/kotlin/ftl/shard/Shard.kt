package ftl.shard

import ftl.args.Chunk
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.xml.model.JUnitTestResult
import ftl.run.exception.FlankConfigurationError
import ftl.util.FlankTestMethod
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

// take in the XML with timing info then return list of shards based on the amount of shards to use
fun createShardsByShardCount(
    testsToRun: List<FlankTestMethod>,
    oldTestResult: JUnitTestResult,
    args: IArgs,
    forcedShardCount: Int = -1
): List<TestShard> {
    if (forcedShardCount < -1 || forcedShardCount == 0) throw FlankConfigurationError("Invalid forcedShardCount value $forcedShardCount")
    val maxShards = maxShards(args.maxTestShards, forcedShardCount)

    val previousMethodDurations = createTestMethodDurationMap(oldTestResult, args)
    val testCases = createTestCases(testsToRun, previousMethodDurations, args)
        .sortedByDescending(TestMethod::time) // We want to iterate over testcase going from slowest to fastest

    val testCount = getNumberOfNotIgnoredTestCases(testCases)

    // If maxShards is infinite or we have more shards than tests, let's match it
    val shardsCount = matchNumberOfShardsWithTestCount(maxShards, testCount)

    // Create the list of shards we will return
    if (shardsCount <= 0) throw FlankConfigurationError(
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

private fun maxShards(maxShardsCount: Int, forcedShardCount: Int) =
    if (forcedShardCount == -1) maxShardsCount else forcedShardCount

private fun getNumberOfNotIgnoredTestCases(testCases: List<TestMethod>): Int {
    // Ugly hotfix for case when all test cases are annotated with @Ignore
    // we need to filter them because they have time == 0.0 which cause empty shards creation, few lines later
    // and we don't need additional shards for ignored tests.
    return if (testCases.isEmpty()) 0 else testCases.filter { it.time > IGNORE_TEST_TIME }
        .takeIf { it.isNotEmpty() }?.size ?: 1
}

private fun matchNumberOfShardsWithTestCount(maxShards: Int, testCount: Int) =
    if (maxShards == -1 || maxShards > testCount) testCount else maxShards

private val IArgs.platformName get() = if (this is IosArgs) "ios" else "android"

private fun printShardsInfo(shards: List<TestShard>) {
    println("  Shard times: " + shards.joinToString(", ") { "${it.time.roundToInt()}s" } + "\n")
}

private fun createShardsForTestCases(
    testCases: List<TestMethod>,
    shardsCount: Int
): List<TestShard> = testCases.fold(
    initial = List(shardsCount) { TestShard(0.0, mutableListOf()) }
) { shards, testMethod ->
    shards.apply {
        first().apply {
            testMethods += testMethod
            time += testMethod.time
        }
    }.sortedBy(TestShard::time)
}
