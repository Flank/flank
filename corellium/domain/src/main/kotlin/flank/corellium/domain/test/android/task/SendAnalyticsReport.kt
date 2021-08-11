package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid
import flank.corellium.domain.TestAndroid.AnalyticsReport
import flank.corellium.domain.TestAndroid.Args
import flank.corellium.domain.TestAndroid.DeviceCostPerSecond
import flank.corellium.domain.TestAndroid.DevicesDuration
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.TestDuration
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.instrument.log.Instrument
import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.toMap
import flank.tool.resource.readRevision

internal val sendAnalyticsReport = AnalyticsReport from setOf(
    PrepareShards,
    ParseApkInfo,
    ExecuteTests,
    DeviceCostPerSecond,
    DevicesDuration,
    TestDuration,
) using context {
    Mixpanel.run {
        fun Long.devicesCost() = div(1000) * costPerSecond
        val statistics = mapOf(
            CONFIGURATION to args.toMap(),
            FLANK_VERSION to readRevision(),
            TEST_PLATFORM to Mixpanel.Platform.CORELLIUM,
            APP_ID to shards.flatten().mapNotNull { app -> packageNames[app.name] }.toSet(),
            DEVICE_TYPES to "VIRTUAL",
            "test_duration" to testDuration,
            "cost" to mapOf(
                "testing" to calculateAbsoluteTestingDuration().devicesCost(),
                "device" to devicesDuration.values.sum().devicesCost(),
            )
        )
        configure(args.project, false, Args::class)
        add(statistics)
    }
}

private fun TestAndroid.Context.calculateAbsoluteTestingDuration(): Long =
    testResult.sumOf { result ->
        result.value
            .filterIsInstance<Instrument.Status>()
            .takeIf(List<Instrument.Status>::isNotEmpty)
            ?.run { last().endTime - first().startTime }
            ?: 0L
    }
