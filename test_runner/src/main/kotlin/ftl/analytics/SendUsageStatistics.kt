package ftl.analytics

import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs
import ftl.args.blockSendingUsageStatistics

fun AndroidArgs.sendConfiguration() = sendConfiguration(events = createEventMap())

fun IosArgs.sendConfiguration() = sendConfiguration(events = createEventMap())

fun IArgs.sendConfiguration(events: Map<String, Any?>) = takeUnless { blockSendingUsageStatistics }?.run {
    registerUser()
    events
        .toEvent(project)
        .send()
}
