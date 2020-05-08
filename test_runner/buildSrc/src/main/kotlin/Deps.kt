object Versions {
    // https://github.com/bugsnag/bugsnag-java/releases
    const val BUGSNAG = "3.6.1"

    // https://github.com/3breadt/dd-plist/releases
    const val DD_PLIST = "1.22"

    // https://github.com/arturbosch/detekt/releases
    const val DETEKT = "1.1.0"

    // https://github.com/bintray/gradle-bintray-plugin/releases
    const val BINTRAY = "1.8.5"

    // https://github.com/johnrengelman/shadow/releases
    const val SHADOW = "5.2.0"

    // https://github.com/linkedin/dex-test-parser/releases
    const val DEX_TEST_PARSER = "2.1.1"

    // match to Tools -> Kotlin -> Configure Kotlin Plugin Updates -> Update Channel: Stable
    const val KOTLIN = "1.3.72"

    // https://github.com/Kotlin/kotlinx.coroutines/releases
    const val KOTLIN_COROUTINES = "1.3.2"

    // https://github.com/remkop/picocli/releases
    const val PICOCLI = "4.2.0"

    // https://search.maven.org/search?q=a:google-api-services-toolresults%20g:com.google.apis
    const val GOOGLE_API_TOOLRESULTS = "v1beta3-rev20190923-1.30.3"

    // https://github.com/googleapis/google-auth-library-java/releases
    // NOTE: https://github.com/googleapis/google-oauth-java-client is End of Life and replaced by google-auth-library-java
    // https://github.com/googleapis/google-oauth-java-client/issues/251#issuecomment-504565533
    const val GOOGLE_AUTH = "0.18.0"

    // https://search.maven.org/search?q=a:google-cloud-nio%20g:com.google.cloud
    const val GOOGLE_NIO = "0.114.0-alpha"

    // https://search.maven.org/search?q=a:google-cloud-storage%20g:com.google.cloud
    const val GOOGLE_STORAGE = "1.96.0"

    // https://github.com/google/gson/releases
    const val GSON = "2.8.6"

    // https://github.com/FasterXML/jackson-core/releases
    // https://github.com/FasterXML/jackson-dataformat-xml/releases
    const val JACKSON = "2.10.0"

    const val JUNIT = "4.12"

    // https://github.com/jhy/jsoup/releases
    const val JSOUP = "1.12.1"

    // https://github.com/ktorio/ktor/releases
    const val KTOR = "1.2.5"

    // https://github.com/qos-ch/logback/releases
    const val LOGBACK = "1.2.3"

    // https://github.com/square/okhttp/releases
    const val OKHTTP = "4.2.2"

    // https://github.com/stefanbirkner/system-rules/releases
    const val SYSTEM_RULES = "1.19.0"

    // https://github.com/google/truth/releases
    const val TRUTH = "1.0"

    // https://github.com/FasterXML/woodstox/releases
    const val WOODSTOX = "6.0.1"

    const val KOTLIN_LOGGING = "1.7.8"

    // https://github.com/mockk/mockk
    const val MOCKK = "1.9.3"

    //https://commons.apache.org/proper/commons-text/
    const val COMMON_TEXT = "1.8"
}

object Libs {
    const val BUGSNAG = "com.bugsnag:bugsnag:${Versions.BUGSNAG}"

    const val DD_PLIST = "com.googlecode.plist:dd-plist:${Versions.DD_PLIST}"
    const val DEX_TEST_PARSER = "com.linkedin.dextestparser:parser:${Versions.DEX_TEST_PARSER}"

    const val GOOGLE_AUTH = "com.google.auth:google-auth-library-oauth2-http:${Versions.GOOGLE_AUTH}"
    const val GOOGLE_NIO = "com.google.cloud:google-cloud-nio:${Versions.GOOGLE_NIO}"
    const val GOOGLE_STORAGE = "com.google.cloud:google-cloud-storage:${Versions.GOOGLE_STORAGE}"
    const val GOOGLE_TOOLRESULTS = "com.google.apis:google-api-services-toolresults:${Versions.GOOGLE_API_TOOLRESULTS}"

    const val GSON = "com.google.code.gson:gson:${Versions.GSON}"

    // https://search.maven.org/search?q=a:jackson-databind%20g:com.fasterxml.jackson.core
    const val JACKSON_DATABIND = "com.fasterxml.jackson.core:jackson-databind:${Versions.JACKSON}"

    const val JACKSON_KOTLIN = "com.fasterxml.jackson.module:jackson-module-kotlin:${Versions.JACKSON}"
    const val JACKSON_YAML = "com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:${Versions.JACKSON}"
    const val JACKSON_XML = "com.fasterxml.jackson.dataformat:jackson-dataformat-xml:${Versions.JACKSON}"

    const val KOTLIN_COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.KOTLIN_COROUTINES}"
    const val KOTLIN_REFLECT = "org.jetbrains.kotlin:kotlin-reflect:${Versions.KOTLIN}"
    const val KOTLIN_STD_LIB = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.KOTLIN}"

    const val KTOR_GSON = "io.ktor:ktor-gson:${Versions.KTOR}"
    const val KTOR_SERVER_CORE = "io.ktor:ktor-server-core:${Versions.KTOR}"
    const val KTOR_SERVER_NETTY = "io.ktor:ktor-server-netty:${Versions.KTOR}"
    const val LOGBACK = "ch.qos.logback:logback-classic:${Versions.LOGBACK}"

    const val WOODSTOX = "com.fasterxml.woodstox:woodstox-core:${Versions.WOODSTOX}"

    const val KOTLIN_LOGGING = "io.github.microutils:kotlin-logging:${Versions.KOTLIN_LOGGING}"

    //region Plugins
    const val DETEKT_FORMATTING = "io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}"
    //endregion

    //region Test Dependencies
    const val JSOUP = "org.jsoup:jsoup:${Versions.JSOUP}"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
    const val PICOCLI = "info.picocli:picocli:${Versions.PICOCLI}"
    const val PICOCLI_CODEGEN = "info.picocli:picocli-codegen:${Versions.PICOCLI}"
    const val SYSTEM_RULES = "com.github.stefanbirkner:system-rules:${Versions.SYSTEM_RULES}"
    const val TRUTH = "com.google.truth:truth:${Versions.TRUTH}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
    //endregion

    const val COMMON_TEXT = "org.apache.commons:commons-text:${Versions.COMMON_TEXT}"
}
