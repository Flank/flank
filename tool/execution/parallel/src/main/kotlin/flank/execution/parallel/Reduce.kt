package flank.execution.parallel

import flank.execution.parallel.internal.contextValidatorTypes
import flank.execution.parallel.internal.reduceTo
import flank.execution.parallel.internal.type

/**
 * Reduce given [Tasks] by [expected] types to remove unneeded tasks from the graph.
 * The returned graph will hold only tasks that are returning selected types, their dependencies and derived dependencies.
 * Additionally, this is keeping also the validators for initial state.
 *
 * @return Reduced [Tasks]
 */
operator fun Tasks.invoke(
    expected: Set<Parallel.Type<*>>
): Tasks =
    reduceTo(expected + contextValidatorTypes())

/**
 * Shortcut for tasks reducing.
 */
operator fun Tasks.invoke(
    vararg expected: Parallel.Type<*>
): Tasks = invoke(expected.toSet())

/**
 * Remove the [Tasks] by given [types].
 */
operator fun Tasks.minus(
    types: Set<Parallel.Type<*>>
): Tasks =
    filterNot { task -> task.type in types }.toSet()
