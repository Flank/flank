package ftl.config

import ftl.util.Utils.trimStartLine

data class Device(
    val model: String,
    val version: String,
    val locale: String = "en",
    val orientation: String = "portrait"
) {

    override fun toString(): String {
        return """
        - model: $model
          version: $version
          locale: $locale
          orientation: $orientation""".trimStartLine()
    }
}
