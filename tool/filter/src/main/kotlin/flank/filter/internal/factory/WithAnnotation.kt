package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun withAnnotation(filter: List<String>) = Test.Filter(
    describe = "withAnnotation (${filter.joinToString(", ")})",
    shouldRun = { (_, annotations) ->
        annotations.any { it in filter }
    },
    isAnnotation = true
)
