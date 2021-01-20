package ftl.analytics

import com.fasterxml.jackson.core.type.TypeReference
import com.google.common.annotations.VisibleForTesting
import ftl.args.AndroidArgs
import ftl.args.IArgs
import ftl.args.IosArgs

private const val COMMON_ARGS = "commonArgs"

internal fun AndroidArgs.createEventMap(
    defaultArgs: AndroidArgs = AndroidArgs.default(),
) = toArgsMap(defaultArgs).plus(commonArgs.toArgsMap(defaultArgs.commonArgs))

internal fun IosArgs.createEventMap(
    defaultArgs: IosArgs = IosArgs.default(),
) = toArgsMap(defaultArgs).plus(commonArgs.toArgsMap(defaultArgs.commonArgs))

private fun IArgs.toArgsMap(
    defaultArgs: IArgs
) = objectToMap().filterNonCommonArgs().getNonDefaultArgs(defaultArgs.objectToMap())

@VisibleForTesting
internal fun Any.objectToMap() = objectMapper.convertValue(this, object : TypeReference<Map<String, Any>>() {})

@VisibleForTesting
internal fun Map<String, Any>.filterNonCommonArgs() = filter { it.key != COMMON_ARGS }
