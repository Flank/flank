package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.GenerateCostReport
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.json.writeJson
import flank.junit.JUnit
import java.io.File
import java.math.BigDecimal

internal val generateCostReport = GenerateCostReport from setOf(
    TestAndroid.ExecuteTests,
    TestAndroid.DevicesDuration,
    TestAndroid.DeviceCost,
) using TestAndroid.context {
    val file = File(args.outputDir, JUnit.COST_REPORT_FILE_NAME)
    val report = mapOf(
        "testing" to testResult.sumOf { result ->
            calculateCostPerSecond(result.duration(), costMicroCents[result.id] ?: -1)
        },
        "device" to devicesDuration.toList().sumOf { (id, duration) ->
            calculateCostPerSecond(duration, costMicroCents[id] ?: -1)
        },
    )
    report.apply { writeJson(file.writer()) }
}

private fun TestAndroid.Device.Result.duration(): Long = value
    .filterIsInstance<Instrument.Status>()
    .takeIf(List<Instrument.Status>::isNotEmpty)
    ?.run { last().endTime - first().startTime }
    ?: 0L

private fun calculateCostPerSecond(
    durationInMilliseconds: Long,
    costInMicroCentsPerSecond: Long
) = run {
    BigDecimal.valueOf(durationInMilliseconds) * BigDecimal.valueOf(costInMicroCentsPerSecond)
}.movePointLeft(MILLIS + MICRO)

private const val MILLIS = 3
private const val MICRO = 6
