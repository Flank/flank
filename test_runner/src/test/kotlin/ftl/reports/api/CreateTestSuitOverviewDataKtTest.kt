package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestExecutionStep
import com.google.api.services.toolresults.model.TestSuiteOverview
import com.google.api.services.toolresults.model.Timestamp
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateTestSuitOverviewDataKtTest {

    @Test
    fun createTestSuitOverviewData() {
        // given
        val testExecutionData = TestExecutionData(
            testExecution = TestExecution(),
            timestamp = Timestamp(),
            testCases = listOf(
                TestCase(), // status success
                TestCase().apply { status = "error" },
                TestCase().apply { status = "failed" },
                TestCase().apply { flaky = true }
            ),
            step = Step().apply {
                testExecutionStep = TestExecutionStep().apply {
                    testSuiteOverviews = listOf(
                        TestSuiteOverview().apply {
                            skippedCount = 1
                        }
                    )
                }
            }
        )

        // when
        val expected = TestSuiteOverviewData(
            total = 5,
            errors = 1,
            failures = 1,
            skipped = 1,
            flakes = 1
        )
        val actual = testExecutionData.createTestSuitOverviewData()

        // then
        assertEquals(expected, actual)
    }
}
