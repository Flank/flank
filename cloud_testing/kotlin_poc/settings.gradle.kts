rootProject.name = "kotlin_poc"

include(":testing")
project(":testing").projectDir = File(rootProject.projectDir, "../apis/testing")

include("xctest_parser")
project(":xctest_parser").projectDir = File(rootProject.projectDir, "../xctest_parser")
