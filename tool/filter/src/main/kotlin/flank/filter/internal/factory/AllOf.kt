package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun allOf(vararg filters: Test.Filter) = Test.Filter(
    describe = "allOf ${filters.map { it.describe }}",
    shouldRun = { testMethod ->
        filters.isEmpty() || filters.all { filter ->
            filter.shouldRun(testMethod)
        }
    }
)
