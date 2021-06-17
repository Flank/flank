package flank.log

/**
 * Logger with provided output.
 */
interface Logger {
    val out: Output
}

/**
 * Logging function signature.
 */
typealias Output = Any.() -> Unit

/**
 * Generic logging function signature.
 */
typealias GenericOutput<T> = T.() -> Unit

/**
 * Normalizes [GenericOutput] into [Output] using [transform] function.
 */
fun <T> GenericOutput<T>.normalize(transform: (Any) -> T?): Output =
    { transform(this)?.let(this@normalize) }

/**
 * Normalizes [GenericOutput] into [Output] by filtering values of type [T]
 */
inline fun <reified T> GenericOutput<T>.filter(): Output =
    normalize { this as? T }
