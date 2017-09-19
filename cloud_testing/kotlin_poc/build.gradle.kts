import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "kotlin_poc"
version = "1.0-SNAPSHOT"

val kotlinVersion: String by extra
buildscript {
    val kotlinVersion: String by extra
    extra["kotlinVersion"] = "1.1.4-3"

    repositories {
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

plugins {
    application
}

apply {
    plugin("kotlin")
}

application {
    mainClassName = "hello"
}

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jre8", kotlinVersion))
    testCompile("junit:junit:4.12")
}

val javaVersion = "1.8"
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = javaVersion
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = javaVersion
}
