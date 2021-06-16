package flank.log

import kotlin.reflect.KClass

fun buildFormatter(build: Builder.() -> Unit): Formatter =
    Builder().apply(build).run {
        @Suppress("UNCHECKED_CAST")
        Formatter(
            static = static as Map<StaticMatcher, ToString<Any>>,
            dynamic = dynamic as Map<DynamicMatcher, ToString<Any>>,
        )
    }

class Builder internal constructor() {

    internal val static = mutableMapOf<StaticMatcher, ToString<*>>()
    internal val dynamic = mutableMapOf<DynamicMatcher, ToString<*>>()

    operator fun <V : Event.Data> KClass<V>.invoke(
        context: Any? = null,
        toString: ToString<V>,
    ) {
        static += listOfNotNull(context, java) to toString
    }

    operator fun <V> Event.Type<V>.invoke(
        context: Any? = null,
        toString: ToString<V>
    ) {
        static += listOfNotNull(context, this) to toString
    }

    fun <V> match(f: (Any.(Any) -> V?)) = f

    infix fun <V> (Any.(Any) -> V?).to(
        toString: ToString<V>
    ) {
        val wrap: DynamicMatcher = { invoke(this, it) != null }
        dynamic += wrap to toString
    }

}
