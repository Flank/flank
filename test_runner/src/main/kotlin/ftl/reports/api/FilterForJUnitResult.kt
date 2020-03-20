package ftl.reports.api

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase
import ftl.reports.api.data.TestExecutionData

// List of TestExecutionData can contains also secondary steps from flaky tests reruns.
// We need only primary steps, but we also prefer to display failed tests over successful,
// so we overrides successful test cases with failed from secondary steps
internal fun List<TestExecutionData>.filterForJUnitResult(): List<TestExecutionData> = groupBy { data ->
    data.step.primaryStepId
}.mapNotNull { (_, list: List<TestExecutionData>) ->
    // sort by multistepNumber, primary step on first position will be used as accumulator for merged test cases
    list.sortedBy { testExecutionData ->
        testExecutionData.step.multistepNumber
    }.reduce { primary, next ->
        primary.copy(testCases = mergeFlaky(primary.testCases, next.testCases))
    }
}

// For primary step return stepId instead of primaryStepId
private val Step.primaryStepId get() = multiStep?.primaryStepId ?: stepId

// Primary step doesn't have multistepNumber so use 0
private val Step.multistepNumber get() = multiStep?.multistepNumber ?: 0

// For the same test cases from different steps, prefer failed one with stacktrace over successful
private fun mergeFlaky(
    primaryStepTestCases: List<TestCase>,
    nextStepTestCases: List<TestCase>
): List<TestCase> = (primaryStepTestCases + nextStepTestCases)
    .groupBy { testCase: TestCase ->
        testCase.testCaseReference
    }
    .map { (_, list: List<TestCase>) ->
        list.firstOrNull { it.stackTraces  != null } ?: list.first()
    }