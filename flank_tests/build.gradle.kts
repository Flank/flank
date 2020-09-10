import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    application
    kotlin(Kotlin.PLUGIN_JVM) version Versions.KOTLIN_VERSION
    kotlin(Kotlin.PLUGIN_SERIALIZATION) version Versions.KOTLIN_VERSION
    id(PLUGIN_SHADOW_JAR) version Versions.SHADOW_JAR
    id(DETEKT_PLUGIN) version Versions.DETEKT
}
group = "flank.tests"
version = "1.0"

repositories {
    mavenCentral()
}

detekt {
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("../config/detekt.yml")
    reports {
        xml {
            enabled = false
        }
        html {
            enabled = true
        }
    }
}

tasks["check"].dependsOn(tasks["detekt"])

dependencies {
    implementation(kotlin("stdlib"))
    detektPlugins(DETEKT_FORMATTING)
    testImplementation(JUNIT)
}
tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}
application {
    mainClassName = "MainKt"
}

tasks.test {
    systemProperty("flank-path", System.getProperty("flank-path"))
    systemProperty("yml-path", System.getProperty("yml-path"))
    systemProperty("run-params", System.getProperty("run-params"))
    systemProperty("working-directory", System.getProperty("working-directory"))
}
