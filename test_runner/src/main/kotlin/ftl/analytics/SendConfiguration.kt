package ftl.analytics

import com.google.common.annotations.VisibleForTesting
import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.Mixpanel.add
import flank.tool.analytics.mixpanel.internal.FIREBASE
import flank.tool.analytics.mixpanel.internal.filterSensitiveValues
import flank.tool.analytics.mixpanel.internal.objectToMap
import flank.tool.analytics.mixpanel.internal.removeNotNeededKeys
import flank.tool.analytics.mixpanel.internal.schemaVersion
import flank.tool.analytics.mixpanel.internal.sessionId
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.environment.PHYSICAL_DEVICE
import ftl.environment.VIRTUAL_DEVICE
import ftl.util.readVersion

fun AndroidArgs.reportConfiguration() {
    add(Mixpanel.CONFIGURATION, createEventMap())
    add(
        Mixpanel.DEVICE_TYPES,
        devices.map { if (it.isVirtual) VIRTUAL_DEVICE else PHYSICAL_DEVICE }.distinct()
    )
}

internal fun AndroidArgs.createEventMap() =
    toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

fun IosArgs.reportConfiguration() {
    addCommonData()
    add(Mixpanel.CONFIGURATION, createEventMap())
    add(Mixpanel.DEVICE_TYPES, listOf(PHYSICAL_DEVICE))
}

fun IArgs.addCommonData() = let {
    initUsageStatistics()
    add(Mixpanel.SCHEMA_VERSION, schemaVersion)
    add(Mixpanel.FLANK_VERSION, readVersion())
    add(Mixpanel.SESSION_ID, sessionId)
    add(Mixpanel.TEST_PLATFORM, FIREBASE)
    add(Mixpanel.PROJECT_ID, project)
}

private fun IosArgs.createEventMap() =
    toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

private fun IArgs.toArgsMap() = objectToMap().filterNonCommonArgs()

@VisibleForTesting
internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }

private const val COMMON_ARGS = "commonArgs"
