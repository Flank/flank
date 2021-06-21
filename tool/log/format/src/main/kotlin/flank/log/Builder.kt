package flank.log

import kotlin.reflect.KClass

/**
 * Factory method for building and creating generic [Formatter].
 */
fun <T> buildFormatter(build: Builder<T>.() -> Unit): Formatter<T> =
    Builder<T>().apply(build).run {
        @Suppress("UNCHECKED_CAST")
        Formatter(
            static = static as Map<StaticMatcher, Format<Any, T>>,
            dynamic = dynamic as Map<DynamicMatcher, Format<Any, T>>,
        )
    }

/**
 * Generic formatters builder.
 */
class Builder<T> internal constructor() {

    internal val static = mutableMapOf<StaticMatcher, Format<*, T>>()
    internal val dynamic = mutableMapOf<DynamicMatcher, Format<*, T>>()

    /**
     * Registers [V] formatter with [KClass] based static matcher.
     *
     * @receiver [KClass] as a identifier.
     * @param context Optional context.
     * @param format Reference to formatting function.
     */
    operator fun <V : Event.Data> KClass<V>.invoke(
        context: Any? = null,
        format: Format<V, T>,
    ) {
        static += listOfNotNull(context, java) to format
    }

    /**
     * Registers [V] formatter with [Event.Type] based static matcher.
     *
     * @receiver [Event.Type] as a identifier.
     * @param context Optional context.
     * @param format Reference to formatting function.
     */
    operator fun <V> Event.Type<V>.invoke(
        context: Any? = null,
        format: Format<V, T>
    ) {
        static += listOfNotNull(context, this) to format
    }

    /**
     * Creates matcher function.
     */
    fun <V> match(f: (Any.(Any) -> V?)) = f

    /**
     * Registers [V] formatter with dynamic matcher function.
     *
     * @receiver Matching event function. The receiver is a context where
     * @param format Reference to formatting function.
     */
    infix fun <V> (Any.(Any) -> V?).to(
        format: Format<V, T>
    ) {
        val wrap: DynamicMatcher = { invoke(this, it) != null }
        dynamic += wrap to format
    }
}
