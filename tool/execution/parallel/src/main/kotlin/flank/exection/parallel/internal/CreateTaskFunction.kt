package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState

/**
 * Abstract factory for creating task function.
 */
internal interface CreateTaskFunction<X : Parallel.Context> {
    val context: () -> X
    operator fun <T> invoke(body: suspend X.() -> T): suspend ParallelState.() -> T = {
        context().also { it.state = this }.run { body() }
    }
}
