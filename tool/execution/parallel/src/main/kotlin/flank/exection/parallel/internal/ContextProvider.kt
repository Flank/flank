package flank.exection.parallel.internal

import flank.exection.parallel.ExecuteTask
import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.validator

/**
 * Abstract factory for wrapping state into context and creating task function.
 */
abstract class ContextProvider<X : Parallel.Context>
internal constructor() : (ParallelState) -> X {
    protected abstract val context: () -> X

    override fun invoke(state: ParallelState): X =
        context().also { it.state = state }

    operator fun <R> invoke(body: suspend X.(ParallelState) -> R): ExecuteTask<R> =
        { invoke(this).body(this) }

    val validate by lazy { validator(context) }
}
