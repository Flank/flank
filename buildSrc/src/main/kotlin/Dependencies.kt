object Kotlin {
    const val PLUGIN_JVM = "jvm"
    const val PLUGIN_SERIALIZATION = "plugin.serialization"
    const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.KOTLINX_SERIALIZATION}"
}

const val PLUGIN_SHADOW_JAR = "com.github.johnrengelman.shadow"
const val CLIKT = "com.github.ajalt:clikt:${Versions.CLIKT}"

object Fuel {
    const val CORE = "com.github.kittinunf.fuel:fuel:${Versions.FUEL}"
    const val COROUTINES = "com.github.kittinunf.fuel:fuel-coroutines:${Versions.FUEL}"
    const val KOTLINX_SERIALIZATION = "com.github.kittinunf.fuel:fuel-kotlinx-serialization:${Versions.FUEL}"
}

const val TRUTH = "com.google.truth:truth:${Versions.TRUTH}"
const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
const val JUNIT = "junit:junit:${Versions.JUNIT}"
const val SYSTEM_RULES = "com.github.stefanbirkner:system-rules:${Versions.SYSTEM_RULES}"
const val DETEKT_PLUGIN = "io.gitlab.arturbosch.detekt"
const val DETEKT_FORMATTING = "io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}"
