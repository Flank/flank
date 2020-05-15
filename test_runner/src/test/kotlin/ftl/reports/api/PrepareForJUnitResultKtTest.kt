package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.toolresults.model.MultiStep
import com.google.api.services.toolresults.model.StackTrace
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import ftl.reports.api.data.TestExecutionData
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrepareForJUnitResultKtTest {

    @Test
    fun prepareForJUnitResult() {
        // given
        val primaryTestCases = listOf(
            TestCase().apply {
                startTime = Timestamp().apply {
                    seconds = 1
                }
            }
        )
        val primaryStep = Step().apply {
            stepId = "1"
        }

        val secondaryTestCases = listOf(
            TestCase().apply {
                startTime = Timestamp().apply {
                    seconds = 2
                }
                stackTraces = listOf(
                    StackTrace().apply {
                        exception = "exception"
                    }
                )
            }
        )

        val secondaryStep = Step().apply {
            multiStep = MultiStep().apply {
                primaryStepId = "1"
                multistepNumber = 1
            }
        }

        val testExecutionDataList = listOf(
            TestExecutionData(
                testExecution = TestExecution(),
                timestamp = Timestamp(),
                testCases = secondaryTestCases,
                step = secondaryStep
            ),
            TestExecutionData(
                testExecution = TestExecution(),
                timestamp = Timestamp(),
                testCases = primaryTestCases,
                step = primaryStep
            )
        )

        // when
        assertFalse(secondaryTestCases.first().flaky)
        val expected = listOf(
            TestExecutionData(
                testExecution = TestExecution(),
                testCases = secondaryTestCases,
                step = primaryStep,
                timestamp = Timestamp()
            )
        )
        val actual = testExecutionDataList.prepareForJUnitResult()

        // then
        assertArrayEquals(expected.toTypedArray(), actual.toTypedArray())
        assertTrue(secondaryTestCases.first().flaky)
    }
}
