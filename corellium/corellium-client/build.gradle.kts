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
    implementation(kotlin("stdlib", org.jetbrains.kotlin.config.KotlinCompilerVersion.VERSION)) // or "stdlib-jdk8"
    implementation(Dependencies.KOTLIN_SERIALIZATION)
    implementation("io.ktor:ktor-client-serialization:1.5.1")
    implementation("io.ktor:ktor-client-websockets:1.5.1")
    implementation("io.ktor:ktor-client-logging:1.5.1")

    implementation("ch.qos.logback:logback-classic:1.2.3")

    api("io.ktor:ktor-client-core:1.5.1")
    api("io.ktor:ktor-client-cio:1.5.1")

    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.MOCKK)
    testImplementation(Dependencies.TRUTH)
}
