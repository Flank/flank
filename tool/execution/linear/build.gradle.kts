import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    api(project(":tool:log"))
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    testImplementation(Dependencies.JUNIT)
}
