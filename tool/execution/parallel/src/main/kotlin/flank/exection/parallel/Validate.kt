package flank.execution.parallel

import flank.execution.parallel.internal.validateExecutionGraphs

/**
 * Validate the given [Tasks] and [ParallelState] for finding missing dependencies or broken paths.
 *
 * @param initial The initial arguments for tasks execution.
 * @return Valid [Tasks] if graph has no broken paths or missing dependencies.
 */
fun Tasks.validate(initial: ParallelState = emptyMap()): Tasks =
    validateExecutionGraphs(initial)
