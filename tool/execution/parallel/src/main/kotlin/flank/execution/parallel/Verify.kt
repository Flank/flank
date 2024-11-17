package flank.execution.parallel

import flank.execution.parallel.internal.checkThrowableValues

/**
 * Verify that given [ParallelState] has no errors.
 */
fun ParallelState.verify(): ParallelState = checkThrowableValues()
