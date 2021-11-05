object Dependencies {
    const val SENTRY = "io.sentry:sentry:${Versions.SENTRY}"
    const val MIXPANEL = "com.mixpanel:mixpanel-java:${Versions.MIXPANEL}"
    const val DD_PLIST = "com.googlecode.plist:dd-plist:${Versions.DD_PLIST}"
    const val DEX_TEST_PARSER = "com.linkedin.dextestparser:parser:${Versions.DEX_TEST_PARSER}"
    const val APK_PARSER = "net.dongliu:apk-parser:${Versions.APK_PARSER}"

    const val GOOGLE_API_CLIENT = "com.google.api-client:google-api-client:${Versions.GOOGLE_API}"
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

    //region ktor
    const val KTOR_GSON = "io.ktor:ktor-gson:${Versions.KTOR}"
    const val KTOR_SERVER_CORE = "io.ktor:ktor-server-core:${Versions.KTOR}"
    const val KTOR_SERVER_NETTY = "io.ktor:ktor-server-netty:${Versions.KTOR}"
    const val KTOR_CLIENT_SERIALIZATION = "io.ktor:ktor-client-serialization:${Versions.KTOR}"
    const val KTOR_CLIENT_WEBSOCKETS = "io.ktor:ktor-client-websockets:${Versions.KTOR}"
    const val KTOR_CLIENT_LOGGING = "io.ktor:ktor-client-logging:${Versions.KTOR}"
    const val KTOR_CLIENT_CORE = "io.ktor:ktor-client-core:${Versions.KTOR}"
    const val KTOR_CLIENT_CIO = "io.ktor:ktor-client-cio:${Versions.KTOR}"
    //endregion ktor

    const val LOGBACK = "ch.qos.logback:logback-classic:${Versions.LOGBACK}"
    const val JBOSS_LOGGING = "org.jboss.logging:commons-logging-jboss-logging:${Versions.JBOSS_LOGGING}"

    const val WOODSTOX = "com.fasterxml.woodstox:woodstox-core:${Versions.WOODSTOX}"

    const val KOTLIN_LOGGING = "io.github.microutils:kotlin-logging:${Versions.KOTLIN_LOGGING}"

    const val JANSI = "org.fusesource.jansi:jansi:${Versions.JANSI}"

    const val PROGUARD = "com.guardsquare:proguard-gradle:${Versions.PROGUARD}"

    const val JSON = "org.json:json:${Versions.JSON}"

    //region Test Dependencies
    const val JSOUP = "org.jsoup:jsoup:${Versions.JSOUP}"
    const val JUNIT = "junit:junit:${Versions.JUNIT}"
    const val OKHTTP = "com.squareup.okhttp3:okhttp:${Versions.OKHTTP}"
    const val PICOCLI = "info.picocli:picocli:${Versions.PICOCLI}"
    const val PICOCLI_CODEGEN = "info.picocli:picocli-codegen:${Versions.PICOCLI}"
    const val SYSTEM_RULES = "com.github.stefanbirkner:system-rules:${Versions.SYSTEM_RULES}"
    const val TRUTH = "com.google.truth:truth:${Versions.TRUTH}"
    const val MOCKK = "io.mockk:mockk:${Versions.MOCKK}"
    const val KOTLIN_COROUTINES_TEST = "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.KOTLIN_COROUTINES}"
    //endregion

    const val COMMON_TEXT = "org.apache.commons:commons-text:${Versions.COMMON_TEXT}"
    const val KOTLIN_SERIALIZATION = "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.KOTLIN_SERIALIZATION}"

    //region flank-scripts
    const val ARCHIVE_LIB = "org.rauschig:jarchivelib:${Versions.ARCHIVE_LIB}"
    const val TUKAANI_XZ = "org.tukaani:xz:${Versions.TUKAANI_XZ}"
    const val CLIKT = "com.github.ajalt:clikt:${Versions.CLIKT}"
    const val JCABI_GITHUB = "com.jcabi:jcabi-github:${Versions.JCABI_GITHUB}"
    const val SLF4J_NOP = "org.slf4j:slf4j-nop:${Versions.SLF4J_NOP}"
    object Fuel {
        const val CORE = "com.github.kittinunf.fuel:fuel:${Versions.FUEL}"
        const val COROUTINES = "com.github.kittinunf.fuel:fuel-coroutines:${Versions.FUEL}"
        const val KOTLINX_SERIALIZATION = "com.github.kittinunf.fuel:fuel-kotlinx-serialization:${Versions.FUEL}"
    }
    const val GLASSFISH_JSON = "org.glassfish:javax.json:${Versions.GLASSFISH_JSON}"
    //endregion
}
