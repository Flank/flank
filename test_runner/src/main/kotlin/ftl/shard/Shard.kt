package ftl.shard

data class TestMethod(
    val name: String,
    val time: Double
)

data class TestShard(
    val time: Double,
    val testMethods: MutableList<TestMethod>
)

data class TestShards(
    val shards: MutableList<TestShard> = mutableListOf()
)

object Shard {

    /**
     * Build shard by removing remaining methods that total to targetShardDuration
     * At least one method per shard will always be returned, regardless of targetShardDuration.
     *
     * remainingMethods must be sorted in order of fastest execution time to slowest.
     * remainingMethods.sortBy { it.time }
     *
     * Based on createConfigurableShard from Flank Java
     * https://github.com/TestArmada/flank/blob/ceda6d2c3d9eb2a366f19b826e04289cd24bddf3/Flank/src/main/java/com/walmart/otto/shards/ShardCreator.java#L99
     */
    fun build(remainingMethods: MutableList<TestMethod>, targetShardDuration: Double): TestShard {
        var timeBudget = targetShardDuration
        var shardTime = 0.0

        val testMethods = remainingMethods.iterator()
        val shardTests = mutableListOf<TestMethod>()

        while (testMethods.hasNext()) {
            val test = testMethods.next()
            val testWithinBudget = timeBudget - test.time >= 0

            if (testWithinBudget) {
                timeBudget -= test.time

                shardTime += test.time
                shardTests.add(test)
                testMethods.remove()

                continue
            }

            val noTestsAdded = timeBudget == targetShardDuration
            val testOverBudget = test.time >= timeBudget

            if (noTestsAdded && testOverBudget) {
                // Always add at least 1 test per shard regardless of budget
                shardTime += test.time
                shardTests.add(test)
                testMethods.remove()

                return TestShard(shardTime, shardTests)
            }
        }
        return TestShard(shardTime, shardTests)
    }
}
