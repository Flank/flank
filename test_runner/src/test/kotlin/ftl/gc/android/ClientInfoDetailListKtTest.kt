package ftl.gc.android

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.FileReference
import ftl.api.ShardChunks
import ftl.args.IArgs
import ftl.client.google.run.android.setupTestTargets
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ClientInfoDetailListKtTest {

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
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
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
                numUniformShards = IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last,
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
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
                keepTestTargetsEmpty = false,
                testTargetsForShard = emptyList()
            )
            .shardingOption
            .manualSharding
            .testTargetsForShard

        // then
        assertEquals(shardChunks.size, actual.size)
    }
}
