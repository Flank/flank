package flank.corellium.api

/**
 * Structured representation of the parsed test apk file.
 * @property packageName Parsed apk package name.
 * @property testCases Full list of test methods from parsed apk.
 */
data class TestApk(
    val packageName: String,
    val testCases: List<String>
) {

    interface Parse : (LocalPath) -> TestApk
}

/**
 * Local path to the test apk file.
 */
private typealias LocalPath = String
