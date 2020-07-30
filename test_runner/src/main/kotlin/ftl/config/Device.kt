package ftl.config

import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.config.FtlConstants.defaultLocale
import ftl.config.FtlConstants.defaultOrientation
import ftl.util.trimStartLine

data class Device(
    val model: String,
    val version: String,
    val locale: String = defaultLocale,
    val orientation: String = defaultOrientation,
    val isVirtual: Boolean? = null
) {
    override fun toString(): String {
        return """
        - model: $model
          version: $version
          locale: $locale
          orientation: $orientation""".trimStartLine()
    }
}

fun defaultDevice(android: Boolean) = Device(
    model = if (android) defaultAndroidModel else defaultIosModel,
    version = if (android) defaultAndroidVersion else defaultIosVersion
)

fun Map<String, String>.asDevice(android: Boolean) =
    if (isEmpty()) null
    else defaultDevice(android).run {
        copy(
            model = getOrDefault("model", model),
            version = getOrDefault("version", version),
            locale = getOrDefault("locale", version),
            orientation = getOrDefault("orientation", version)
        )
    }

fun List<Device>.containsVirtualDevices() = any { it.isVirtual ?: false }

fun List<Device>.containsPhysicalDevices() = any { (it.isVirtual ?: false).not() }
