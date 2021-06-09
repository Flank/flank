package flank.exection.parallel

import kotlinx.coroutines.flow.Flow

// ======================= Reduce =======================
/**
 * Shortcut for tasks reducing.
 */
operator fun Tasks.invoke(
    vararg returns: Parallel.Type<*>
): Tasks = invoke(returns.toSet())

// ======================= Execute =======================
/**
 * Shortcut for executing tasks on a given state.
 */
operator fun Tasks.invoke(
    vararg state: Pair<Parallel.Type<*>, Any>
): Flow<ParallelState> = invoke(state.toMap())

/**
 * Shortcut for executing tasks on empty state.
 */
operator fun Tasks.invoke(): Flow<ParallelState> =
    invoke(emptyMap())
