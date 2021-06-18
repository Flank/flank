package ftl.analytics

import flank.common.userHome
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.args.blockSendingUsageStatistics
import ftl.util.isGoogleAnalyticsDisabled

private const val CONFIGURATION_KEY = "configuration"
private const val APP_ID = "APP_ID"
private const val DEVICE_TYPE = "DEVICE_TYPE"
fun AndroidArgs.sendConfiguration() = sendConfiguration(events = createEventMap())

fun IosArgs.sendConfiguration() = sendConfiguration(events = createEventMap())

fun IArgs.sendConfiguration(
    events: Map<String, Any?>,
    rootPath: String = userHome,
    eventName: String = CONFIGURATION_KEY
) =
    takeUnless { blockSendingUsageStatistics || isGoogleAnalyticsDisabled(rootPath) }?.run {
        registerUser()
        events
            .toEvent(project, eventName)
            .send()
    }

fun IArgs.sendAppId(appId: String) = when (this) {
    is AndroidArgs -> sendConfiguration(mapOf(APP_ID to appId, DEVICE_TYPE to "Android"), eventName = APP_ID)
    else -> sendConfiguration(mapOf(APP_ID to appId, DEVICE_TYPE to "Ios"), eventName = APP_ID)
}
