package flank.execution.parallel.internal

import flank.execution.parallel.ExecuteTask
import flank.execution.parallel.Parallel
import flank.execution.parallel.ParallelState
import flank.execution.parallel.validator

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
