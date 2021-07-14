package ftl.analytics

import com.google.common.annotations.VisibleForTesting
import flank.tool.analytics.mixpanel.filterSensitiveValues
import flank.tool.analytics.mixpanel.objectToMap
import flank.tool.analytics.mixpanel.removeNotNeededKeys
import flank.tool.analytics.mixpanel.sendConfiguration
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs

fun AndroidArgs.sendConfiguration() {
    initUsageStatistics()
    sendConfiguration(project = project, events = createEventMap())
}

internal fun AndroidArgs.createEventMap() =
    toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

fun IosArgs.sendConfiguration() {
    initUsageStatistics()
    sendConfiguration(project = project, events = createEventMap())
}

private fun IosArgs.createEventMap() =
    toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

private fun IArgs.toArgsMap() = objectToMap().filterNonCommonArgs()

@VisibleForTesting
internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }

private const val COMMON_ARGS = "commonArgs"
