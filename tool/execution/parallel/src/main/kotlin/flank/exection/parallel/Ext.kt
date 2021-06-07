package flank.exection.parallel

import kotlinx.coroutines.Job

/**
 * Extension for executing tasks on a given state.
 */
operator fun Tasks.invoke(
    vararg state: Pair<Parallel.Type<*>, Any>
) = invoke(state.toMap())

/**
 * Extension for executing tasks on empty state.
 */
operator fun Tasks.invoke() =
    invoke(emptyMap())

/**
 * Extension for reducing tasks to returned with dependencies.
 */
operator fun Tasks.invoke(
    vararg returns: Parallel.Type<*>
) = invoke(returns.toSet())


data class DeadlockError(
    val state: ParallelState,
    val jobs: Map<Parallel.Type<*>, Job>,
    val remaining: Map<Set<Parallel.Type<*>>, List<Parallel.Task<*>>>,
) : Error() {
    override fun toString(): String = listOf(
        "Parallel execution dump:",
        state,
        jobs,
        remaining,
        "", super.toString(),
    ).joinToString("\n")
}
