package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.Tasks
import flank.exection.parallel.internal.graph.findCycles
import flank.exection.parallel.internal.graph.findDuplicatedDependencies
import flank.exection.parallel.internal.graph.findMissingDependencies
import kotlinx.coroutines.runBlocking

// TODO: Check all cases and collect results, instead of throwing the first encountered error.
internal fun Tasks.validateExecutionGraphs(initial: ParallelState = emptyMap()): Tasks = run {
    // Separate initial validators from tasks. Validators are important now but not during the execution.
    val (validators, tasks) = this
        .groupBy { task -> task.type is Parallel.Context }
        .run { getOrDefault(true, emptyList()) to getOrDefault(false, emptyList()) }

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
