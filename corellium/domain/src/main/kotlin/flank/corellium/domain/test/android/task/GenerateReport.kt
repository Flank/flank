package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.Args.ReportConfig.*
import flank.corellium.domain.TestAndroid.Device
import flank.corellium.domain.TestAndroid.Dispatch
import flank.corellium.domain.TestAndroid.TestExecution
import flank.corellium.domain.TestAndroid.GenerateReport
import flank.corellium.domain.TestAndroid.OutputDir
import flank.corellium.domain.TestAndroid.ProcessResults
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.Parallel
import flank.exection.parallel.from
import flank.exection.parallel.select
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
    TestExecution,
    ProcessResults,
    OutputDir
) using context { state ->
    val results = (state select TestExecution) + state
    args.reportConfig.jUnitXml.forEach { (suffix, types) ->
        val file = File(args.outputDir, JUnit.REPORT_FILE_NAME.replace(".", "$suffix."))
        val parallelTypes: List<Parallel.Type<List<Device.Result>>> = types.map(JUnitType::asParallel)
        require(results.keys.containsAll(parallelTypes)) {
            "Cannot generate report, missing values for types: " + (parallelTypes - state.keys)
        }
        types.flatMap { type -> results select type.asParallel() }
            .prepareInputForJUnit()
            .generateJUnitReport()
            .writeAsXml(file.bufferedWriter())
        TestAndroid.Created(file).out()
    }
}

private fun JUnitType.asParallel(): Parallel.Type<List<Device.Result>> = when (this) {
    JUnitType.Shard -> TestExecution.Results.Shard
    JUnitType.Rerun -> TestExecution.Results.Rerun
    JUnitType.Processed -> ProcessResults
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
                flaky = status.details.fullName in result.flakes,
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

private val Instrument.Status.Details.fullName
    get() = "$className#$testName"
