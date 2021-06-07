package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState

/**
 * Lazy accessor to the state value for a give type.
 */
@Suppress("UNCHECKED_CAST")
internal fun <T : Any> Parallel.Context.lazy(
    type: Parallel.Type<T>
): Lazy<T> = lazy { state[type] as T }

/**
 * Abstract factory for creating task function.
 */
internal interface CreateTaskFunction<X : Parallel.Context> {
    val context: () -> X
    operator fun <T> invoke(body: suspend X.() -> T): suspend ParallelState.() -> T =
        { context().also { it.state = this }.run { body() } }
}
