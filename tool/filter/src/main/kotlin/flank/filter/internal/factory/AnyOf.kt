package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun anyOf(vararg filters: Test.Filter) = Test.Filter(
    describe = "anyOf ${filters.map { it.describe }}",
    shouldRun = { testMethod ->
        filters.isEmpty() || filters.any { filter -> filter.shouldRun(testMethod) }
    }
)
