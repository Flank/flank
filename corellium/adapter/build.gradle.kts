import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(project(":corellium:api"))
    implementation(project(":corellium:client-v2"))

    implementation(Dependencies.KOTLIN_SERIALIZATION)
    implementation(Dependencies.KTOR_CLIENT_SERIALIZATION)
    implementation(Dependencies.KTOR_CLIENT_WEBSOCKETS)
    implementation(Dependencies.KTOR_CLIENT_LOGGING)
    implementation(Dependencies.KTOR_CLIENT_CORE)
    implementation(Dependencies.KTOR_CLIENT_CIO)
    implementation(Dependencies.LOGBACK)

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TRUTH)
}
