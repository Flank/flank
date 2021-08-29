package ftl.domain.junit

import com.google.common.truth.Truth.assertThat
import ftl.api.JUnitTest
import org.junit.Test

class StackTraceRemoverTest {

    val testcases = mutableListOf(
        JUnitTest.Case("name", "class", "time", failures = listOf("fail"), errors = listOf("error")),
        JUnitTest.Case("name2", "class2", "time2", failures = listOf("fail2"), errors = listOf("error2")),
    )

    @Test
    fun `Should remove stack traces for flaky tests`() {
        // given
        val testcases = mutableListOf(
            JUnitTest.Case("name", "class", "time", failures = listOf("fail"), errors = listOf("error"), flaky = true),
            JUnitTest.Case("name2", "class2", "time2", failures = listOf("fail2"), errors = listOf("error2")),
        )
        val junitTestResult = testResultsFor(testcases, flaky = true)

        // when
        val actual = junitTestResult.removeStackTraces()

        // then
        actual.testsuites?.forEach { suite ->
            assertThat(suite.testcases?.all { testCase -> testCase.failures == null }).isTrue()
            assertThat(suite.testcases?.all { testCase -> testCase.errors == null }).isTrue()
            assertThat(suite.testcases?.any { testCase -> testCase.flaky == true }).isTrue()
        }
    }

    @Test
    fun `Should not remove stack traces for not flaky test`() {
        // given
        val junitTestResult = testResultsFor(testcases, flaky = false)

        // when
        val actual = junitTestResult.removeStackTraces()

        // then
        actual.testsuites?.forEach { suite ->
            assertThat(suite.testcases?.all { testCase -> testCase.failures == null }).isFalse()
            assertThat(suite.testcases?.all { testCase -> testCase.errors == null }).isFalse()
        }
    }

    private fun testResultsFor(testCases: MutableList<JUnitTest.Case>, flaky: Boolean) = JUnitTest.Result(
        testsuites = mutableListOf(
            JUnitTest.Suite(
                "",
                "-1",
                "-1",
                -1,
                "-1",
                "-1",
                "-1",
                "-1",
                "-1",
                "-1",
                testCases.onEach { it.apply { this.flaky = flaky } },
                null,
                null,
                null
            )
        )
    )
}
