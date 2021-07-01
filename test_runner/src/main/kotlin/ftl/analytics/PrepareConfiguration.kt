package ftl.analytics

import com.fasterxml.jackson.core.type.TypeReference
import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs

private const val COMMON_ARGS = "commonArgs"

fun AndroidArgs.createEventMap() = toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

fun IosArgs.createEventMap() = toArgsMap().plus(commonArgs.toArgsMap()).removeNotNeededKeys().filterSensitiveValues()

private fun IArgs.toArgsMap() = objectToMap().filterNonCommonArgs()

@VisibleForTesting
internal fun Any.objectToMap() = objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})

@VisibleForTesting
internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }
