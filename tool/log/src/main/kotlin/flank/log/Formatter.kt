package flank.log

class Formatter internal constructor(
    val static: Map<StaticMatcher, ToString<Any>>,
    val dynamic: Map<DynamicMatcher, ToString<Any>>,
)

internal typealias StaticMatcher = List<Any>
internal typealias DynamicMatcher = Any.(Any) -> Boolean
internal typealias ToString<V> = V.(Any) -> String?


operator fun Formatter.invoke(event: Event<*>): String? = event.run {
    val format = static[listOf(context, type)]
        ?: static[listOf(type)]
        ?: dynamic.toList().firstOrNull { (match, _) -> match.invoke(context, value) }?.second

    format?.invoke(value, context)
}

val Formatter.output: Output get() = output(::println)

fun Formatter.output(log: (String) -> Unit): Output = { (this as? Event<*>)?.let { invoke(it)?.let(log) } }

