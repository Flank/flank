package flank.execution.parallel.internal

import flank.execution.parallel.Parallel
import flank.execution.parallel.ParallelState

/**
 * Factory function for lazy property delegate.
 */
internal fun <T : Any, R> Parallel.Context.lazyProperty(
    type: Parallel.Type<T>,
    select: T.() -> R
): Lazy<R> = lazy {
    fun errorMessage() = "Cannot resolve dependency of type: $type. Make sure is specified as argument"
    @Suppress("UNCHECKED_CAST")
    (state[type] as? T ?: throw IllegalStateException(errorMessage())).select()
}

internal fun <T : Any> Parallel.Context.lazyProperty(
    type: Parallel.Type<T>
): Lazy<T> =
    lazyProperty(type) { this }

/**
 * Eager properties provider and initializer.
 */
class EagerProperties(
    private val state: () -> ParallelState
) {
    private val props = mutableSetOf<Lazy<*>>()

    /**
     * Initialize eager properties, this performs also validation.
     */
    operator fun invoke(): Unit = props.forEach { prop -> prop.value }

    /**
     * Register new parallel type. Inline modifier is necessary to perform real type check
     */
    inline operator fun <reified T : Any> invoke(type: Parallel.Type<T>): Lazy<T> = lazy { type.value() as T }.append()

    // local helpers need to be public because of inlined invoke
    fun <T> Lazy<T>.append(): Lazy<T> = apply { props += this }
    fun <T : Any> Parallel.Type<T>.value(): Any? = state()[this]
}
