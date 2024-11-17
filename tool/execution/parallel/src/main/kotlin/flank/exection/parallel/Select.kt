package flank.execution.parallel

/**
 * Select value by type.
 */
@Suppress("UNCHECKED_CAST")
infix fun <T : Any> ParallelState.select(type: Parallel.Type<T>) = get(type) as T
