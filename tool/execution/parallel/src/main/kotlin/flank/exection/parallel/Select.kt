package flank.exection.parallel

/**
 * Select value by type.
 */
@Suppress("UNCHECKED_CAST")
fun <T : Any> ParallelState.select(type: Parallel.Type<T>) = get(type) as T
