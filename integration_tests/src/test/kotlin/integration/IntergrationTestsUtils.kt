package integration

import org.junit.Assert.assertEquals
import utils.ProcessResult
import java.io.File

const val FLANK_JAR_PATH = "../test_runner/build/libs/flank.jar"
const val CONFIGS_PATH = "./src/test/resources/cases"

val androidRunCommands = listOf("firebase", "test", "android", "run")
val iosRunCommands = listOf("firebase", "test", "ios", "run")

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

fun assertContainsOutcomeSummary(input: String, block: OutcomeSummary.() -> Unit) =
    OutcomeSummary().apply(block).matcher.entries.forEach { (outcome, times) ->
        val actual = outcome.regex.findAll(input).toList().size
        if (actual != times) throw AssertionError("""
             |Incorrect number of ${outcome.name}
             |  expected: $times
             |  but was:  $actual
             |Output:
             |${"┌[\\s\\S]*┘".toRegex().find(input)?.value?.trimIndent()}
         """.trimMargin()
        )
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
        if (isWindows) "\\?\\s$outcome\\s\\?\\smatrix-[a-zA-Z0-9]*\\s\\?\\s*[a-zA-Z0-9-]*\\s*\\?\\s[a-zA-Z0-9\\s,-]*\\s*\\?".toRegex()
        else "│\\s$outcome\\s│\\s${"matrix"}-[a-zA-Z0-9]*\\s│\\s*[a-zA-Z0-9-]*\\s*│\\s[a-zA-Z0-9\\s,-]*\\s*│".toRegex()
    }

fun String.removeUnicode() = replace("\u001B\\[\\d{1,2}m".toRegex(), "").trimIndent()

fun findInCompare(name: String) = File("./src/test/resources/compare/$name-compare").readText().trimIndent()

private val osName = System.getProperty("os.name")?.toLowerCase() ?: ""

val isWindows: Boolean by lazy {
    osName.indexOf("win") >= 0
}
