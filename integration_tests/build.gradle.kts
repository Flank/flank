plugins {
    java
    kotlin(Plugins.Kotlin.PLUGIN_JVM)
    id(Plugins.DETEKT_PLUGIN)
}

group = "org.flank"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "http://dl.bintray.com/kotlin/ktor")
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
}
