package flank.exection.parallel

import flank.exection.parallel.internal.initialValidators
import flank.exection.parallel.internal.reduceTo

/**
 * Reduce given [Tasks] by [select] types to remove unneeded tasks from the graph.
 * The returned graph will hold only tasks that are returning selected types, their dependencies and derived dependencies.
 * Additionally this is keeping also the validators for initial state.
 *
 * @return Reduced [Tasks]
 */
operator fun Tasks.invoke(
    select: Set<Parallel.Type<*>>
): Tasks =
    reduceTo(select + initialValidators)

/**
 * Shortcut for tasks reducing.
 */
operator fun Tasks.invoke(
    vararg returns: Parallel.Type<*>
): Tasks = invoke(returns.toSet())
