package flank.exection.parallel.internal

import flank.exection.parallel.Parallel
import flank.exection.parallel.ParallelState

/**
 * Factory function for lazy property delegate.
 */
internal fun <T : Any> Parallel.Context.lazyProperty(type: Parallel.Type<T>) = lazy {
    state[type] as T
}

/**
 * Eager properties provider and initializer.
 */
class EagerProperties(
    private val state: () -> ParallelState
) {
    private val set = mutableSetOf<Lazy<*>>()

    /**
     * Initialize eager properties, this performs also validation.
     */
    operator fun invoke(): Unit = set.forEach { prop -> println(prop.value) }

    /**
     * Register new parallel type. Inline modifier is necessary to perform real type check
     */
    inline operator fun <reified T : Any> invoke(type: Parallel.Type<T>): Lazy<T> = lazy { type.value() as T }.append()

    // local helpers need to be public because of inlined invoke
    fun <T> Lazy<T>.append(): Lazy<T> = apply { set.plusAssign(this) }
    fun <T : Any> Parallel.Type<T>.value(): Any? = state()[this]
}
