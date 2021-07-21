import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
}

repositories {
    mavenCentral()
}

tasks.withType<KotlinCompile> { kotlinOptions.jvmTarget = "1.8" }

dependencies {
    api(project(":tool:shard"))

    testImplementation(Dependencies.JUNIT)
}
