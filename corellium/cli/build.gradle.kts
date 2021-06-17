import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    kotlin(Plugins.Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN
}

repositories {
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    implementation(Dependencies.KOTLIN_COROUTINES_CORE)
    implementation(Dependencies.PICOCLI)
    implementation(project(":corellium:api"))
    implementation(project(":corellium:domain"))
    implementation(project(":corellium:adapter"))
    implementation(project(":tool:apk"))
    implementation(project(":tool:junit"))
    implementation(project(":tool:log:format"))
    implementation(Dependencies.JACKSON_KOTLIN)
    implementation(Dependencies.JACKSON_YAML)
    implementation(Dependencies.JACKSON_XML)
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
}
