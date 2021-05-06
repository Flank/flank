package ftl.client.junit

import com.google.api.services.toolresults.model.Step
import com.google.api.services.toolresults.model.TestCase

// List of TestExecutionData can contains also secondary steps from flaky tests reruns.
// We need only primary steps, but we also prefer to display failed tests over successful,
// so we overrides successful test cases with failed from secondary steps
internal fun List<TestExecutionData>.prepareForJUnitResult(): List<TestExecutionData> = this
    .reduceToPrimarySteps()
    .reduceTestCases()

private fun List<TestExecutionData>.reduceToPrimarySteps(): List<TestExecutionData> = groupBy { data ->
    data.step.primaryStepId
}.mapNotNull { (_, list: List<TestExecutionData>) ->
    list.sortedBy { data ->
        data.step.multistepNumber
    }.reduce { primary, next ->
        primary.copy(testCases = primary.testCases + next.testCases)
    }
}

private fun List<TestExecutionData>.reduceTestCases(): List<TestExecutionData> = map(TestExecutionData::reduceTestCases)

private fun TestExecutionData.reduceTestCases() = copy(
    testCases = testCases.groupBy(TestCase::getTestCaseId).map { (_, testCases) ->
        testCases.sortedBy { testCase: TestCase ->
            testCase.startTime?.asUnixTimestamp()
        }.run {
            if (!isFlaky()) first().apply {
                stackTraces = testCases.mapNotNull { it.stackTraces }.flatten()
            }
            else first { it.stackTraces != null }.apply {
                flaky = true
                stackTraces = testCases.mapNotNull { it.stackTraces }.flatten()
            }
        }
    }
)

internal fun List<TestExecutionData>.removeStackTraces(): List<TestExecutionData> =
    map(TestExecutionData::removeStackTraces)

private fun TestExecutionData.removeStackTraces() = copy(
    testCases = testCases.onEach {
        if (it.flaky) it.stackTraces = emptyList()
    }
)

// For primary step return stepId instead of primaryStepId
private val Step.primaryStepId get() = multiStep?.primaryStepId ?: stepId

// Primary step doesn't have multistepNumber so use 0
private val Step.multistepNumber get() = multiStep?.multistepNumber ?: 0

// tests are flaky if any of them is not successful, but not all
private fun List<TestCase>.isFlaky() = count { it.stackTraces != null } in 1 until size
