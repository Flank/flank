import org.gradle.api.tasks.testing.logging.TestExceptionFormat
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
    jcenter()
}

// Output full test results to console
// Avoids having to read the HTML report
tasks.withType<Test> {
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
}

dependencies {
    compile("com.google.code.gson:gson:2.8.2")

    // https://github.com/remkop/picocli
    compile("info.picocli:picocli:2.2.1")

    // https://github.com/Kotlin/kotlinx.coroutines/releases
    compile("org.jetbrains.kotlinx:kotlinx-coroutines-core:0.21.2")

    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.cloud%22%20AND%20a%3A%22google-cloud-storage%22
    compile("com.google.cloud:google-cloud-storage:1.15.0")

    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-toolresults%22
    compile("com.google.apis:google-api-services-toolresults:v1beta3-rev351-1.21.0")

    // https://github.com/linkedin/dex-test-parser/releases
    compile("com.linkedin.dextestparser:parser:1.1.0")

    // https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.google.apis%22%20AND%20a%3A%22google-api-services-testing%22
    compile("com.google.apis:google-api-services-testing:v1-rev23-1.23.0")

    compile(kotlin("stdlib-jre8", Deps.kotlinVersion))
    testCompile("junit:junit:4.12")
    // http://stefanbirkner.github.io/system-rules/index.html
    testCompile("com.github.stefanbirkner:system-rules:1.17.1")

    // yaml config
    compile("com.fasterxml.jackson.core:jackson-databind:2.9.4")
    compile("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.4")
    compile("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.4")
}


// Fix Exception in thread "main" java.lang.NoSuchMethodError: com.google.common.hash.Hashing.crc32c()Lcom/google/common/hash/HashFunction;
// https://stackoverflow.com/a/45286710
configurations.all {
    resolutionStrategy {
        force("com.google.guava:guava:23.6-jre")
        force("org.jetbrains.kotlin:kotlin-reflect:${Deps.kotlinVersion}")
        exclude(group = "com.google.guava", module = "guava-jdk5")
    }
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

task("fatJar", type = Jar::class) {
    baseName = "${project.name}-all"
    manifest {
        attributes.apply {
            put("Main-Class", "ftl.Main")
        }
    }
    from(configurations.runtime.map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks["jar"] as CopySpec)
}
