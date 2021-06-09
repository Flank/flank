package flank.exection.parallel.internal

import flank.exection.parallel.MissingDependencies
import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.Tasks
import kotlinx.coroutines.runBlocking

/**
 * Validate the given [Tasks] and [ParallelState] for finding missing dependencies or broken paths.
 *
 * @param initial The initial arguments for tasks execution.
 * @return Valid [Tasks] if graph has no broken paths or missing dependencies.
 * @throws [Parallel.ValidationError] if find broken paths between tasks.
 */
internal fun Tasks.validate(initial: ParallelState = emptyMap()): Tasks = run {
    val (validators, tasks) = splitTasks()

    // check if initial state is providing all required values specified in context.
    runBlocking { validators.forEach { check -> check.execute(initial) } }

    // validate graph is not broken
    findMissingDependencies(initial.keys).run { if (isNotEmpty()) throw Parallel.ValidationError(this) }

    tasks.toSet()
}

private fun Iterable<Parallel.Task<*>>.splitTasks() = this
    .groupBy { it.signature.returns is Parallel.Context }
    .run { getOrDefault(true, emptyList()) to getOrDefault(false, emptyList()) }

private fun Tasks.findMissingDependencies(initial: Set<Parallel.Type<*>>): MissingDependencies {
    val all = map { task -> task.signature.returns }.toSet() + initial
    return associate { task -> task.signature.run { returns to args - all } }
        .filterValues { missing -> missing.isNotEmpty() }
}
