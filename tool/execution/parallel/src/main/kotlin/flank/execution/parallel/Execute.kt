package flank.execution.parallel

import flank.execution.parallel.internal.Execution
import flank.execution.parallel.internal.invoke
import flank.execution.parallel.internal.minusContextValidators
import kotlinx.coroutines.flow.Flow

/**
 * Execute tasks in parallel with a given args.
 *
 * @return [Flow] of [ParallelState] changes.
 */
infix operator fun Tasks.invoke(
    args: ParallelState
): Flow<ParallelState> =
    Execution(minusContextValidators(), args).invoke()

// ======================= Extensions =======================

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
