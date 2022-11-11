import java.io.ByteArrayOutputStream
import org.gradle.kotlin.dsl.support.serviceOf

rootProject.name = "flank"

includeBuild("maven_version_check")
includeBuild("check_version_updated")

include(
    ":test_runner",
    ":firebase_apis:test_api",
    ":flank-scripts",
    ":integration_tests",
    "samples:gradle-export-api",
    "test_projects:android",
    ":common",
    ":flank_wrapper",
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
    ":tool:json",
    ":tool:log",
    ":tool:log:format",
    ":tool:execution:parallel",
    ":tool:execution:parallel:plantuml",
    ":tool:execution:synchronized",
    ":tool:execution:linear",
    ":tool:resource",
)
