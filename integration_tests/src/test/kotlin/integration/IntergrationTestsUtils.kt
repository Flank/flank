package integration

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertEquals
import utils.ProcessResult
import java.lang.StringBuilder
import kotlin.text.Regex.Companion.escape

fun FlankAssertion.defaultConfig() = customConfig()

fun FlankAssertion.customConfig(vararg customChecks: Pair<String, String> = emptyArray()) {
    val checks = defaultAndroidConfig.toMutableMap()
    customChecks.forEach { checks[it.first] = it.second }
    checks.entries
        .forEach {
            if (it.makeKVRegex().find(input) == null) {
                val message = """
                    |Incorrect value for [${it.key}].
                    |Expected:
                    |  ${it.key}: ${
                    it.value.toString()
                        .replace("-", "${System.lineSeparator()}  -")
                        .replace("""\s*""", "")
                }
                    |Was:
                    |  ${
                    "${it.key}:[\\s\\S]{10,60}"
                        .toRegex()
                        .find(input)
                        ?.value
                        ?.split(System.lineSeparator())
                        ?.joinToString { value -> "${System.lineSeparator()}  " + value.trim() }
                }(...)
                    |Output:
                    |$input""".trimMargin()
                throw AssertionError(message)
            }
        }
}

private fun MutableMap.MutableEntry<String, Any>.makeKVRegex() = "${key}:\\s*${value}".toRegex()

infix fun String.with(value: Any) = this to value.toString()

infix fun String.with(values: List<String>) = this to buildString { values.forEach(::flankOutputLine) }

private fun StringBuilder.flankOutputLine(line: String) = append(line).append("\\s*")

fun FlankOutput.matricesWebLink(linksNumber: Int = 1) = lines.add(buildString {
    flankOutputLine("Matrices webLink")
    repeat(linksNumber) {
        flankOutputLine("matrix-[a-zA-Z0-9]+ https://console.firebase.google.com/project/flank-open-source/testlab/histories/[.a-zA-Z0-9_-]*/matrices/[.a-zA-Z0-9_-]*(/executions/[.a-zA-Z0-9_-]*)?")
    }
})

private fun shards(tests: Int = 1, classes: Int = 0, shards: Int = 1, matrices: Int = 1, os: String) = buildString {
    val testString = if (tests > 0) "tests" else "test"
    val classString = if (classes > 0) "classes" else "class"
    val shardString = if (shards > 0) "shards" else "shard"
    flankOutputLine("RunTests")
    flankOutputLine("[\\S\\s]*")
    flankOutputLine("Saved $shards shards to ${os}_shards.json")
    flankOutputLine("Uploading ${os}_shards.json .")
    if (tests >= 0) {
        flankOutputLine("Uploading app-debug.apk [\\s\\S]*")
        if (classes == 0)
            flankOutputLine("$tests $testString / $shards $shardString")
        else
            flankOutputLine("$tests $testString + $classes parameterized $classString / $shards $shardString")
    }
    flankOutputLine("$matrices matrix ids created in \\dm \\ds")
    flankOutputLine("https://console.developers.google.com/storage/browser/test-lab-[a-zA-Z0-9_-]*/[.a-zA-Z0-9_-]*")
}

fun FlankOutput.androidShards(tests: Int = 1, classes: Int = 0, shards: Int = 1, matrices: Int = 1) = lines.add(shards(tests, classes, shards, matrices, "android"))

fun FlankOutput.iosShards(tests: Int = 1, classes: Int = 0, shards: Int = 1, matrices: Int = 1) = lines.add(shards(tests, classes, shards, matrices, "ios"))

fun FlankOutput.resultsReport(left: Int = 1, right: Int = 1) = lines.add(buildString {
    flankOutputLine("MatrixResultsReport")
    flankOutputLine("$left / $right ${escape("(100.00%)")}")
})

fun FlankOutput.noneTestRan() = lines.apply {
    add(buildString {
        flankOutputLine("RunTests")
        flankOutputLine("No tests for [a-zA-Z0-9._-]*.apk")
    })
    add("There are no tests to run")
}

fun FlankOutput.fetchArtifacts() = lines.add(buildString {
    flankOutputLine("FetchArtifacts")
    flankOutputLine("\\.*")
    flankOutputLine("Updating matrix file")
})

fun FlankOutput.costReport(virtual: Boolean, physical: Boolean) = lines.add(buildString {
    flankOutputLine("CostReport")
    if (virtual) {
        flankOutputLine("Virtual devices")
        flankOutputLine("\\$(\\d*.\\d*) for \\d*m")
    }
    if (physical) {
        flankOutputLine("Physical devices")
        flankOutputLine("\\$(\\d*.\\d*) for \\d*m")
    }
})

data class FlankOutput(val lines: MutableList<String> = mutableListOf())

infix fun FlankAssertion.output(block: FlankOutput.() -> Unit) = assertThat(input).run {
    FlankOutput().apply(block).lines.forEach { containsMatch(it) }
}

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

object Assert {
    infix fun that(input: String) = FlankAssertion(input)
}

@Suppress("EXPERIMENTAL_FEATURE_WARNING")
inline class FlankAssertion(val input: String)

inline infix fun FlankAssertion.contains(block: FlankAssertion.() -> Unit) = this.run(block)

data class OutcomeSummary(val matcher: MutableMap<TestOutcome, Int> = mutableMapOf<TestOutcome, Int>().withDefault { 0 }) {
    fun success(number: Int) = matcher.put(TestOutcome.SUCCESS, number)

    fun failure(number: Int) = matcher.put(TestOutcome.FAILURE, number)

    fun flaky(number: Int) = matcher.put(TestOutcome.FLAKY, number)
}

fun FlankAssertion.outcomeSummary(block: OutcomeSummary.() -> Unit) =
    OutcomeSummary().apply(block).matcher.entries.forEach { (outcome, times) ->
        val actual = outcome.regex.findAll(input).toList().size
        if (actual != times) throw AssertionError(
            """Incorrect number of ${outcome.name}
             |  expected: $times
             |  but was:  $actual
             |Output:
             |${"┌[\\s\\S]*┘".toRegex().find(input)?.value?.trimIndent()}
         """.trimMargin()
        )
    }

fun FlankAssertion.noOutcomeSummary() {
    if ("┌[\\s\\S]*┘".toRegex().matches(input)) throw AssertionError("There should be no outcome table.")
}

enum class TestOutcome(val regex: Regex) {
    SUCCESS(fromCommon("success", "---")),
    FLAKY(fromCommon("flaky", "[0-9a-z\\s,]*")),
    FAILURE(fromCommon("failure", "[0-9a-z\\s,]*")),
}

private val fromCommon =
    { result: String, details: String -> "│\\s$result\\s│\\s${"matrix"}-[a-zA-Z0-9]*\\s│\\s[a-zA-Z0-9-]*\\s│\\s$details\\s*│".toRegex() }

fun String.removeUnicode() = replace("\u001B\\[\\d{1,2}m".toRegex(), "")
