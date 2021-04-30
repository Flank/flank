package ftl.presentation

import flank.common.logLn

/**
 * The abstraction which allows passing output from the domain to presentation.
 * Implement in domain top-level interfaces for getting access to outputting result structures.
 * @property out The reference to outputting result function.
 */
interface Output {
    val out: Any.() -> Unit
}

fun outputLogger(map: Any.() -> String): Any.() -> Unit = {
    logLn(map())
}

fun Any.throwUnknownType(): Nothing =
    throw IllegalArgumentException(javaClass.toGenericString())
