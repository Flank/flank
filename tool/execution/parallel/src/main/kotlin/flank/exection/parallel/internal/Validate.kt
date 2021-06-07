package flank.exection.parallel.internal

import flank.exection.parallel.MissingDependencies
import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.Tasks

/**
 * Validate the given [Tasks] and [ParallelState] for finding missing dependencies or broken paths.
 *
 * @param initial The initial arguments for tasks execution.
 * @return Valid [Tasks] if graph has no broken paths or missing dependencies.
 * @throws [Parallel.ValidationError] if find broken paths between tasks.
 */
internal fun Tasks.validate(initial: ParallelState = emptyMap()): Tasks = apply {
    val missing = findMissingDependencies(initial.keys)
    if (missing.isNotEmpty()) throw Parallel.ValidationError(missing)
}

private fun Tasks.findMissingDependencies(initial: Set<Parallel.Type<*>>): MissingDependencies {
    val all = map { task -> task.signature.returns }.toSet() + initial
    return associate { task -> task.signature.run { returns to args - all } }
        .filterValues { missing -> missing.isNotEmpty() }
}
