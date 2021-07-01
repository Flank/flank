package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun withPackageName(packageNames: List<String>) = Test.Filter(
    describe = "withPackageName (${packageNames.joinToString(", ")})",
    shouldRun = { (name, _) ->
        packageNames.any { packageName ->
            name.startsWith(packageName)
        }
    }
)
