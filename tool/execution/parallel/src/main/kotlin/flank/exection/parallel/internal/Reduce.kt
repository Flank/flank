package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.Tasks

internal infix fun Tasks.reduceTo(
    selectedTypes: Set<Parallel.Type<*>>
): Tasks =
    filter { task -> task.type in selectedTypes }
        .toSet()
        .apply {
            val notFound = selectedTypes - map(Parallel.Task<*>::type)
            if (notFound.isNotEmpty()) throw Exception("Cannot reduce find tasks for the following types: $notFound")
        }
        .reduce(this)

/**
 * Reduce [all] steps to given receiver steps and their dependencies.
 *
 * @receiver The task selector for current reducing step.
 * @param all The list of all tasks that are under reducing.
 * @param acc Currently accumulated tasks.
 * @return Accumulated tasks if selector is empty.
 */
private tailrec fun Tasks.reduce(
    all: Tasks,
    acc: Tasks =
        if (isEmpty()) all
        else emptySet(),
): Tasks =
    when {
        isEmpty() -> acc
        else -> flatMap(Parallel.Task<*>::args)
            .mapNotNull(all::findByReturnType)
            .toSet()
            .reduce(all, acc + this)
    }

private fun Tasks.findByReturnType(
    type: Parallel.Type<*>
): Parallel.Task<*>? =
    find { task -> task.type == type }
