import org.gradle.kotlin.dsl.extra
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines

group = "kotlin_poc"
version = "1.0-SNAPSHOT"

buildscript {
    repositories {
        jcenter()
    }

    dependencies {
        classpath(kotlin("gradle-plugin", Deps.kotlinVersion))
    }
}

plugins {
    application
    kotlin("jvm") version Deps.kotlinVersion
}

kotlin {
    experimental.coroutines = Coroutines.ENABLE
}

apply {
    plugin("kotlin")
}

application {
    mainClassName = "main"
}

repositories {
    maven(url = "http://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
    jcenter()
}

dependencies {

    // https://bintray.com/kotlin/ktor/ktor
    val ktorVersion = "0.9.1-alpha-9"
    compile("io.ktor:ktor-server-core:$ktorVersion")
    compile("io.ktor:ktor-server-netty:$ktorVersion")
    compile("org.slf4j:slf4j-nop:1.7.21")

    compile("com.google.code.gson:gson:2.8.2")

    // https://github.com/Kotlin/kotlinx.coroutines/releases
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.21.2")

    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.cloud%22%20AND%20a%3A%22google-cloud-storage%22
    compile("com.google.cloud:google-cloud-storage:1.14.0")

    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-toolresults%22
    compile("com.google.apis:google-api-services-toolresults:v1beta3-rev339-1.23.0")

    // https://github.com/linkedin/dex-test-parser/releases
    compile("com.linkedin.dextestparser:parser:1.1.0")

    compile(project(":testing"))

    compile(kotlin("stdlib-jre8", Deps.kotlinVersion))
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
