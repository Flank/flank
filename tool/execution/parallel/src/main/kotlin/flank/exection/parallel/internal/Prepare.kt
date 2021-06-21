package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.Tasks

/**
 * Get initial state validators.
 * This is necessary to perform validations of initial state before the execution.
 */
internal fun Tasks.contextValidators(): List<Parallel.Context> =
    mapNotNull { task -> task.type as? Parallel.Context }
