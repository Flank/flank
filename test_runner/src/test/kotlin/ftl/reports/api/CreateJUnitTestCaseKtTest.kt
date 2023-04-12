package ftl.reports.api

import com.google.api.services.toolresults.model.Duration
import com.google.api.services.toolresults.model.StackTrace
import com.google.api.services.toolresults.model.TestCase
import com.google.api.services.toolresults.model.TestCaseReference
import com.google.testing.model.ToolResultsStep
import ftl.api.JUnitTest
import ftl.client.junit.createJUnitTestCases
import ftl.client.junit.flaky
import ftl.client.junit.xmlPrettyWriter
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class CreateJUnitTestCaseKtTest {

    @Test
    fun createJUnitTestCases() {
        // given
        val testCases = listOf(
            TestCase().setup(
                // Success
            ),
            TestCase().setup(
                status = "skipped"
            ),
            TestCase().setup(
                status = "error",
                hasException = true
            ),
            TestCase().setup(
                status = "failed",
                hasException = true
            ),
            TestCase().setup(
                status = "failed",
                isFlaky = true,
                hasException = true
            )
        )
        val toolResultsStep = ToolResultsStep().apply {
            projectId = "projectId"
            historyId = "historyId"
            executionId = "executionId"
            stepId = "stepId"
        }

        // when
        val expected = listOf(
            JUnitTest.Case(
                name = "test1",
                classname = "TestClassName",
                time = "1.100"
            ).apply {
                webLink =
                    "https://console.firebase.google.com/project/projectId/testlab/histories/historyId/matrices/executionId/executions/stepId/testcases/test1"
            },
            JUnitTest.Case(
                name = "test2",
                classname = "TestClassName",
                time = "1.100",
                skipped = null
            ).apply {
                webLink =
                    "https://console.firebase.google.com/project/projectId/testlab/histories/historyId/matrices/executionId/executions/stepId/testcases/test2"
            },
            JUnitTest.Case(
                name = "test3",
                classname = "TestClassName",
                time = "1.100",
                errors = listOf("exception")
            ).apply {
                webLink =
                    "https://console.firebase.google.com/project/projectId/testlab/histories/historyId/matrices/executionId/executions/stepId/testcases/test3"
            },
            JUnitTest.Case(
                name = "test4",
                classname = "TestClassName",
                time = "1.100",
                failures = listOf("exception")
            ).apply {
                webLink =
                    "https://console.firebase.google.com/project/projectId/testlab/histories/historyId/matrices/executionId/executions/stepId/testcases/test4"
            },
            JUnitTest.Case(
                name = "test5",
                classname = "TestClassName",
                time = "1.100",
                failures = listOf("exception")
            ).apply {
                webLink =
                    "https://console.firebase.google.com/project/projectId/testlab/histories/historyId/matrices/executionId/executions/stepId/testcases/test5"
                flaky = true
            }
        )

        val actual = createJUnitTestCases(
            testCases = testCases,
            toolResultsStep = toolResultsStep,
        )

        // then
        assertArrayEquals(
            expected.map(xmlPrettyWriter::writeValueAsString).toTypedArray(),
            actual.map(xmlPrettyWriter::writeValueAsString).toTypedArray()
        )
    }

    // helper
    private var testIdCounter = 1

    private fun TestCase.setup(
        hasException: Boolean = false,
        status: String? = null,
        isFlaky: Boolean = false
    ) = apply {
        testCaseId = "test${testIdCounter++}"
        this.status = status
        testCaseReference = TestCaseReference().apply {
            name = testCaseId
            className = "TestClassName"
        }
        elapsedTime = Duration().apply {
            seconds = 1
            nanos = 1_000_000_00
        }
        if (hasException) stackTraces = listOf(
            StackTrace().also {
                it.exception = "exception"
            }
        )
        if (isFlaky) flaky = true
    }
}
