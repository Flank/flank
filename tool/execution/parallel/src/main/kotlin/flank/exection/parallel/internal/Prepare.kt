package flank.execution.parallel.internal

import flank.execution.parallel.Parallel
import flank.execution.parallel.Tasks

/**
 * Get initial state validators.
 *
 * This is necessary to perform validations of initial state before the execution.
 */
internal fun Tasks.contextValidatorTypes(): List<Parallel.Context> =
    mapNotNull { task -> task.type as? Parallel.Context }

/**
 * Return graph without context validation tasks.
 *
 * Typically, context validation tasks should be used for testing purposes so running them on production is redundant.
 * This function can be used to filter them out.
 */
internal fun Tasks.minusContextValidators(): Tasks =
    filterNot { task -> task.type is Parallel.Context }.toSet()
