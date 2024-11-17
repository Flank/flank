package flank.execution.parallel.internal

import flank.execution.parallel.ParallelState

internal fun ParallelState.checkThrowableValues(): ParallelState =
    onEach { (_, value) -> if (value is Throwable) throw value }
