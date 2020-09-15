package ftl.shard

import com.google.common.truth.Truth.assertThat
import ftl.args.IArgs
import ftl.test.util.FlankTestRunner
import ftl.util.FlankTestMethod
import io.mockk.every
import io.mockk.mockk
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
        val args = mockk<IArgs> {
            every { useAverageTestTimeForNewTests } returns true
            every { defaultTestTime } returns 10.0
            every { defaultClassTestTime } returns 12.0
        }

        // when
        val createdTestCases = createTestCases(ignoredTestMethods, mapOf(), args)

        // then
        createdTestCases.forEach {
            assertThat(it.time).isEqualTo(expectedTestTime)
        }
    }

    @Test
    fun `should create test cases with provided default time for test methods not found in map`() {
        // given
        val expectedTestTime = 10.0
        val args = mockk<IArgs> {
            every { useAverageTestTimeForNewTests } returns false
            every { defaultTestTime } returns 10.0
            every { defaultClassTestTime } returns 12.0
        }

        // when
        val createdTestCases = createTestCases(testMethods, mapOf(), args)

        // then
        createdTestCases.forEach {
            assertThat(it.time).isEqualTo(expectedTestTime)
        }
    }

    @Test
    fun `should create test cases with average time of previous methods for test methods not found in map`() {
        // given
        val previousTestMethods = mapOf(
            "oldA" to 12.0,
            "oldB" to 6.0,
            "oldC" to 18.0,
            "oldD" to 12.0,
        )
        val expectedTestTime = previousTestMethods.values.average()
        val args = mockk<IArgs> {
            every { useAverageTestTimeForNewTests } returns true
            every { defaultTestTime } returns 10.0
            every { defaultClassTestTime } returns 12.0
        }

        // when
        val createdTestCases = createTestCases(testMethods, previousTestMethods, args)

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
        val args = mockk<IArgs> {
            every { useAverageTestTimeForNewTests } returns true
            every { defaultTestTime } returns 10.0
            every { defaultClassTestTime } returns 12.0
        }

        // when
        val createdTestCases = createTestCases(testMethods, previousMapDuration, args)

        // then
        createdTestCases.forEach {
            assertThat(it.time).isEqualTo(previousTestTimeSeconds)
        }
    }
}
