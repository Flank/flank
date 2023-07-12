package ftl.config

import flank.common.trimStartLine
import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.config.FtlConstants.defaultLocale
import ftl.config.FtlConstants.defaultOrientation

data class Device(
    val model: String,
    val version: String,
    val locale: String = defaultLocale,
    val orientation: String = defaultOrientation,
    val isVirtual: Boolean = false
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

fun List<Device>.containsVirtualDevices() = any { it.isVirtual }

fun List<Device>.containsPhysicalDevices() = any { !it.isVirtual }

fun List<Device>.containsArmDevices() = any { it.model.endsWith(".arm") }

fun List<Device>.containsNonArmDevices() = any { !it.model.endsWith(".arm") }
