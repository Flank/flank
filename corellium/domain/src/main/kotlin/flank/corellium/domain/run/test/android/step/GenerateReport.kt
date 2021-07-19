package flank.corellium.domain.run.test.android.step

import flank.corellium.domain.RunTestCorelliumAndroid
import flank.corellium.domain.RunTestCorelliumAndroid.ExecuteTests
import flank.corellium.domain.RunTestCorelliumAndroid.GenerateReport
import flank.corellium.domain.RunTestCorelliumAndroid.OutputDir
import flank.corellium.domain.RunTestCorelliumAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.junit.JUnit
import flank.junit.generateJUnitReport
import flank.junit.writeAsXml
import java.io.File

/**
 * The step is generating the summary report basing on the collected state.
 * Generated JUnit report is saved as formatted xml file
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
    RunTestCorelliumAndroid.Created(file).out()
}

/**
 * Simple mapper, no logical operations or API calls,
 * just converting one structure to another.
 *
 * @receiver Instrument results of each instance execution
 * @return prepared input for generating JUnitReport
 */
private fun List<List<Instrument>>.prepareInputForJUnit(): List<JUnit.TestResult> =
    flatMapIndexed { index: Int, list: List<Instrument> ->
        list.filterIsInstance<Instrument.Status>().map { status ->
            JUnit.TestResult(
                suiteName = "shard_$index",
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
