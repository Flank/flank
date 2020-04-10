package ftl.gc

import com.google.api.services.testing.model.AndroidInstrumentationTest
import ftl.args.AndroidArgs
import ftl.args.ShardChunks
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class UtilsKtTest {

    @Test
    fun `setupTestTargets should setup testTargets`() {
        // given
        val args = mockk<AndroidArgs> {
            every { disableSharding } returns true
        }
        val testShards: ShardChunks = emptyList()

        // when
        val actual = AndroidInstrumentationTest().setupTestTargets(args, testShards)
            .testTargets

        // then
        assertNotNull(actual)
    }

    @Test
    fun `setupTestTargets should setup uniformSharding`() {
        // given
        val expectedNumShards = 50
        val expectedTestTargets = emptyList<String>()
        val args = mockk<AndroidArgs> {
            every { disableSharding } returns false
            every { numUniformShards } returns expectedNumShards
        }
        val testShards: ShardChunks = listOf(expectedTestTargets)

        // when
        val actual = AndroidInstrumentationTest().setupTestTargets(args, testShards)

        // then
        assertEquals(expectedNumShards, actual.shardingOption.uniformSharding.numShards)
        assertEquals(expectedTestTargets, actual.testTargets)
    }

    @Test
    fun `setupTestTargets should setup manualSharding`() {
        // given
        val shardChunks: ShardChunks = listOf(emptyList(), emptyList())
        val args = mockk<AndroidArgs> {
            every { disableSharding } returns false
            every { numUniformShards } returns null
        }

        // when
        val actual = AndroidInstrumentationTest().setupTestTargets(args, shardChunks)
            .shardingOption
            .manualSharding
            .testTargetsForShard

        // then
        assertEquals(shardChunks.size, actual.size)
    }
}
