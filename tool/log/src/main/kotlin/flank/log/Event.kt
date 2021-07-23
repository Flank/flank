package flank.log

/**
 * Structural log representation
 *
 * @property context Any object that can be treated as a context for a group of events.
 * @property type Unique identifier for value. Could be a KClass of [Event.Type] depending on value type.
 * @property value Logged value.
 * @property timestamp Time when the event occurs. Unix Timestamp in milliseconds.
 */
data class Event<V : Any> internal constructor(
    val context: Any,
    val type: Any,
    val value: V,
    val timestamp: Long = System.currentTimeMillis(),
) {
    /**
     * Interface of event data identified by KClass
     */
    interface Data

    /**
     * Interface of unique key for [T] event data.
     */
    interface Type<T> {
        operator fun invoke(value: T) = this to value
    }

    /**
     * Notify task was started
     */
    object Start : Type<Unit>

    /**
     * Notify task was stopped
     */
    object Stop : Type<Unit>
}

/**
 * Creates [Event] from [this] context and given [any] type or value.
 */
infix fun Any.event(any: Any): Event<out Any> = when (any) {
    is Event<*> -> any
    is Pair<*, *> -> Event(this, any.first!!, any.second!!)
    is Event.Data -> Event(this, any::class.java, any)
    is Event.Type<*> -> Event(this, any, Unit)
    else -> Event(this, any::class.java, any) // Consider disallow unknown types.
}
