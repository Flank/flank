package flank.exection.parallel.internal

import flank.exection.parallel.Parallel

open class CompositeType
internal constructor(val types: Set<Parallel.Type<*>>) {
    internal constructor(types: Array<out Parallel.Type<*>>) : this(types.toSet())
}
