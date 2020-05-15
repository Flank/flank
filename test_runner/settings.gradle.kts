rootProject.name = "flank"

include(":test_api")
project(":test_api").projectDir = File(rootProject.projectDir, "../firebase_apis/test_api")
