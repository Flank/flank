package ftl.client.junit

import com.google.api.services.toolresults.model.StackTrace
import com.google.api.services.toolresults.model.TestCase
import com.google.testing.model.ToolResultsStep
import ftl.reports.api.format
import ftl.reports.api.millis

internal fun createJUnitTestCases(
    testCases: List<TestCase>,
    toolResultsStep: ToolResultsStep,
    overheadTime: Double
): List<JUnitTestCase> = testCases.map { testCase ->
    createJUnitTestCase(
        testCase = testCase,
        toolResultsStep = toolResultsStep,
        overheadTime = overheadTime
    )
}

private fun createJUnitTestCase(
    testCase: TestCase,
    toolResultsStep: ToolResultsStep,
    overheadTime: Double
): JUnitTestCase {
    val stackTraces = mapOf(
        testCase.status to testCase.stackTraces?.map(StackTrace::getException)
    )
    return JUnitTestCase(
        name = testCase.testCaseReference.name,
        classname = testCase.testCaseReference.className,
        time = (testCase.elapsedTime.millis() + overheadTime).format(),
        failures = stackTraces["failed"],
        errors = stackTraces["error"],
        // skipped = true is represented by null. skipped = false is "absent"
        skipped = if (testCase.status == "skipped") null else "absent"
    ).apply {
        webLink = getWebLink(toolResultsStep, testCase.testCaseId)
        if (testCase.flaky) {
            flaky = true
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
