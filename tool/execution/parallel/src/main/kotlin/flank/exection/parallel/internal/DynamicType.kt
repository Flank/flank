package flank.execution.parallel.internal

import flank.execution.parallel.Parallel

internal class DynamicType<T : Any>(val type: Class<T>) : Parallel.Type<T> {
    override fun equals(other: Any?): Boolean = (other as? DynamicType<*>)?.type == type
    override fun hashCode(): Int = type.hashCode() + javaClass.hashCode()
    override fun toString(): String = type.canonicalName
}
