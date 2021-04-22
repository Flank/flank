import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
}

group = "org.flank"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
}

dependencies {
    implementation(project(":common"))
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.JACKSON_XML)
    testImplementation(Dependencies.JACKSON_KOTLIN)
    testImplementation(Dependencies.TRUTH)
}

tasks.test {
    onlyIf {
        System.getProperty("flank-path", "").isNotBlank()
    }
    systemProperty("flank-path", System.getProperty("flank-path"))
    systemProperty("yml-path", System.getProperty("yml-path"))
    systemProperty("run-params", System.getProperty("run-params"))
    systemProperty("working-directory", System.getProperty("working-directory"))
    systemProperty("output-pattern", System.getProperty("output-pattern"))
    systemProperty("expected-output-code", System.getProperty("expected-output-code"))
    filter {
        excludeTestsMatching("*IT")
    }
}

tasks.register<Test>("integrationTestsAndroid") {
    group = "Verification"
    description = "Runs flank integration tests -- Android"
    filter {
        includeTestsMatching("*IT")
    }
    useJUnit {
        includeCategories = setOf("integration.config.AndroidTest")
        excludeCategories = setOf("integration.config.IosTest")
    }
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    maxParallelForks = Runtime.getRuntime().availableProcessors()
}

tasks.register<Test>("integrationTestsIos") {
    group = "Verification"
    description = "Runs flank integration tests -- iOS"
    filter {
        includeTestsMatching("*IT")
    }
    useJUnit {
        includeCategories = setOf("integration.config.IosTest")
        excludeCategories = setOf("integration.config.AndroidTest")
    }
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    failFast = true
}

tasks.register("integrationTests") {
    dependsOn("integrationTestsAndroid", "integrationTestsIos")
    tasks["integrationTestsIos"].mustRunAfter("integrationTestsAndroid")
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}
// Locally you should have flank built already, on CI we need to build it
if (System.getenv("CI") != null) {
    tasks["integrationTests"].dependsOn(":test_runner:build")
}
