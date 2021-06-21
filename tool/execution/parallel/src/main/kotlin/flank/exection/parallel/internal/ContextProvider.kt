package flank.exection.parallel.internal

import flank.exection.parallel.ExecuteTask
import flank.exection.parallel.Parallel
import flank.exection.parallel.validator

/**
 * Abstract factory for creating task function.
 */
abstract class ContextProvider<X : Parallel.Context> {
    protected abstract val context: () -> X

    operator fun <R> invoke(body: suspend X.() -> R): ExecuteTask<R> =
        { context().also { it.state = this }.body() }

    val validator by lazy { validator(context) }
}
