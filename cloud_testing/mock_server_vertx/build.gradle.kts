import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.kotlin.dsl.extra
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.kotlin.gradle.dsl.Coroutines

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
    id("com.github.johnrengelman.shadow") version "2.0.1"
    kotlin("jvm") version Deps.kotlinVersion
}

apply {
    plugin("kotlin")
    plugin("com.github.johnrengelman.shadow")
}

val launcherClass = "io.vertx.core.Launcher"
application {
    mainClassName = launcherClass
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

version = "0.0.1-SNAPSHOT"

// https://github.com/vert-x3/vertx-gradle-starter/blob/master/build.gradle
val vertxVersion = "3.5.0"
val mainVerticleName = "ftl.MainVerticle"

repositories {
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jre8", Deps.kotlinVersion))
    compile("io.vertx:vertx-core:$vertxVersion")
    compile("io.vertx:vertx-lang-kotlin:$vertxVersion")
    compile("io.vertx:vertx-web:$vertxVersion")
    compile("io.vertx:vertx-web-api-contract:$vertxVersion")
    compile("org.slf4j:slf4j-nop:1.7.25")

    // https://bintray.com/bintray/jcenter/org.jetbrains.kotlin%3Akotlin-test-junit
    testCompile("org.jetbrains.kotlin:kotlin-test-junit:1.2.21")
    testCompile("io.vertx:vertx-unit:$vertxVersion")
    testCompile("io.vertx:vertx-web-client:$vertxVersion")
}

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    classifier = "fat"
    manifest {
        attributes.apply {
            put("Main-Verticle", mainVerticleName)
        }
    }
    mergeServiceFiles {
        include("META-INF/services/io.vertx.core.spi.VerticleFactory")
    }
}

// "gradle run" will start the server
tasks {
    "run"(JavaExec::class) {
        val watchForChange = "src/**/*"
        val doOnChange = "./gradlew classes"

        main = launcherClass

        args("run", mainVerticleName,
                "--redeploy=$watchForChange",
                "--launcher-class=${application.mainClassName}",
                "--on-redeploy=$doOnChange")
    }
}

