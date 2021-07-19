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
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    implementation(Dependencies.KOTLIN_REFLECT)
    implementation(Dependencies.PICOCLI)
    implementation(project(":corellium:domain"))
    implementation(project(":corellium:adapter"))
    implementation(project(":tool:config"))
    implementation(project(":tool:log:format"))
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
}
