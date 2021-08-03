package ftl.analytics

import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.Mixpanel.CONFIGURATION
import flank.tool.analytics.mixpanel.Mixpanel.DEVICE_TYPES
import flank.tool.analytics.mixpanel.Mixpanel.add
import flank.tool.analytics.mixpanel.Mixpanel.removeSensitiveValues
import flank.tool.analytics.mixpanel.toMap
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.environment.PHYSICAL_DEVICE
import ftl.environment.VIRTUAL_DEVICE
import ftl.util.readVersion

fun AndroidArgs.reportConfiguration() {
    initUsageStatistics()
    addCommonData()
    add(CONFIGURATION, removeSensitiveValues(toMap() + commonArgs.toMap()/* - COMMON_ARGS*/)) // TODO verify JsonIgnore
    add(DEVICE_TYPES, devices.map { if (it.isVirtual) VIRTUAL_DEVICE else PHYSICAL_DEVICE }.distinct())
}

fun IosArgs.reportConfiguration() {
    initUsageStatistics()
    addCommonData()
    add(CONFIGURATION, removeSensitiveValues(toMap() + commonArgs.toMap()/* - COMMON_ARGS*/)) // TODO verify JsonIgnore
    add(DEVICE_TYPES, listOf(PHYSICAL_DEVICE))
}

fun IArgs.addCommonData() = let {
    add(Mixpanel.SCHEMA_VERSION, Mixpanel.schemaVersion)
    add(Mixpanel.FLANK_VERSION, readVersion())
    add(Mixpanel.SESSION_ID, Mixpanel.sessionId)
    add(Mixpanel.TEST_PLATFORM, Mixpanel.Platform.FIREBASE)
    add(Mixpanel.PROJECT_ID, project)
}

/**
 * String name of [AndroidArgs.commonArgs] and [IosArgs.commonArgs], used to remove redundant nested data
 */
private const val COMMON_ARGS = "commonArgs"
