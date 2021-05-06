package ftl.reports.api

import com.google.api.services.toolresults.model.ListTestCasesResponse
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import com.google.testing.model.TestExecution
import com.google.testing.model.ToolResultsStep
import ftl.client.junit.TestExecutionData
import ftl.client.junit.createTestExecutionDataListAsync
import ftl.gc.GcToolResults
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.unmockkObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class CreateTestExecutionDataKtTest {

    @Before
    fun setUp() {
        mockkObject(GcToolResults)
    }

    @After
    fun tearDown() {
        unmockkObject(GcToolResults)
    }

    @Test
    fun `should create TestExecutionData list`() {
        // given
        val listTestCasesResponse = ListTestCasesResponse().apply {
            testCases = listOf(
                TestCase().apply {
                    startTime = Timestamp()
                }
            )
        }

        every { GcToolResults.getStepResult(any()) } returns Step()
        every { GcToolResults.listTestCases(any()) } returns listTestCasesResponse

        val testExecution = TestExecution().apply {
            toolResultsStep = ToolResultsStep()
        }
        val testExecutions = listOf(testExecution)

        val expected = listOf(
            TestExecutionData(
                testExecution = testExecution,
                step = Step(),
                testCases = listTestCasesResponse.testCases,
                timestamp = Timestamp()
            )
        )

        // when
        val actual = testExecutions.createTestExecutionDataListAsync()

        // then
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun `should not throw is ListTestCasesResponse testCases are null`() {
        // given
        every { GcToolResults.getStepResult(any()) } returns Step()
        every { GcToolResults.listAllTestCases(any()) } returns emptyList()

        val testExecution = TestExecution().apply {
            toolResultsStep = ToolResultsStep()
        }
        val testExecutions = listOf(testExecution)

        // when
        testExecutions.createTestExecutionDataListAsync()
    }
}
