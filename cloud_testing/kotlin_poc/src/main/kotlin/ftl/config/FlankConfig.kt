package ftl.config

import com.google.common.math.IntMath
import ftl.util.Utils
import ftl.util.Utils.fatalError
import java.math.RoundingMode

class FlankConfig(
        testShards: Int = 1,
        testRuns: Int = 1,
        val limitBreak: Boolean = false,
        var testShardChunks: List<List<String>> = emptyList(),
        var testMethodsAlwaysRun: Collection<String> = emptyList()
) {

    var testShards: Int = testShards
        set(value) {
            field = assertVmLimit(value)
        }

    var testRuns: Int = testRuns
        set(value) {
            field = assertVmLimit(value)
        }

    private fun assertVmLimit(value: Int): Int {
        if (value > 100 && !limitBreak) {
            Utils.fatalError("Shard count exceeds 100. Set limitBreak=true to enable large shards")
        }
        return value
    }

    fun calculateShards(gCloudConfig: GCloudConfig) {
        val testMethods = gCloudConfig.testMethods
        val allTestMethods = gCloudConfig.validTestNames

        val testShardMethods = if (testMethods.isNotEmpty()) {
            testMethods
        } else {
            allTestMethods
        }.distinct().toMutableList()
        testShardMethods.removeAll(testMethodsAlwaysRun)

        if (testShards < 1) testShards = 1

        var chunkSize = IntMath.divide(testShardMethods.size, testShards, RoundingMode.UP)
        // 1 method / 40 shard = 0. chunked(0) throws an exception.
        // default to running all tests in a single chunk if method count is less than shard count.
        if (chunkSize < 1) chunkSize = testShardMethods.size

        testShardChunks = testShardMethods.chunked(chunkSize).map { testMethodsAlwaysRun + it }

        // Ensure we don't create more VMs than requested. VM count per run should be <= testShards
        if (testShardChunks.size > testShards) {
            fatalError("Calculated chunks $testShardChunks is > requested $testShards testShards.")
        }
        if (testShardChunks.isEmpty()) fatalError("Failed to populate test shard chunks")
    }

}
