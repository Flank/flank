import java.io.ByteArrayOutputStream
import org.gradle.kotlin.dsl.support.serviceOf

rootProject.name = "flank"

include(
    ":test_runner",
    ":firebase_apis:test_api",
    ":flank-scripts",
    ":integration_tests",
    "samples:gradle-export-api",
    "test_projects:android",
    ":common",
    ":corellium:sandbox",
    ":corellium:client",
    ":corellium:instrument:log",
    ":corellium:instrument:command",
    ":corellium:api",
    ":corellium:apk",
    ":corellium:shard",
    ":corellium:shard:calculate",
    ":corellium:shard:obfuscate",
    ":corellium:shard:dump",
    ":corellium:adapter",
    ":corellium:junit",
    ":corellium:domain",
    ":corellium:cli",
)

plugins {
    id("com.gradle.enterprise") version "3.5"
}

@Suppress("UnstableApiUsage")
val isCI: Boolean = serviceOf<ProviderFactory>()
    .environmentVariable("CI")
    .forUseAtConfigurationTime().map { it == "true" }
    .getOrElse(false)

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        publishAlwaysIf(isCI)
        background {
            val os = ByteArrayOutputStream()
            exec {
                commandLine("git", "rev-parse", "--verify", "HEAD")
                standardOutput = os
            }
            value("Git Commit ID", os.toString())
        }
    }
}
