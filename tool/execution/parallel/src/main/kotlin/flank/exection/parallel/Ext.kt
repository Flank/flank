package flank.exection.parallel

/**
 * Extension for executing tasks on a given state.
 */
operator fun Tasks.invoke(
    vararg state: Pair<Parallel.Type<*>, Any>
) = invoke(state.toMap())

/**
 * Extension for reducing tasks to returned with dependencies.
 */
operator fun Tasks.invoke(
    vararg returns: Parallel.Type<*>
) = invoke(returns.toSet())
