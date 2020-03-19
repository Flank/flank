package ftl.reports.api

import ftl.reports.api.data.TestExecutionData

//    Filter elements that will be used to JUnitResult generation
internal fun List<TestExecutionData>.filterForJUnitResult(): List<TestExecutionData> = groupBy { data ->
    // Group multi steps of flaky test by primaryStepId
    data.step.multiStep?.primaryStepId ?: data.step.stepId
}.mapNotNull { (_, list: List<TestExecutionData>) ->
    // TODO consider if failure result is more important than successful for flaky test
    // get first first successful rerun of flaky test
    list.first()
    // get first successful rerun of flaky test
//    list.minBy { it.step.multiStep.multistepNumber ?: Integer.MAX_VALUE }?.copy(
//        timestamp = list.first().timestamp
//    )
}
