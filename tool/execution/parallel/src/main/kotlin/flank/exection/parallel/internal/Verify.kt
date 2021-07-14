package flank.exection.parallel.internal

import flank.exection.parallel.ParallelState

internal fun ParallelState.checkThrowableValues(): ParallelState =
    onEach { (_, value) -> if (value is Throwable) throw value }
