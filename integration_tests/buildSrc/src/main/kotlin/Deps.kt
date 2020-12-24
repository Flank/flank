object Versions {
    // https://github.com/detekt/detekt
    const val DETEKT = "1.11.0"

    // match to Tools -> Kotlin -> Configure Kotlin Plugin Updates -> Update Channel: Stable
    const val KOTLIN = "1.4.0"

    // https://github.com/Kotlin/kotlinx.coroutines/releases
    const val KOTLIN_COROUTINES = "1.3.9"
    const val JUNIT = "4.13"

    // https://github.com/mockk/mockk
    const val MOCKK = "1.10.0"

    // https://github.com/FasterXML/jackson-core/releases
    // https://github.com/FasterXML/jackson-dataformat-xml/releases
    const val JACKSON = "2.11.3"
}

object Libs {
    const val KOTLIN_COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLIN_COROUTINES}"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}"
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"

    //region Jackson
    const val JACKSON_KOTLIN = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}"
    const val JACKSON_XML = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.JACKSON}"
    //endregion

    //region Test Dependencies
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
    //endregion
}
