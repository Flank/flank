package ftl.shard

import com.google.common.truth.Truth.assertThat
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestResult
import ftl.reports.xml.model.JUnitTestSuite
import ftl.test.util.FlankTestRunner
import java.util.concurrent.TimeUnit
import kotlin.system.measureNanoTime
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock

@RunWith(FlankTestRunner::class)
class ShardTest {

    private fun sample(): JUnitTestResult {

        val testCases = mutableListOf(
            JUnitTestCase("a", "a", "1.0"),
            JUnitTestCase("b", "b", "2.0"),
            JUnitTestCase("c", "c", "4.0"),
            JUnitTestCase("d", "d", "6.0"),
            JUnitTestCase("e", "e", "0.5"),
            JUnitTestCase("f", "f", "2.0"),
            JUnitTestCase("g", "g", "1.0")
        )

        val suite1 = JUnitTestSuite("", "-1", "-1", "-1", "-1", "-1", "-1", "-1", testCases, null, null, null)
        val suite2 = JUnitTestSuite("", "-1", "-1", "-1", "-1", "-1", "-1", "-1", mutableListOf(), null, null, null)

        return JUnitTestResult(mutableListOf(suite1, suite2))
    }

    private fun mockArgs(testShards: Int): IArgs {
        val mockArgs = mock(IosArgs::class.java)
        `when`(mockArgs.testShards).thenReturn(testShards)
        return mockArgs
    }

    @Test
    fun oneTestPerShard() {
        val reRunTestsToRun = listOf("a", "b", "c", "d", "e", "f", "g")
        val suite = sample()

        val result = Shard.calculateShardsByTime(reRunTestsToRun, suite, mockArgs(100))

        assertThat(result.size).isEqualTo(7)
        result.forEach {
            assertThat(it.testMethods.size).isEqualTo(1)
        }
    }

    @Test
    fun sampleTest() {
        val reRunTestsToRun = listOf("a/a", "b/b", "c/c", "d/d", "e/e", "f/f", "g/g")
        val suite = sample()
        val result = Shard.calculateShardsByTime(reRunTestsToRun, suite, mockArgs(3))

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
        val testsToRun = listOf("a", "b", "c")
        val result = Shard.calculateShardsByTime(testsToRun, JUnitTestResult(null), mockArgs(2))

        assertThat(result.size).isEqualTo(2)
        assertThat(result.sumByDouble { it.time }).isEqualTo(30.0)

        val ordered = result.sortedBy { it.testMethods.size }
        assertThat(ordered[0].testMethods.size).isEqualTo(1)
        assertThat(ordered[1].testMethods.size).isEqualTo(2)
    }

    @Test
    fun mixedNewAndOld() {
        val testsToRun = listOf("a/a", "b/b", "c/c", "w", "y", "z")
        val result = Shard.calculateShardsByTime(testsToRun, sample(), mockArgs(4))
        assertThat(result.size).isEqualTo(4)
        assertThat(result.sumByDouble { it.time }).isEqualTo(37.0)

        val ordered = result.sortedBy { it.testMethods.size }
        assertThat(ordered[0].testMethods.size).isEqualTo(1)
        assertThat(ordered[1].testMethods.size).isEqualTo(1)
        assertThat(ordered[2].testMethods.size).isEqualTo(1)
        assertThat(ordered[3].testMethods.size).isEqualTo(3)
    }

    @Test
    fun performance_calculateShardsByTime() {
        val testsToRun = mutableListOf<String>()
        repeat(1_000_000) { index -> testsToRun.add("$index/$index") }

        val nano = measureNanoTime {
            Shard.calculateShardsByTime(testsToRun, JUnitTestResult(null), mockArgs(4))
        }

        val ms = TimeUnit.NANOSECONDS.toMillis(nano)
        println("Shards calculated in $ms ms")
        assertThat(ms).isLessThan(5000)
    }
}
