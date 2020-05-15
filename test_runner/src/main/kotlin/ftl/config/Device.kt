package ftl.config

import ftl.config.FtlConstants.defaultLocale
import ftl.config.FtlConstants.defaultOrientation
import ftl.util.trimStartLine

data class Device(
    val model: String,
    val version: String,
    val locale: String = defaultLocale,
    val orientation: String = defaultOrientation
) {

    override fun toString(): String {
        return """
        - model: $model
          version: $version
          locale: $locale
          orientation: $orientation""".trimStartLine()
    }
}
