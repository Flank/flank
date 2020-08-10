package ftl.json

import com.google.api.services.toolresults.model.Environment
import com.google.api.services.toolresults.model.FailureDetail
import com.google.api.services.toolresults.model.InconclusiveDetail
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.SkippedDetail
import com.google.api.services.toolresults.model.Step
import ftl.reports.api.createTestSuitOverviewData
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.StepOutcome
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.StepOutcome.unset

fun Environment.getDetails(): String {
    require(environmentResult?.outcome?.summary != null)
    return environmentResult.outcome.getDetails(createTestSuitOverviewData())
}

fun List<Step>.getOutcome(): Outcome? = minBy {
    StepOutcome.order.indexOf(it.outcome?.summary)
}?.outcome

fun List<Step>.getDetails(): String {
    val outcome = getOutcome()
    return outcome?.getDetails(createTestSuitOverviewData()) ?: "Unknown outcome"
}

internal fun Outcome.getDetails(testSuiteOverviewData: TestSuiteOverviewData?): String = when (summary) {
    success, flaky -> testSuiteOverviewData?.getSuccessOutcomeDetails(successDetail?.otherNativeCrash ?: false)
        ?: "Unknown outcome"
    failure -> failureDetail.getFailureOutcomeDetails(testSuiteOverviewData)
    inconclusive -> inconclusiveDetail.formatOutcomeDetails()
    skipped -> skippedDetail.formatOutcomeDetails()
    unset -> "Unset outcome"
    else -> "Unknown outcome"
}

private fun TestSuiteOverviewData.getSuccessOutcomeDetails(
    otherNativeCrash: Boolean
) = StringBuilder("$successCount test cases passed").apply {
    if (skipped > 0) append(skippedMessage(skipped))
    if (flakes > 0) append(flakesMessage(flakes))
    if (otherNativeCrash) append(NATIVE_CRASH_MESSAGE)
}.toString()

private val TestSuiteOverviewData.successCount
    get() = total - errors - failures - skipped - flakes

private fun FailureDetail?.getFailureOutcomeDetails(testSuiteOverviewData: TestSuiteOverviewData?) = when {
    this == null -> testSuiteOverviewData?.buildFailureOutcomeDetailsSummary() ?: "Unknown failure"
    crashed == true -> "Application crashed"
    timedOut == true -> "Test timed out"
    notInstalled == true -> "App failed to install"
    else -> testSuiteOverviewData?.buildFailureOutcomeDetailsSummary() ?: "Unknown failure"
} + this?.takeIf { it.otherNativeCrash }?.let { NATIVE_CRASH_MESSAGE }.orEmpty()

private fun TestSuiteOverviewData.buildFailureOutcomeDetailsSummary() =
    StringBuilder("$failures test cases failed").apply {
        if (errors > 0) append(errorMessage(errors))
        successCount.takeIf { it > 0 }?.let { append(successMessage(it)) }
        if (skipped > 0) append(skippedMessage(skipped))
        if (flakes > 0) append(flakesMessage(flakes))
    }.toString()

private fun InconclusiveDetail?.formatOutcomeDetails() = when {
    this == null -> UNKNOWN_REASON_MESSAGE
    infrastructureFailure == true -> INFRASTRUCTURE_FAILURE_MESSAGE
    abortedByUser == true -> ABORTED_BY_USER_MESSAGE
    else -> UNKNOWN_REASON_MESSAGE
}

private fun SkippedDetail?.formatOutcomeDetails(): String = when {
    this == null -> UNKNOWN_REASON_MESSAGE
    incompatibleDevice == true -> INCOMPATIBLE_DEVICE_MESSAGE
    incompatibleArchitecture == true -> INCOMPATIBLE_ARCHITECTURE_MESSAGE
    incompatibleAppVersion == true -> INCOMPATIBLE_APP_VERSION_MESSAGE
    else -> UNKNOWN_REASON_MESSAGE
}

private const val UNKNOWN_REASON_MESSAGE = "Unknown reason"
const val INFRASTRUCTURE_FAILURE_MESSAGE = "Infrastructure failure"
const val ABORTED_BY_USER_MESSAGE = "Test run aborted by user"
const val INCOMPATIBLE_DEVICE_MESSAGE = "Incompatible device/OS combination"
const val INCOMPATIBLE_ARCHITECTURE_MESSAGE = "App does not support the device architecture"
const val INCOMPATIBLE_APP_VERSION_MESSAGE = "App does not support the OS version"

private const val NATIVE_CRASH_MESSAGE = " (Native crash)"
private val flakesMessage: (Int) -> String = { ", $it flaky" }
private val skippedMessage: (Int) -> String = { ", $it skipped" }
private val successMessage: (Int) -> String = { ", $it passed" }
private val errorMessage: (Int) -> String = { ", $it errors" }
