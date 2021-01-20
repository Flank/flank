package ftl.analytics

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.args.blockSendingUsageStatistics
import ftl.util.isGoogleAnalyticsDisabled

fun AndroidArgs.sendConfiguration() = sendConfiguration(events = createEventMap())

fun IosArgs.sendConfiguration() = sendConfiguration(events = createEventMap())

fun IArgs.sendConfiguration(events: Map<String, Any?>, rootPath: String = userHome) =
    takeUnless { blockSendingUsageStatistics || isGoogleAnalyticsDisabled(rootPath) }?.run {
        registerUser()
        events
            .toEvent(project)
            .send()
    }
