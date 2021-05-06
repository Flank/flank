package ftl.reports.api

import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestExecutionStep
import com.google.api.services.toolresults.model.TestSuiteOverview
import com.google.api.services.toolresults.model.Timestamp
import com.google.testing.model.TestExecution
import ftl.client.junit.TestExecutionData
import ftl.client.junit.createTestSuiteOverviewData
import ftl.client.junit.flaky
import ftl.reports.api.data.TestSuiteOverviewData
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateTestSuiteOverviewDataKtTest {

    @Test
    fun createTestSuiteOverviewData() {
        // given
        val testExecutionData = TestExecutionData(
            testExecution = TestExecution(),
            timestamp = Timestamp(),
            testCases = listOf(
                TestCase(), // status success
                TestCase().apply { status = "error" },
                TestCase().apply { status = "failed" },
                TestCase().apply { flaky = true }
            ).apply {
                forEach {
                    it.elapsedTime = Duration().apply {
                        seconds = 1
                    }
                }
            },
            step = Step().apply {
                testExecutionStep = TestExecutionStep().apply {
                    testSuiteOverviews = listOf(
                        TestSuiteOverview().apply {
                            skippedCount = 1
                            elapsedTime = Duration().apply {
                                seconds = 8
                            }
                        }
                    )
                }
            }
        )

        // when
        val expected = TestSuiteOverviewData(
            total = 4,
            errors = 1,
            failures = 1,
            skipped = 1,
            flakes = 1,
            elapsedTime = 8.0,
            overheadTime = 1.0
        )
        val actual = testExecutionData.createTestSuiteOverviewData()

        // then
        assertEquals(expected, actual)
    }
}
