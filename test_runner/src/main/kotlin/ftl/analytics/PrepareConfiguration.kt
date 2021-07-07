package ftl.analytics

import com.google.common.annotations.VisibleForTesting
import flank.tool.analytics.filterSensitiveValues
import flank.tool.analytics.objectToMap
import flank.tool.analytics.removeNotNeededKeys
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs

private const val COMMON_ARGS = "commonArgs"

fun AndroidArgs.createEventMap() = toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

fun IosArgs.createEventMap() = toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

private fun IArgs.toArgsMap() = objectToMap().filterNonCommonArgs()

@VisibleForTesting
internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }
