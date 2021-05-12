package ftl.util

@Suppress("UNCHECKED_CAST")
fun <T> Any.asList(): List<T> = this as List<T>

@Suppress("UNCHECKED_CAST")
fun <T> Any.asListOrNull(): List<T>? = this as? List<T>
