package ftl.analytics

import flank.common.userHome
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.args.blockSendingUsageStatistics
import ftl.util.isGoogleAnalyticsDisabled

private const val CONFIGURATION_KEY = "configuration"
private const val APP_ID = "app_id"
private const val DEVICE_TYPE = "device_type"
const val FLANK_VERSION = "flank_version"
const val FLANK_VERSION_PROPERTY = "version"
const val TEST_PLATFORM = "test_platform"
const val FIREBASE = "firebase"

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
    is AndroidArgs -> sendConfiguration(mapOf(APP_ID to appId, DEVICE_TYPE to "android"), eventName = APP_ID)
    else -> sendConfiguration(mapOf(APP_ID to appId, DEVICE_TYPE to "ios"), eventName = APP_ID)
}
