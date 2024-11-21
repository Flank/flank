package flank.execution.parallel.internal.graph

/**
 * Find dependencies for given nodes in [graph].
 *
 * @receiver Expected elements for current iteration.
 * @param graph Graph to search.
 * @param acc Currently accumulated elements.
 * @return Set expected elements along with dependencies.
 */
internal tailrec fun <T> Set<T>.findDependenciesIn(
    graph: Map<T, Set<T>>,
    acc: Set<T> =
        if (isEmpty()) graph.keys
        else emptySet(),
): Set<T> =
    when {
        isEmpty() -> acc // No more elements, so return all accumulated.
        else -> flatMap(graph::getValue).toSet() // Map each expected element to its dependencies.
            .minus(acc) // Remove already accumulated elements to optimize calculations.
            .findDependenciesIn(graph, acc + this) // Accumulate current dependencies and run next iteration.
    }
