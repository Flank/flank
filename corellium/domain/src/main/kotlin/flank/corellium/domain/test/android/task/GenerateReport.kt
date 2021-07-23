package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.GenerateReport
import flank.corellium.domain.TestAndroid.OutputDir
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.junit.JUnit
import flank.junit.generateJUnitReport
import flank.junit.writeAsXml
import java.io.File

/**
 * Generates the summary report basing on the collected state.
 * Generated JUnit report is saved as formatted xml file.
 */
internal val generateReport = GenerateReport from setOf(
    ExecuteTests,
    OutputDir
) using context {
    val file = File(args.outputDir, JUnit.REPORT_FILE_NAME)
    testResult
        .prepareInputForJUnit()
        .generateJUnitReport()
        .writeAsXml(file.bufferedWriter())
    TestAndroid.Created(file).out()
}

/**
 * Simple mapper, no logical operations or API calls,
 * just converting one structure to another.
 *
 * @receiver Instrument results of each instance execution
 * @return prepared input for generating JUnitReport
 */
private fun List<Device.Result>.prepareInputForJUnit(): List<JUnit.TestResult> =
    flatMap { result ->
        val suiteName = result.suiteName
        result.value.filterIsInstance<Instrument.Status>().map { status ->
            JUnit.TestResult(
                suiteName = suiteName,
                testName = status.details.testName,
                className = status.details.className,
                startAt = status.startTime,
                endsAt = status.endTime,
                stack = listOfNotNull(status.details.stack),
                status = when (status.code) {
                    Instrument.Code.PASSED -> JUnit.TestResult.Status.Passed
                    Instrument.Code.FAILED -> JUnit.TestResult.Status.Failed
                    Instrument.Code.EXCEPTION -> JUnit.TestResult.Status.Error
                    Instrument.Code.SKIPPED -> JUnit.TestResult.Status.Skipped
                    else -> throw IllegalArgumentException("Unsupported status code ${status.code}")
                }
            )
        }
    }

/**
 * Generates proper suite name from [Device.Result] basing on [Dispatch.Type].
 */
private val Device.Result.suiteName
    get() = when (data.type) {
        Dispatch.Type.Rerun -> "rerun"
        Dispatch.Type.Shard -> "shard"
    }.let { prefix ->
        "${prefix}_${data.index}_$id"
    }
