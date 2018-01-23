package ftl.util

object ArtifactRegex {

    val testResultRgx = Regex(".*test_result_\\d+\\.xml$")
    val screenshotRgx = Regex(".*\\.png$")
}
