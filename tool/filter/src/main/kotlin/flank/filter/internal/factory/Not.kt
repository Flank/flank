package flank.filter.internal.factory

import flank.filter.internal.Test

internal fun not(filter: Test.Filter) = Test.Filter(
    describe = "not (${filter.describe})",
    shouldRun = { testMethod ->
        filter.shouldRun(testMethod).not()
    },
    isAnnotation = filter.isAnnotation
)
