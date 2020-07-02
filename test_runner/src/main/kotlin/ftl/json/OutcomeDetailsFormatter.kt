package ftl.json

import com.google.api.services.toolresults.model.FailureDetail
import com.google.api.services.toolresults.model.InconclusiveDetail
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.SkippedDetail
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.StepOutcome.unset

fun Outcome.getDetails(testSuiteOverviewData: TestSuiteOverviewData?) = when (summary) {
    success, flaky -> testSuiteOverviewData?.getSuccessOutcomeDetails(successDetail?.otherNativeCrash ?: false)
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
    this == null -> "Unknown reason"
    infrastructureFailure == true -> "Infrastructure failure"
    abortedByUser == true -> "Test run aborted by user"
    else -> "Unknown reason"
}

private fun SkippedDetail?.formatOutcomeDetails(): String = when {
    this == null -> "Unknown reason"
    incompatibleDevice == true -> "Incompatible device/OS combination"
    incompatibleArchitecture == true -> "App does not support the device architecture"
    incompatibleAppVersion == true -> "App does not support the OS version"
    else -> "Unknown reason"
}

private const val NATIVE_CRASH_MESSAGE = " (Native crash)"
private val flakesMessage: (Int) -> String = { ", $it flaky" }
private val skippedMessage: (Int) -> String = { ", $it skipped" }
private val successMessage: (Int) -> String = { ", $it passed" }
private val errorMessage: (Int) -> String = { ", $it errors" }
