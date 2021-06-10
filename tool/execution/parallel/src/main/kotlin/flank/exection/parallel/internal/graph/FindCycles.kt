package flank.exection.parallel.internal.graph

tailrec fun <T : Any> Map<T, Set<T>>.findCycles(

    remaining: List<T> = toList()
        .sortedByDescending { (_, edges) -> edges.size }
        .map { (vertices, _) -> vertices }
        .toMutableList(),

    queue: List<T> = emptyList(),

    path: List<T> = emptyList(),

    visited: Set<T> = emptySet(),

    cycles: List<List<T>> = emptyList()

): List<List<T>> {

    val current: T = queue.firstOrNull()
        ?: remaining.firstOrNull()
        ?: return cycles

    val next: List<T> = getOrDefault(current, emptySet())
        .intersect(path + remaining + queue)
        .toList()

    val cycle = current in path + next

    // println("$cycle R:$remaining Q:$queue N:$next C:$current P:$path V:$visited")
    // println()

    return findCycles(
        remaining =
        remaining - (next + current),

        queue =
        if (cycle) queue
        else (next + queue) - current,

        path =
        when {
            cycle -> emptyList()
            next.isEmpty() -> emptyList()
            else -> path + current
        },

        visited =
        if (cycle || queue.isEmpty()) visited + path
        else visited,

        cycles =
        if (cycle) cycles.plus(element = path + current)
        else cycles
    )
}
