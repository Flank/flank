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
    mainClassName = "main"
}

repositories {
    jcenter()
}

dependencies {
    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.cloud%22%20AND%20a%3A%22google-cloud-storage%22
    compile("com.google.cloud:google-cloud-storage:1.6.0")

    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-toolresults%22
    compile("com.google.apis:google-api-services-toolresults:v1beta3-rev260-1.22.0")

    // https://github.com/linkedin/dex-test-parser/releases
    compile("com.linkedin.dextestparser:parser:1.1.0")

    compile(project(":testing"))

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

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

