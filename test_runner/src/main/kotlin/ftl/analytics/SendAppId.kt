package ftl.analytics

import flank.tool.analytics.mixpanel.ANDROID
import flank.tool.analytics.mixpanel.APP_ID
import flank.tool.analytics.mixpanel.DEVICE_TYPE
import flank.tool.analytics.mixpanel.IOS
import flank.tool.analytics.mixpanel.sendConfiguration
import ftl.args.AndroidArgs
import ftl.args.IArgs

fun IArgs.sendAppId(appId: String) {
    initUsageStatistics()
    when (this) {
        is AndroidArgs -> sendConfiguration(project, mapOf(APP_ID to appId, DEVICE_TYPE to ANDROID), eventName = APP_ID)
        else -> sendConfiguration(project, mapOf(APP_ID to appId, DEVICE_TYPE to IOS), eventName = APP_ID)
    }
}
