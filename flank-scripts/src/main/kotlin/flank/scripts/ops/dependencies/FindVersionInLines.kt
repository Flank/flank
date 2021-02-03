package flank.scripts.ops.dependencies

fun List<String>.matchingVersionVal(name: String) =
    find { it.contains(name) }?.findValName() ?: NOT_FOUND_VERSION

private fun String.findValName() = versionRegex.find(this)
    ?.value
    ?.split('.')
    ?.last()
    ?.replace("}\"", "")
    ?: NOT_FOUND_VERSION

private val versionRegex = "(\\$\\{Versions\\.).*}\"".toRegex()
private const val NOT_FOUND_VERSION = "!versionNotFound"
