package ftl.util

import com.google.api.services.toolresults.model.FailureDetail
import com.google.api.services.toolresults.model.InconclusiveDetail
import com.google.api.services.toolresults.model.Outcome
import com.google.api.services.toolresults.model.SkippedDetail
import com.google.api.services.toolresults.model.SuccessDetail
import ftl.reports.api.data.TestSuiteOverviewData
import ftl.util.StepOutcome.failure
import ftl.util.StepOutcome.flaky
import ftl.util.StepOutcome.inconclusive
import ftl.util.StepOutcome.skipped
import ftl.util.StepOutcome.success
import ftl.util.StepOutcome.unset
import java.lang.StringBuilder

fun Outcome.getDetails(testSuiteOverviewData: TestSuiteOverviewData?) = when (summary) {
    success, flaky -> testSuiteOverviewData?.let { getSuccessOutcomeDetails(it, successDetail) }
    failure -> failureDetail.getFailureOutcomeDetails(testSuiteOverviewData)
    inconclusive -> inconclusiveDetail.formatOutcomeDetails()
    skipped -> skippedDetail.formatOutcomeDetails()
    unset -> "unset"
    else -> "unknown"
}

private fun getSuccessOutcomeDetails(
    testSuiteOverviewData: TestSuiteOverviewData,
    successDetail: SuccessDetail?
) = StringBuilder("${testSuiteOverviewData.successCount} test cases passed").apply {
    if (testSuiteOverviewData.skipped > 0) append(skippedMessage(testSuiteOverviewData.skipped))
    if (testSuiteOverviewData.flakes > 0) append(flakesMessage(testSuiteOverviewData.flakes))
    if (successDetail?.otherNativeCrash == true) append(NATIVE_CRASH_MESSAGE)
}.toString()

private val TestSuiteOverviewData.successCount
    get() = total - errors - failures - skipped - flakes

private fun FailureDetail?.getFailureOutcomeDetails(testSuiteOverviewData: TestSuiteOverviewData?) = when {
    this == null -> testSuiteOverviewData?.buildFailureOutcomeDetailsSummary()
    crashed -> "Application crashed"
    timedOut -> "Test timed out"
    notInstalled -> "App failed to install"
    else -> testSuiteOverviewData?.buildFailureOutcomeDetailsSummary()
} + this?.takeIf { it.otherNativeCrash }?.let { NATIVE_CRASH_MESSAGE }.orEmpty()

private fun TestSuiteOverviewData.buildFailureOutcomeDetailsSummary(): String {
    return StringBuilder("$failures test casess failed").apply {
        if (errors > 0) append(errorMessage(errors))
        successCount.takeIf { it > 0 }?.let(successMessage)
        if (skipped > 0) append(skippedMessage(skipped))
        if (flakes > 0) append(flakesMessage(flakes))
    }.toString()
}

private fun InconclusiveDetail?.formatOutcomeDetails() = when {
    this == null -> "Unknown reason"
    infrastructureFailure -> "Infrastructure failure"
    abortedByUser -> "Test run aborted by user"
    else -> "Unknown reason"
}

private fun SkippedDetail?.formatOutcomeDetails(): String = when {
    this == null  -> "Unknown reason"
    incompatibleAppVersion == true -> "Incompatible device/OS combination"
    incompatibleArchitecture == true -> "App does not support the device architecture"
    incompatibleAppVersion == true -> "App does not support the OS version"
    else -> "Unknown reason"
}

private const val NATIVE_CRASH_MESSAGE = " (Native crash)"
private val flakesMessage: (Int) -> String = { ", $it flaky" }
private val skippedMessage: (Int) -> String = { ", $it skipped" }
private val successMessage: (Int) -> String = { ", $it passed" }
private val errorMessage: (Int) -> String = { ", $it errors" }
