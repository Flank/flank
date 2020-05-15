package ftl.reports.api

import com.google.api.services.testing.model.TestExecution
import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.StepDimensionValueEntry
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.Timestamp
import ftl.reports.api.data.TestExecutionData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.reports.xml.model.JUnitTestCase
import ftl.reports.xml.model.JUnitTestSuite
import ftl.reports.xml.xmlPrettyWriter
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class CreateJUnitTestSuiteKtTest {

    @Before
    fun setUp() {
        mockkStatic(
            "ftl.reports.api.CreateTestSuitOverviewDataKt",
            "ftl.reports.api.CreateJUnitTestCaseKt"
        )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun createJUnitTestSuites() {
        // given
        every {
            any<TestExecutionData>().createTestSuitOverviewData()
        } returns TestSuiteOverviewData(1, 1, 1, 1, 1, 1.1, 1.1)

        val jUnitTestCase = JUnitTestCase(null, null, "1.1")

        every {
            createJUnitTestCases(any(), any(), any())
        } returns listOf(jUnitTestCase)

        val testExecutionDataList = listOf(
            TestExecutionData(
                testExecution = TestExecution().apply {
                    toolResultsStep = ToolResultsStep()
                },
                timestamp = Timestamp().apply {
                    seconds = 1
                    nanos = 1_000_000_00
                },
                step = Step().apply {
                    dimensionValue = listOf("Model", "Version", "Locale", "Orientation").map {
                        StepDimensionValueEntry().apply { key = it; value = it }
                    }
                },
                testCases = listOf(TestCase())
            )
        )

        // when
        val expected = JUnitTestSuite(
            name = "Model-Version-Locale-Orientation",
            tests = "1",
            failures = "1",
            flakes = 1,
            errors = "1",
            skipped = "1",
            time = "1.100",
            testcases = mutableListOf(jUnitTestCase),
            timestamp = "1970-01-01T00:00:01"
        )
        val actual = testExecutionDataList.createJUnitTestSuites().first()

        // then
        assertEquals(
            expected.let(xmlPrettyWriter::writeValueAsString),
            actual.let(xmlPrettyWriter::writeValueAsString)
        )
    }
}
