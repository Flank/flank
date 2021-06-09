package flank.exection.parallel

import flank.exection.parallel.internal.EagerProperties

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
 * Typically the [Parallel.Context] with added [EagerProperties] is used to validate initial state.
 */
internal fun <C : Parallel.Context> validator(
    context: (() -> C)
): Parallel.Task<Unit> =
    context() using { context().also { it.state = this }.run { validate() } }
