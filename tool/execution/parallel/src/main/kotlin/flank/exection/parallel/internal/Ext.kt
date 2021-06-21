package flank.exection.parallel.internal

import flank.exection.parallel.Parallel

internal val <R : Any> Parallel.Task<R>.type get() = signature.type
internal val <R : Any> Parallel.Task<R>.args get() = signature.args
