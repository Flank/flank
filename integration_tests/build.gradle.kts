import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.DETEKT_PLUGIN)
}

group = "org.flank"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kotlin/ktor")
    maven(url = "https://dl.bintray.com/kotlin/kotlinx")
}

detekt {
    failFast = true // fail build on any finding
    input = files("src/main/kotlin", "src/test/kotlin")
    config = files("../config/detekt.yml")
    autoCorrect = true //auto format for detekt via klint
    buildUponDefaultConfig = true // preconfigure defaults
    parallel = true

    reports {
        html.enabled = true // observe findings in your browser with structure and code snippets
        xml.enabled = true // checkstyle like format mainly for integrations like Jenkins
        txt.enabled = true // similar to the console output, contains issue signature to manually edit baseline files
    }
}

dependencies {
    implementation(kotlin("stdlib"))
    testImplementation(Dependencies.JUNIT)
    testImplementation(Dependencies.JACKSON_XML)
    testImplementation(Dependencies.JACKSON_KOTLIN)
    testImplementation(Dependencies.TRUTH)
    detektPlugins(Dependencies.DETEKT_FORMATTING)
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

tasks.register<Test>("integrationTests") {
    group = "Verification"
    description = "Runs flank integration tests"
    filter {
        includeTestsMatching("*IT")
    }
    testLogging {
        events("skipped", "failed")
        exceptionFormat = TestExceptionFormat.FULL
    }
    maxParallelForks = Runtime.getRuntime().availableProcessors() / 2
}

val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}
// Locally you should have flank built already, on CI we need to build it
if(System.getenv("CI") != null) {
    tasks["integrationTests"].dependsOn(":test_runner:build")
}
