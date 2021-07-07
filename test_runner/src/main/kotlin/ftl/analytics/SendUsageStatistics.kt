package ftl.analytics

import flank.tool.analytics.APP_ID
import flank.tool.analytics.DEVICE_TYPE
import flank.tool.analytics.sendConfiguration
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs

fun AndroidArgs.sendConfiguration() = sendConfiguration(project = project, events = createEventMap())

fun IosArgs.sendConfiguration() = sendConfiguration(project = project, events = createEventMap())

fun IArgs.sendAppId(appId: String) = when (this) {
    is AndroidArgs -> sendConfiguration(project, mapOf(APP_ID to appId, DEVICE_TYPE to "android"), eventName = APP_ID)
    else -> sendConfiguration(project, mapOf(APP_ID to appId, DEVICE_TYPE to "ios"), eventName = APP_ID)
}
