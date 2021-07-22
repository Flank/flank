package ftl.analytics

import com.google.common.annotations.VisibleForTesting
import flank.tool.analytics.mixpanel.FIREBASE
import flank.tool.analytics.mixpanel.FLANK_VERSION
import flank.tool.analytics.mixpanel.SESSION_ID
import flank.tool.analytics.mixpanel.TEST_PLATFORM
import flank.tool.analytics.mixpanel.add
import flank.tool.analytics.mixpanel.analyticsReport
import flank.tool.analytics.mixpanel.filterSensitiveValues
import flank.tool.analytics.mixpanel.objectToMap
import flank.tool.analytics.mixpanel.removeNotNeededKeys
import flank.tool.analytics.mixpanel.schemaVersion
import flank.tool.analytics.mixpanel.sessionId
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.environment.PHYSICAL_DEVICE
import ftl.environment.VIRTUAL_DEVICE
import ftl.util.readVersion

fun AndroidArgs.reportConfiguration() {
    addCommonData()
        .add("configuration", createEventMap())
        .add("device_types", devices.map { if (it.isVirtual) VIRTUAL_DEVICE else PHYSICAL_DEVICE }.distinct())
}

internal fun AndroidArgs.createEventMap() =
    toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

fun IosArgs.reportConfiguration() {
    addCommonData()
        .add("configuration", createEventMap())
        .add("device_types", listOf(PHYSICAL_DEVICE))
}

fun IArgs.addCommonData() = let {
    initUsageStatistics()
    analyticsReport
        .add("schema_version", schemaVersion)
        .add(FLANK_VERSION, readVersion())
        .add(SESSION_ID, sessionId)
        .add(TEST_PLATFORM, FIREBASE)
        .add("project_id", project)
}

private fun IosArgs.createEventMap() =
    toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

private fun IArgs.toArgsMap() = objectToMap().filterNonCommonArgs()

@VisibleForTesting
internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }

private const val COMMON_ARGS = "commonArgs"
