package flank.corellium.domain.test.android.task

import flank.corellium.domain.TestAndroid.AnalyticsReport
import flank.corellium.domain.TestAndroid.Args
import flank.corellium.domain.TestAndroid.DevicesDuration
import flank.corellium.domain.TestAndroid.ParseApkInfo
import flank.corellium.domain.TestAndroid.PrepareShards
import flank.corellium.domain.TestAndroid.TestDuration
import flank.corellium.domain.TestAndroid.context
import flank.exection.parallel.from
import flank.exection.parallel.using
import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.toMap
import java.io.InputStream

internal val sendAnalyticsReport = AnalyticsReport from setOf(
    PrepareShards,
    ParseApkInfo,
    DevicesDuration,
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
        )
        configure(args.project, false, Args::class)
        add(statistics)
    }
}

//==============================================================

// TODO Stolen from `ftl.util.UtilsKt`, before merge expose the common logic as dedicated tool.

fun readRevision(): String =
    readTextResource("revision.txt").trim()

fun readTextResource(name: String): String =
    getResource(name).bufferedReader().use { it.readText() }

private fun getResource(name: String): InputStream =
    requireNotNull(
        Thread.currentThread()
            .contextClassLoader
            .getResourceAsStream(name)
    ) { "Unable to find resource: $name" }
