package flank.exection.parallel.internal

import flank.exection.parallel.Parallel.Task
import flank.exection.parallel.Parallel.Type
import flank.exection.parallel.Tasks
import flank.exection.parallel.internal.graph.findDependenciesIn

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
