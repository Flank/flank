package flank.exection.parallel

import flank.exection.parallel.internal.args
import flank.exection.parallel.internal.graph.findCycles
import flank.exection.parallel.internal.graph.findDuplicatedDependencies
import flank.exection.parallel.internal.graph.findMissingDependencies
import flank.exection.parallel.internal.type
import kotlinx.coroutines.runBlocking

/**
 * Validate the given [Tasks] and [ParallelState] for finding missing dependencies or broken paths.
 *
 * @param initial The initial arguments for tasks execution.
 * @return Valid [Tasks] if graph has no broken paths or missing dependencies.
 */
fun Tasks.validate(initial: ParallelState = emptyMap()): Tasks = run {
    // Separate initial validators from tasks. Validators are important now but not during the execution.
    val (validators, tasks) = splitTasks()

    // check if initial state is providing all required values specified in context.
    runBlocking { validators.forEach { check -> check.execute(initial) } }

    map(Parallel.Task<*>::type).findDuplicatedDependencies(initial.keys).run {
        if (isNotEmpty()) throw Parallel.DependenciesError.Duplicate(this)
    }

    val graph = associate { task -> task.type to task.args }

    graph.findMissingDependencies(initial.keys).run {
        if (isNotEmpty()) throw Parallel.DependenciesError.Missing(this)
    }

    graph.findCycles().run {
        if (isNotEmpty()) throw Parallel.DependenciesError.Cycles(this)
    }

    tasks.toSet()
}

private fun Iterable<Parallel.Task<*>>.splitTasks() = this
    .groupBy { task -> task.type is Parallel.Context }
    .run { getOrDefault(true, emptyList()) to getOrDefault(false, emptyList()) }

