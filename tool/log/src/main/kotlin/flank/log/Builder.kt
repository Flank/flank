package flank.log

import kotlin.reflect.KClass

/**
 * Factory method for building and creating formatter.
 */
fun buildFormatter(build: Builder.() -> Unit): Formatter =
    Builder().apply(build).run {
        @Suppress("UNCHECKED_CAST")
        Formatter(
            static = static as Map<StaticMatcher, ToString<Any>>,
            dynamic = dynamic as Map<DynamicMatcher, ToString<Any>>,
        )
    }

/**
 * Log formatters builder.
 */
class Builder internal constructor() {

    internal val static = mutableMapOf<StaticMatcher, ToString<*>>()
    internal val dynamic = mutableMapOf<DynamicMatcher, ToString<*>>()

    /**
     * Registers [V] formatter with [KClass] based static matcher.
     *
     * @receiver [KClass] as a identifier.
     * @param context Optional context.
     * @param toString Reference to formatting function.
     */
    operator fun <V : Event.Data> KClass<V>.invoke(
        context: Any? = null,
        toString: ToString<V>,
    ) {
        static += listOfNotNull(context, java) to toString
    }

    /**
     * Registers [V] formatter with [Event.Type] based static matcher.
     *
     * @receiver [Event.Type] as a identifier.
     * @param context Optional context.
     * @param toString Reference to formatting function.
     */
    operator fun <V> Event.Type<V>.invoke(
        context: Any? = null,
        toString: ToString<V>
    ) {
        static += listOfNotNull(context, this) to toString
    }

    /**
     * Creates matcher function.
     */
    fun <V> match(f: (Any.(Any) -> V?)) = f

    /**
     * Registers [V] formatter with dynamic matcher function.
     *
     * @receiver Matching event function. The receiver is a context where
     * @param toString Reference to formatting function.
     */
    infix fun <V> (Any.(Any) -> V?).to(
        toString: ToString<V>
    ) {
        val wrap: DynamicMatcher = { invoke(this, it) != null }
        dynamic += wrap to toString
    }
}
