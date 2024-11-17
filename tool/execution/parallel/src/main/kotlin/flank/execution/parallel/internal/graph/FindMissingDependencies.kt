package flank.execution.parallel.internal.graph

internal fun <T : Any> Map<T, Set<T>>.findMissingDependencies(
    initial: Set<T>
): Map<T, Set<T>> {
    val all = keys + initial
    return mapValues { (_, args) -> args - all }.filterValues { missing -> missing.isNotEmpty() }
}
