package ftl.shard

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class ShardCountTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `createShardsByShardTime workingSample`() {
        val testsToRun = listOfFlankTestMethod("a/a", "b/b", "c/c", "d/d", "e/e", "f/f", "g/g")
        val suite = sample()
        val result = shardCountByTime(testsToRun, suite, mockArgs(20, 7))

        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `createShardsByShardTime countShouldNeverBeHigherThanMaxAvailable`() {
        val testsToRun = listOfFlankTestMethod("a/a", "b/b", "c/c", "d/d", "e/e", "f/f", "g/g")
        val suite = sample()
        val result = shardCountByTime(testsToRun, suite, mockArgs(2, 7))

        assertThat(result).isEqualTo(2)
    }

    @Test
    fun `createShardsByShardTime unlimitedShardsShouldReturnTheRightAmount`() {
        val testsToRun = listOfFlankTestMethod("a/a", "b/b", "c/c", "d/d", "e/e", "f/f", "g/g")
        val suite = sample()
        val result = shardCountByTime(testsToRun, suite, mockArgs(-1, 7))

        assertThat(result).isEqualTo(7)
    }

    @Test
    fun `createShardsByShardTime uncachedTestResultsUseDefaultTime`() {
        val testsToRun = listOfFlankTestMethod("h/h", "i/i", "j/j")
        val suite = sample()
        val result = shardCountByTime(
            testsToRun,
            suite,
            mockArgs(maxTestShards = -1, shardTime = DEFAULT_TEST_TIME_SEC.toInt())
        )

        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `createShardsByShardTime mixedCachedAndUncachedTestResultsUseDefaultTime`() {
        // Test "a/a" is hard-coded to have 1.0 second run time in test suite results.
        val testsToRun = listOfFlankTestMethod("a/a", "i/i", "j/j")
        val suite = sample()
        val result = shardCountByTime(
            testsToRun,
            suite,
            mockArgs(maxTestShards = -1, shardTime = DEFAULT_TEST_TIME_SEC.toInt() + 1)
        )

        assertThat(result).isEqualTo(3)
    }

    @Test
    fun `createShardsByShardTime uncachedTestResultsAllInOneShard`() {
        val testsToRun = listOfFlankTestMethod("i/i", "j/j")
        val suite = sample()
        val result = shardCountByTime(
            testsToRun,
            suite,
            mockArgs(maxTestShards = -1, shardTime = (DEFAULT_TEST_TIME_SEC * 2).toInt())
        )

        assertThat(result).isEqualTo(1)
    }
}
