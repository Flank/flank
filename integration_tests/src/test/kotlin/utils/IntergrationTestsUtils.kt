package utils

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import org.junit.Assert.assertEquals
import java.io.File
import java.math.BigDecimal

const val FLANK_JAR_PATH = "../test_runner/build/libs/flank.jar"
const val CONFIGS_PATH = "./src/test/resources/cases"

val androidRunCommands = listOf("firebase", "test", "android", "run")
val iosRunCommands = listOf("firebase", "test", "ios", "run")

val multipleSuccessfulTests = listOf(
    "test", "testFoo",
    "test0", "test1", "test2",
    "clickRightButton[0]", "clickRightButton[1]", "clickRightButton[2]",
    "shouldHopefullyPass[0]", "shouldHopefullyPass[1]", "shouldHopefullyPass[2]",
    "clickRightButtonFromMethod(toast, toast) [0]", "clickRightButtonFromMethod(alert, alert) [1]",
    "clickRightButtonFromMethod(exception, exception) [2]", "clickRightButtonFromAnnotation(toast, toast) [0]",
    "clickRightButtonFromAnnotation(alert, alert) [1]", "clickRightButtonFromAnnotation(exception, exception) [2]",
    "clickRightButton[0: toast toast]", "clickRightButton[1: alert alert]", "clickRightButton[2: exception exception]"
)
val multipleFailedTests = listOf(
    "testFoo", "test0", "test1", "test2", "testBar"
)

fun assertExitCode(result: ProcessResult, expectedExitCode: Int) = assertEquals(
    """
    Exit code:
      expected $expectedExitCode
      actual   ${result.exitCode}
    Output:
      ${result.output}
    """.trimIndent(),
    expectedExitCode,
    result.exitCode
)

@Suppress("SetterBackingFieldAssignment")
data class OutcomeSummary(val matcher: MutableMap<TestOutcome, Int> = mutableMapOf<TestOutcome, Int>().withDefault { 0 }) {
    var success: Int = 0
        set(value) {
            matcher[TestOutcome.SUCCESS] = value
        }

    var failure: Int = 0
        set(value) {
            matcher[TestOutcome.FAILURE] = value
        }

    var flaky: Int = 0
        set(value) {
            matcher[TestOutcome.FLAKY] = value
        }
}

fun assertContainsUploads(input: String, vararg uploads: String) = uploads.forEach {
    assertThat(input).containsMatch("Uploading( file)? \\[$it]".toPattern())
}

fun SuiteOverview.assertTestCountMatches(
    total: Int = 0,
    errors: Int = 0,
    failures: Int = 0,
    flakes: Int = 0,
    skipped: Int = 0,
) {
    assertThat(this.total).isEqualTo(total)
    assertThat(this.errors).isEqualTo(errors)
    assertThat(this.failures).isEqualTo(failures)
    assertThat(this.flakes).isEqualTo(flakes)
    assertThat(this.skipped).isEqualTo(skipped)
}

fun OutputReport.assertCostMatches() {
    assertThat(cost?.physical).isEqualToIgnoringScale(BigDecimal.ZERO)
    assertThat(cost?.virtual).isGreaterThan(BigDecimal.ZERO)
    assertThat(cost?.virtual).isEqualToIgnoringScale(cost?.total)
}

fun assertNoOutcomeSummary(input: String) {
    if ("┌[\\s\\S]*┘".toRegex().matches(input)) throw AssertionError("There should be no outcome table.")
}

enum class TestOutcome(val regex: Regex) {
    SUCCESS(fromCommon("success")),
    FLAKY(fromCommon("flaky")),
    FAILURE(fromCommon("failure")),
}

private val fromCommon =
    { outcome: String ->
        if (isWindows) "\\?\\s$outcome\\s\\?\\smatrix-[a-zA-Z0-9]*\\s\\?\\s*[a-zA-Z0-9._-]*\\s*\\?\\s*[a-zA-Z0-9-]*\\s*\\?\\s[a-zA-Z0-9\\s,-]*\\s*\\?".toRegex()
        else "│\\s$outcome\\s│\\s${"matrix"}-[a-zA-Z0-9]*\\s│\\s*[a-zA-Z0-9._-]*\\s*│\\s*[a-zA-Z0-9-]*\\s*│\\s[a-zA-Z0-9\\s,-]*\\s*│".toRegex()
    }

fun String.removeUnicode() = replace("\u001B\\[\\d{1,2}m".toRegex(), "").trimIndent()

fun findInCompare(name: String) = File("./src/test/resources/compare/$name-compare").readText().trimIndent()
