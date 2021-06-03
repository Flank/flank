package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState

@Suppress("UNCHECKED_CAST")
internal fun <T : Any> Parallel.Store.lazy(
    type: Parallel.Type<T>
): Lazy<T> = lazy { state[type] as T }

internal interface CreateFunction<S : Parallel.Store> {
    val store: () -> S
    operator fun <T> invoke(create: suspend S.() -> T): suspend ParallelState.() -> T = {
        store().also { it.state = this }.run { create() }
    }
}
