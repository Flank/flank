package flank.log

/**
 * Generic formatter.
 */
class Formatter<T> internal constructor(
    val static: Map<StaticMatcher, Format<Any, T>>,
    val dynamic: Map<DynamicMatcher, Format<Any, T>>,
)

internal typealias StaticMatcher = List<Any>
internal typealias DynamicMatcher = Any.(Any) -> Boolean
internal typealias Format<V, T> = V.(Any) -> T?

/**
 * Format given [Event] into [T].
 */
operator fun <T> Formatter<T>.invoke(event: Event<*>): T? = event.run {
    val format = static[listOf(context, type)]
        ?: static[listOf(type)]
        ?: dynamic.toList().firstOrNull { (match, _) -> match.invoke(context, value) }?.second

    format?.invoke(value, context)
}

/**
 * Creates [println] log [Output] from a given [Formatter].
 */
val Formatter<String>.output: Output get() = output(::println).filter()

/**
 * Creates [GenericOutput] for [log] using a given [Formatter].
 *
 * Conditionally invoking given [log] with formatted values.
 */
fun <T> Formatter<T>.output(log: (T) -> Unit): GenericOutput<Event<*>> = { invoke(this)?.let(log) }
