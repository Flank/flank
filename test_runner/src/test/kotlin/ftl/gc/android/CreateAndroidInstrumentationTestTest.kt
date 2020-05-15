package ftl.gc.android

import com.google.api.services.testing.model.AndroidInstrumentationTest
import com.google.api.services.testing.model.FileReference
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
                numUniformShards = null
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
                numUniformShards = 50
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
                numUniformShards = null
            )
            .shardingOption
            .manualSharding
            .testTargetsForShard

        // then
        assertEquals(shardChunks.size, actual.size)
    }
}
