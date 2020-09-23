rootProject.name = "flank"

include(
    ":test_runner",
    ":firebase_apis:test_api",
    ":flank-scripts",
    ":integration_tests",
    "samples:gradle-export-api",
    "test_projects:android"
)
