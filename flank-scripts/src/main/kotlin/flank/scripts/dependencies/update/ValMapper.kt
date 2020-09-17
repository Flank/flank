package flank.scripts.dependencies.update

fun List<String>.matchingVersionVal(name: String) =
    find { it.contains(name) }?.findValName() ?: PLUGIN_VERSION

private fun String.findValName() = versionRegex.find(this)
    ?.value
    ?.split('.')
    ?.last()
    ?.replace("}\"", "")
    ?: INLINE_VERSION

private val versionRegex = "(\\$\\{Versions\\.).*}\"".toRegex()
private const val INLINE_VERSION = "!inlineVersion"
private const val PLUGIN_VERSION = "!pluginVersion"
