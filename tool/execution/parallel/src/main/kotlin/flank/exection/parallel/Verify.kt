package flank.exection.parallel

import flank.exection.parallel.internal.checkThrowableValues

/**
 * Verify that given [ParallelState] has no errors.
 */
fun ParallelState.verify(): ParallelState = checkThrowableValues()
