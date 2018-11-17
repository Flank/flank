package ftl.shard

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

object Shard {

    // take in the XML with timing info then return list of shards
    fun calculateShardsByTime(
        runningTests: List<String>,
        testResult: JUnitTestResult,
        maxShards: Int
    ): List<TestShard> {

        val junitMap = mutableMapOf<String, Double>()

        // Create a map with information from previous junit run
        testResult.testsuites?.forEach { testsuite ->
            testsuite.testcases?.forEach { testcase ->
                val key = "${testsuite.name}${testcase.classname}#${testcase.name}".trim()
                junitMap[key] = testcase.time.toDouble()
            }
        }

        var cacheMiss = 0
        val testcases = mutableListOf<TestMethod>()
        runningTests.forEach {
            // junitMap doesn't include `class `, we remove it to search in the map
            val key = it.replaceFirst("class ", "")
            val previousTime = junitMap[key]
            val time = previousTime ?: 10.0

            if (previousTime == null) {
                cacheMiss += 1
            }

            testcases.add(TestMethod(it, time))
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

        val allTests = runningTests.size
        val cacheHit = allTests - cacheMiss
        val cachePercent = cacheHit / allTests * 100
        println("  Smart Flank cache hit: $cachePercent% ($cacheHit / $allTests)")
        println("  Shard times: " + shards.map { it.time.roundToInt() }.joinToString(", ") + "\n")

        return shards
    }
}
