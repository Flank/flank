package ftl.shard

import com.google.common.truth.Truth.assertThat
import ftl.test.util.FlankTestRunner
import ftl.util.FlankTestMethod
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
internal class TestCasesCreatorTest {

    private val testMethods = listOf(
        FlankTestMethod("a"),
        FlankTestMethod("b"),
        FlankTestMethod("c"),
        FlankTestMethod("d")
    )

    @Test
    fun `should create test cases with ignored time for ignored test methods`() {
        // given
        val ignoredTestMethods = testMethods.map { it.copy(ignored = true) }
        val expectedTestTime = IGNORE_TEST_TIME

        // when
        val createdTestCases = TestCasesCreator.createTestCases(ignoredTestMethods, mapOf())

        // then
        createdTestCases.forEach {
            assertThat(it.time).isEqualTo(expectedTestTime)
        }
    }

    @Test
    fun `should create test cases with default time for test methods not found in map`() {
        // given
        val expectedTestTime = DEFAULT_TEST_TIME_SEC

        // when
        val createdTestCases = TestCasesCreator.createTestCases(testMethods, mapOf())

        // then
        createdTestCases.forEach {
            assertThat(it.time).isEqualTo(expectedTestTime)
        }
    }

    @Test
    fun `should return sum of previous total time when all tests are found in previous map durations`() {
        // given
        val previousTestTimeSeconds = 12.0
        val previousMapDuration = testMethods.associate { it.testName to previousTestTimeSeconds }

        // when
        val createdTestCases = TestCasesCreator.createTestCases(testMethods, previousMapDuration)

        // then
        createdTestCases.forEach {
            assertThat(it.time).isEqualTo(previousTestTimeSeconds)
        }
    }
}