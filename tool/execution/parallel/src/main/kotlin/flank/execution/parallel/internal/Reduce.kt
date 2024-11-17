package flank.execution.parallel.internal

import flank.execution.parallel.Parallel.Task
import flank.execution.parallel.Parallel.Type
import flank.execution.parallel.Tasks
import flank.execution.parallel.internal.graph.findDependenciesIn

internal infix fun Tasks.reduceTo(
    expected: Set<Type<*>>
): Tasks {
    val notFound = expected - map(Task<*>::type)
    if (notFound.isNotEmpty()) throw Exception("Cannot find tasks for the following types: $notFound")
    val graph: Map<Type<*>, Set<Type<*>>> = associate { task -> task.type to task.args }
    val dependencies = expected.findDependenciesIn(graph)
    return mapNotNull { task ->
        when (task.type) {
            in expected -> task
            in dependencies -> task.copy(expected = false)
            else -> null
        }
    }.toSet()
}
