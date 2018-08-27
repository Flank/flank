import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Versions.KOTLIN
}

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath(Plugins.KOTLIN)
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
