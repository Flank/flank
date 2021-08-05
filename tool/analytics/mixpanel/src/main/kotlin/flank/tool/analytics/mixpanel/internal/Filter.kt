package flank.tool.analytics.mixpanel.internal

import flank.tool.analytics.mixpanel.ObjectMap

internal fun ObjectMap.removeNotNeededKeys(
    keysToRemove: Set<String> = Report.keysToAnonymize
): ObjectMap =
    if (keysToRemove.isEmpty()) this
    else filterNot { (key, _) -> key in Report.keysToRemove }

internal fun ObjectMap.anonymizeSensitiveValues(
    keysToAnonymize: Set<String> = Report.keysToAnonymize,
    anonymousValue: String = "...",
): ObjectMap =
    if (keysToAnonymize.isEmpty()) this
    else mapValues { (key, value) ->
        when {
            key !in keysToAnonymize -> value
            value is Map<*, *> -> value.mapValues { anonymousValue }
            value is List<*> -> "Count: ${value.size}"
            else -> anonymousValue
        }
    }
