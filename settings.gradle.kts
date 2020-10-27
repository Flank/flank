import java.io.ByteArrayOutputStream

rootProject.name = "flank"

include(
    ":test_runner",
    ":firebase_apis:test_api",
    ":flank-scripts",
    ":integration_tests",
    "samples:gradle-export-api",
    "test_projects:android"
)

plugins {
    id("com.gradle.enterprise") version "3.4.1"
}

gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
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
