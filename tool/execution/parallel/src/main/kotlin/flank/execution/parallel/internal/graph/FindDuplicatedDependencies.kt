package flank.execution.parallel.internal.graph

internal fun <T> List<T>.findDuplicatedDependencies(
    initial: Set<T>
): Map<T, Int> =
    plus(initial)
        .groupBy { it }
        .mapValues { (_, list) -> list.size - 1 }
        .filterValues { it > 0 }
