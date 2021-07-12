import java.io.ByteArrayOutputStream
import org.gradle.kotlin.dsl.support.serviceOf

rootProject.name = "flank"

includeBuild("maven_version_check")

include(
    ":test_runner",
    ":firebase_apis:test_api",
    ":flank-scripts",
    ":integration_tests",
    "samples:gradle-export-api",
    "test_projects:android",
    ":common",
    ":flank_wrapper",

    ":corellium:cli",
    ":corellium:domain",
    ":corellium:api",
    ":corellium:adapter",
    ":corellium:client",
    ":corellium:sandbox",
    ":tool:apk",
    ":tool:config",
    ":tool:filter",
    ":tool:shard",
    ":tool:shard:calculate",
    ":tool:shard:obfuscate",
    ":tool:shard:dump",
    ":tool:instrument:command",
    ":tool:instrument:log",
    ":tool:junit",
    ":tool:log",
    ":tool:log:format",
    ":tool:execution:parallel",
    ":tool:execution:synchronized",
    ":tool:analytics",
    ":tool:analytics:mixpanel",
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
