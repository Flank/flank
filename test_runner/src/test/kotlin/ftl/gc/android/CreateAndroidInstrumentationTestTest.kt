package ftl.gc.android

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.FileReference
import ftl.args.IArgs
import ftl.args.ShardChunks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UtilsKtTest {

    @Test
    fun `setupTestTargets should setup testTargets`() {
        // given
        val testShards: ShardChunks = emptyList()

        // when
        val actual = AndroidInstrumentationTest()
            .setupTestTargets(
                disableSharding = true,
                testShards = testShards,
                numUniformShards = null,
                keepTestTargetsEmpty = false
            )
            .testTargets

        // then
        assertNotNull(actual)
    }

    @Test
    fun `setupTestTargets should setup uniformSharding`() {
        // given
        val expectedTestTargets = emptyList<String>()
        val testShards: ShardChunks = listOf(expectedTestTargets)

        // when
        val actual = AndroidInstrumentationTest()
            .setTestApk(FileReference().setGcsPath("testApk"))
            .setupTestTargets(
                disableSharding = false,
                testShards = testShards,
                numUniformShards = IArgs.AVAILABLE_SHARD_COUNT_RANGE.last,
                keepTestTargetsEmpty = false
            )

        // then
        assertEquals(0, actual.shardingOption.uniformSharding.numShards)
        assertEquals(expectedTestTargets, actual.testTargets)
    }

    @Test
    fun `setupTestTargets should setup manualSharding`() {
        // given
        val shardChunks: ShardChunks = listOf(emptyList(), emptyList())

        // when
        val actual = AndroidInstrumentationTest()
            .setupTestTargets(
                disableSharding = false,
                testShards = shardChunks,
                numUniformShards = null,
                keepTestTargetsEmpty = false
            )
            .shardingOption
            .manualSharding
            .testTargetsForShard

        // then
        assertEquals(shardChunks.size, actual.size)
    }
}
