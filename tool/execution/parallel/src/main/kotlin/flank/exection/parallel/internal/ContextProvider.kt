package flank.exection.parallel.internal

import flank.exection.parallel.ExecuteTask
import flank.exection.parallel.Parallel

/**
 * Abstract factory for creating task function.
 */
internal interface ContextProvider<X : Parallel.Context> {
    val context: () -> X
    operator fun <R> invoke(body: suspend X.() -> R): ExecuteTask<R> =
        { context().also { it.state = this }.body() }
}
