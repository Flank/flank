package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState
import flank.exection.parallel.Tasks

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

tailrec fun <C : Parallel.Context> Tasks<C>.subgraph(
    current: Tasks<C>,
    acc: Tasks<C> =
        if (current.isEmpty()) this
        else emptySet(),
): Tasks<C> = when {
    current.isEmpty() -> acc
    else -> subgraph(
        current = current.flatMap { it.signature.args }
            .mapNotNull(this::findByReturn)
            .toSet(),
        acc = acc + current
    )
}

fun <C : Parallel.Context> Tasks<C>.findByReturn(
    type: Parallel.Type<*>
) = find { it.signature.returns == type }
