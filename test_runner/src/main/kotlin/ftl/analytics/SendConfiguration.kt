package ftl.analytics

import flank.tool.analytics.mixpanel.Mixpanel
import flank.tool.analytics.mixpanel.Mixpanel.add
import flank.tool.analytics.mixpanel.filterSensitiveValues
import flank.tool.analytics.mixpanel.objectToMap
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.environment.PHYSICAL_DEVICE
import ftl.environment.VIRTUAL_DEVICE
import ftl.util.readVersion

fun AndroidArgs.reportConfiguration() {
    initUsageStatistics()
    addCommonData()
    add(Mixpanel.CONFIGURATION, (toArgsMap() + commonArgs.toArgsMap()).filterSensitiveValues())
    add(Mixpanel.DEVICE_TYPES, devices.map { if (it.isVirtual) VIRTUAL_DEVICE else PHYSICAL_DEVICE }.distinct())
}

fun IosArgs.reportConfiguration() {
    initUsageStatistics()
    addCommonData()
    add(Mixpanel.CONFIGURATION, (toArgsMap() + commonArgs.toArgsMap()).filterSensitiveValues())
    add(Mixpanel.DEVICE_TYPES, listOf(PHYSICAL_DEVICE))
}

fun IArgs.addCommonData() = let {
    add(Mixpanel.SCHEMA_VERSION, Mixpanel.schemaVersion)
    add(Mixpanel.FLANK_VERSION, readVersion())
    add(Mixpanel.SESSION_ID, Mixpanel.sessionId)
    add(Mixpanel.TEST_PLATFORM, Mixpanel.Platform.FIREBASE)
    add(Mixpanel.PROJECT_ID, project)
}

private fun IArgs.toArgsMap() = objectToMap().filter { it.key != COMMON_ARGS }

private const val COMMON_ARGS = "commonArgs"
