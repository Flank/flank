package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.AnalyticsReport
import flank.corellium.domain.TestAndroid.Args
import flank.corellium.domain.TestAndroid.ExecuteTests
import flank.corellium.domain.TestAndroid.GenerateCostReport
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.TestDuration
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.toMap
import flank.tool.resource.readRevision

internal val sendAnalyticsReport = AnalyticsReport from setOf(
    PrepareShards,
    ParseApkInfo,
    ExecuteTests,
    GenerateCostReport,
    TestDuration,
) using context {
    Mixpanel.run {
        val statistics = mapOf(
            CONFIGURATION to args.toMap(),
            FLANK_VERSION to readRevision(),
            TEST_PLATFORM to Mixpanel.Platform.CORELLIUM,
            APP_ID to shards.flatten().mapNotNull { app -> packageNames[app.name] }.toSet(),
            DEVICE_TYPES to "VIRTUAL",
            "test_duration" to testDuration,
            "cost" to costReport
        )
        configure(args.project, false, Args::class)
        add(statistics)
        send("Flank Corellium")
    }
}
