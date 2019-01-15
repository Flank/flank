package ftl.shard

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
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

    // take in the XML with timing info then return list of shards
    fun calculateShardsByTime(
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
