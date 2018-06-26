rootProject.name = "kotlin_poc"

include(":testing")
project(":testing").projectDir = File(rootProject.projectDir, "../apis/testing")
