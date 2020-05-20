package ftl.shard

import ftl.test.util.FlankTestRunner
import ftl.util.FlankTestMethod
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
internal class TestMethodDurationTest {

    private val testMethods = listOf(
        FlankTestMethod("a"),
        FlankTestMethod("b"),
        FlankTestMethod("c"),
        FlankTestMethod("d")
    )

    @Test
    fun `should return 0 total time when all tests are ignored`() {
        // given
        val ignoredTestMethods = testMethods.map { it.copy(ignored = true) }
        val expectedTestTime = 0.0

        // when
        val calculatedTotalTestTime = TestMethodDuration.testTotalTime(ignoredTestMethods, mapOf())

        // then
        assertThat(calculatedTotalTestTime).isEqualTo(expectedTestTime)
    }

    @Test
    fun `should return correct total time when all tests are not found in previous map durations`() {
        // given
        val expectedTestTime = testMethods.size * DEFAULT_TEST_TIME_SEC

        // when
        val calculatedTotalTestTime = TestMethodDuration.testTotalTime(testMethods, mapOf())

        // then
        assertThat(calculatedTotalTestTime).isEqualTo(expectedTestTime)
    }

    @Test
    fun `should return sum of previous total time when all tests are found in previous map durations`() {
        // given
        val previousTestTimeSeconds = 12.0
        val previousMapDuration = testMethods.associate { it.testName to previousTestTimeSeconds }
        val expectedTestTime = testMethods.size * previousTestTimeSeconds

        // when
        val calculatedTotalTestTime = TestMethodDuration.testTotalTime(testMethods, previousMapDuration)

        // then
        assertThat(calculatedTotalTestTime).isEqualTo(expectedTestTime)
    }
}