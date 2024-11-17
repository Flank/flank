package flank.execution.parallel

import flank.execution.parallel.internal.DynamicType
import flank.execution.parallel.internal.EagerProperties

// ======================= Signature =======================

/**
 * Factory function for creating [Parallel.Task.Signature] from expected type and argument types.
 */
infix fun <T : Any> Parallel.Type<T>.from(
    args: Set<Parallel.Type<*>>
): Parallel.Task.Signature<T> =
    Parallel.Task.Signature(this, args)

// ======================= Task =======================

/**
 * Factory function for creating [Parallel.Task] from signature and execute function.
 */
infix fun <R : Any> Parallel.Task.Signature<R>.using(
    execute: suspend ParallelState.() -> R
): Parallel.Task<R> =
    Parallel.Task(this, execute)

/**
 * Factory function for creating [Parallel.Task] from expected type and execute function without specifying required arguments.
 */
infix fun <R : Any> Parallel.Type<R>.using(
    execute: suspend ParallelState.() -> R
): Parallel.Task<R> =
    from(emptySet()).using(execute)

// ======================= Validator =======================

/**
 * Factory function for creating special task that can validate arguments before execution.
 * The [Parallel.Context] with added [EagerProperties] can validate the state is containing required initial values.
 */
internal fun <C : Parallel.Context> validator(
    context: (() -> C)
): Parallel.Task<Unit> =
    context() using { context().also { it.state = this }.run { validate() } }

// ======================= Type =======================

/**
 * Factory function for creating dynamic [Parallel.Type].
 */
inline fun <reified T : Any> type(): Parallel.Type<T> = type(T::class.java)

/**
 * Factory function for creating dynamic [Parallel.Type].
 */
fun <T : Any> type(type: Class<T>): Parallel.Type<T> = DynamicType(type)

// ======================= Property =======================

/**
 * Factory function for creating property with value of specified type.
 */
operator fun <T : Any> Parallel.Type<T>.plus(value: T) = this to value
