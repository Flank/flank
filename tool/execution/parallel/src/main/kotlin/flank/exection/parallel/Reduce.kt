package flank.exection.parallel

import flank.exection.parallel.internal.contextValidators
import flank.exection.parallel.internal.reduceTo

/**
 * Reduce given [Tasks] by [expected] types to remove unneeded tasks from the graph.
 * The returned graph will hold only tasks that are returning selected types, their dependencies and derived dependencies.
 * Additionally this is keeping also the validators for initial state.
 *
 * @return Reduced [Tasks]
 */
operator fun Tasks.invoke(
    expected: Set<Parallel.Type<*>>
): Tasks =
    reduceTo(expected + contextValidators())

/**
 * Shortcut for tasks reducing.
 */
operator fun Tasks.invoke(
    vararg expected: Parallel.Type<*>
): Tasks = invoke(expected.toSet())
