package ftl.reports.api

import com.google.api.services.testing.model.ToolResultsStep
import com.google.api.services.toolresults.model.StackTrace
import com.google.api.services.toolresults.model.TestCase
import ftl.reports.xml.model.JUnitTestCase

internal fun createJUnitTestCases(
    testCases: List<TestCase>,
    toolResultsStep: ToolResultsStep
): List<JUnitTestCase> = testCases.map { testCase ->
    createJUnitTestCase(testCase, toolResultsStep)
}

private fun createJUnitTestCase(
    testCase: TestCase,
    toolResultsStep: ToolResultsStep
): JUnitTestCase {
    val stackTraces = mapOf(
        testCase.status to testCase.stackTraces?.map(StackTrace::getException)
    )
    return JUnitTestCase(
        name = testCase.testCaseReference.name,
        classname = testCase.testCaseReference.className,
        time = testCase.elapsedTime.format(),
        failures = stackTraces["failed"],
        errors = stackTraces["error"],
        // skipped = true is represented by null. skipped = false is "absent"
        skipped = if (testCase.status == "skipped") null else "absent"
    ).apply {
        if (errors != null || failures != null) {
            webLink = getWebLink(toolResultsStep, testCase.testCaseId)
        }
    }
}

private fun getWebLink(toolResultsStep: ToolResultsStep, testCaseId: String): String {
    return "https://console.firebase.google.com/project/${toolResultsStep.projectId}/" +
            "testlab/histories/${toolResultsStep.historyId}/" +
            "matrices/${toolResultsStep.executionId}/" +
            "executions/${toolResultsStep.stepId}/" +
            "testcases/$testCaseId"
}
