import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
}

repositories {
    jcenter()
    mavenCentral()
    maven(url = "https://kotlin.bintray.com/kotlinx")
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    api(project(":corellium:shard"))

    testImplementation(Dependencies.JUNIT)
}
