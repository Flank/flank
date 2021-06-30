package flank.filter

import flank.filter.internal.fromTestTargets

/**
 * Factory for creating [ShouldRun] function from given [targets].
 */
fun createTestCasesFilter(targets: List<String>): ShouldRun =
    fromTestTargets(targets).shouldRun

typealias ShouldRun = (Target) -> Boolean

typealias Target = Pair<String, List<String>>
