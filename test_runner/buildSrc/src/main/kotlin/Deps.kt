object Versions {
    // match to Tools -> Kotlin -> Configure Kotlin Plugin Updates -> Update Channel: Stable
    val KOTLIN = "1.3.11"
    // https://github.com/Kotlin/kotlinx.coroutines/releases
    val KOTLIN_COROUTINES = "1.1.0"
    // https://github.com/FasterXML/jackson-core/releases
    val JACKSON = "2.9.8"
    // https://github.com/arturbosch/detekt
    val DETEKT = "1.0.0-RC12"
}

object Plugins {
    val KOTLIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:" + Versions.KOTLIN
}

object Libs {
    val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:" + Versions.KOTLIN
    val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:" + Versions.KOTLIN
    val KOTLIN_COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:" + Versions.KOTLIN_COROUTINES
    val JUNIT = "junit:junit:4.12"
}
