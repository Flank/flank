package ftl.shard

import com.google.common.truth.Truth.assertThat
import ftl.api.JUnitTest
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.run.exception.FlankConfigurationError
import ftl.test.util.FlankTestRunner
import ftl.util.FlankTestMethod
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.file.Paths
import kotlin.system.measureTimeMillis

@RunWith(FlankTestRunner::class)
class ShardTest {

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun oneTestPerShard() {
        val reRunTestsToRun = listOfFlankTestMethod("a", "b", "c", "d", "e", "f", "g")
        val suite = sample()

        val result = createShardsByShardCount(reRunTestsToRun, suite, mockArgs(100))

        assertThat(result.size).isEqualTo(7)
        result.forEach {
            assertThat(it.testMethods.size).isEqualTo(1)
        }
    }

    @Test
    fun sampleTest() {
        val reRunTestsToRun = listOfFlankTestMethod("a/a", "b/b", "c/c", "d/d", "e/e", "f/f", "g/g")
        val suite = sample()
        val result = createShardsByShardCount(reRunTestsToRun, suite, mockArgs(3))

        assertThat(result.size).isEqualTo(3)
        result.forEach {
            assertThat(it.testMethods).isNotEmpty()
        }

        assertThat(result.sumByDouble { it.time }).isEqualTo(16.5)

        val testNames = mutableListOf<String>()
        result.forEach { shard ->
            shard.testMethods.forEach {
                testNames.add(it.name)
            }
        }

        testNames.sort()
        assertThat(testNames).isEqualTo(listOf("a/a", "b/b", "c/c", "d/d", "e/e", "f/f", "g/g"))
    }

    @Test
    fun firstRun() {
        val testsToRun = listOfFlankTestMethod("a", "b", "c")
        val result = createShardsByShardCount(testsToRun, JUnitTest.Result(null), mockArgs(2))

        assertThat(result.size).isEqualTo(2)
        assertThat(result.sumByDouble { it.time }).isEqualTo(3 * DEFAULT_TEST_TIME_SEC)

        val ordered = result.sortedBy { it.testMethods.size }
        assertThat(ordered[0].testMethods.size).isEqualTo(1)
        assertThat(ordered[1].testMethods.size).isEqualTo(2)
    }

    @Test
    fun mixedNewAndOld() {
        val testsToRun = listOfFlankTestMethod("a/a", "b/b", "c/c", "w", "y", "z")
        val result = createShardsByShardCount(testsToRun, sample(), mockArgs(4))
        assertThat(result.size).isEqualTo(4)
        assertThat(result.sumByDouble { it.time }).isEqualTo(7.0 + 3 * DEFAULT_TEST_TIME_SEC)

        val ordered = result.sortedBy { it.testMethods.size }
        // Expect a/a, b/b, c/c to be in one shard, and w, y, z to each be in their own shards.
        assertThat(ordered[0].testMethods.size).isEqualTo(1)
        assertThat(ordered[1].testMethods.size).isEqualTo(1)
        assertThat(ordered[2].testMethods.size).isEqualTo(1)
        assertThat(ordered[3].testMethods.size).isEqualTo(3)
    }

    @Test
    fun `performance calculateShardsByTime`() {
        val testsToRun = mutableListOf<FlankTestMethod>()
        repeat(1_000_000) { index -> testsToRun.add(FlankTestMethod("$index/$index")) }

        val arg = AndroidArgs.loadOrDefault(Paths.get("just-be-here"), null).run {
            copy(commonArgs = commonArgs.copy(maxTestShards = 4))
        }

        // The test occasionally fails on GH Actions' machines, we need to retry to exclude flakiness
        val maxAttempts = 5
        val maxMs = 5000

        var attempt = 0
        var ms = Long.MAX_VALUE

        while (attempt++ < maxAttempts && ms >= maxMs) {
            ms = measureTimeMillis {
                createShardsByShardCount(testsToRun, JUnitTest.Result(null), arg)
            }
            println("Shards calculated in $ms ms, attempt: $attempt")
        }
        assertThat(ms).isLessThan(maxMs)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `createShardsByShardCount throws on forcedShardCount = 0`() {
        createShardsByShardCount(
            listOf(),
            sample(),
            mockArgs(-1, 7),
            0
        )
    }

    private fun newSuite(testCases: MutableList<JUnitTest.Case>): JUnitTest.Result {
        return JUnitTest.Result(
            mutableListOf(
                JUnitTest.Suite(
                    "",
                    "3",
                    "0",
                    -1,
                    "0",
                    "0",
                    "12.032",
                    "2019-07-27T08:15:04",
                    "localhost",
                    "matrix-1234_execution-asdf",
                    testCases,
                    null,
                    null,
                    null
                )
            )
        )
    }

    private fun shardCountByTime(shardTime: Int): Int {
        val testsToRun = listOfFlankTestMethod("a/a", "b/b", "c/c")
        val testCases = mutableListOf(
            JUnitTest.Case("a", "a", "0.001"),
            JUnitTest.Case("b", "b", "0.0"),
            JUnitTest.Case("c", "c", "0.0")
        )

        val oldTestResult = newSuite(testCases)

        return shardCountByTime(
            testsToRun,
            oldTestResult,
            mockArgs(-1, shardTime)
        )
    }

    @Test(expected = FlankConfigurationError::class)
    fun `shardCountByTime throws on invalid shard time -3`() {
        shardCountByTime(-3)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `shardCountByTime throws on invalid shard time -2`() {
        shardCountByTime(-2)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `shardCountByTime throws on invalid shard time 0`() {
        shardCountByTime(0)
    }

    @Test
    fun `shardCountByTime shards valid times`() {
        assertThat(shardCountByTime(-1)).isEqualTo(-1)
        assertThat(shardCountByTime(1)).isEqualTo(1)
        assertThat(shardCountByTime(2)).isEqualTo(1)
        assertThat(shardCountByTime(3)).isEqualTo(1)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should terminate with exit status == 3 test targets is 0 and maxTestShards == -1`() {
        createShardsByShardCount(emptyList(), JUnitTest.Result(mutableListOf()), mockArgs(-1))
    }

    @Test
    fun `tests annotated with @Ignore should have time 0 and do not hav impact on sharding`() {
        val androidMockedArgs = mockk<IosArgs>(relaxed = true)
        every { androidMockedArgs.maxTestShards } returns 2
        every { androidMockedArgs.shardTime } returns 10

        val testsToRun = listOf(
            FlankTestMethod("a/a", ignored = true),
            FlankTestMethod("b/b"),
            FlankTestMethod("c/c")
        )

        val oldTestResult = newSuite(
            mutableListOf(
                JUnitTest.Case("a", "a", "5.0"),
                JUnitTest.Case("b", "b", "10.0"),
                JUnitTest.Case("c", "c", "10.0")
            )
        )

        val shardCount = shardCountByTime(testsToRun, oldTestResult, androidMockedArgs)
        assertEquals(2, shardCount)

        val shards = createShardsByShardCount(testsToRun, oldTestResult, androidMockedArgs, shardCount)
        assertEquals(2, shards.size)
        assertTrue(shards.flatMap { it.testMethods }.map { it.time }.filter { it == 0.0 }.count() == 1)
        shards.forEach { assertEquals(10.0, it.time, 0.0) }
    }

    @Test
    fun `tests annotated with @Ignore should not produce additional shards`() {
        val androidMockedArgs = mockk<IosArgs>(relaxed = true)
        every { androidMockedArgs.maxTestShards } returns IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last
        every { androidMockedArgs.shardTime } returns -1

        val testsToRun = listOf(
            FlankTestMethod("a/a", ignored = true),
            FlankTestMethod("b/b", ignored = true),
            FlankTestMethod("c/c", ignored = true)
        )

        val oldTestResult = newSuite(mutableListOf())

        val shardCount = shardCountByTime(testsToRun, oldTestResult, androidMockedArgs)
        assertEquals(-1, shardCount)

        val shards = createShardsByShardCount(testsToRun, oldTestResult, androidMockedArgs, shardCount)
        assertEquals(1, shards.size)
    }
}
