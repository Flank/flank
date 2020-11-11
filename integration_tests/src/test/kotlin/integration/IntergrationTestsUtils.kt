package integration

import com.google.common.truth.Truth.assertThat
import org.junit.Assert.assertEquals
import utils.ProcessResult
import java.io.File
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

private fun MutableMap.MutableEntry<String, Any>.makeKVRegex() = "$key:\\s*$value".toRegex()

infix fun String.with(value: Any) = this to value.toString()

infix fun String.with(values: List<String>) = this to buildString { values.forEach(::flankOutputLine) }

private fun StringBuilder.flankOutputLine(line: String) = append(line).append("\\s*")

fun FlankOutput.matricesWebLink(linksNumber: Int = 1) = lines.add(buildString {
    flankOutputLine("Matrices webLink")
    repeat(linksNumber) {
        flankOutputLine("matrix-[a-zA-Z0-9]+ https://console.firebase.google.com/project/flank-open-source/testlab/histories/[.a-zA-Z0-9_-]*/matrices/[.a-zA-Z0-9_-]*(/executions/[.a-zA-Z0-9_-]*)?")
    }
})

private fun shards(tests: Int, classes: Int, shards: Int, matrices: Int, os: String) = buildString {
    val testString = if (tests > 0) "tests" else "test"
    val classString = if (classes > 0) "classes" else "class"
    val shardString = if (shards > 0) "shards" else "shard"
    flankOutputLine("RunTests")
    flankOutputLine("[\\S\\s]*")
    flankOutputLine("Saved $shards shards to ${os}_shards.json")
    flankOutputLine("Uploading ${os}_shards.json .")
    if (tests >= 0 && matrices > 0) {
        flankOutputLine("Uploading app-debug.apk[\\s\\S]*")
        if (classes == 0)
            flankOutputLine("$tests $testString / $shards $shardString")
        else
            flankOutputLine("$tests $testString + $classes parameterized $classString / $shards $shardString")

        flankOutputLine("$matrices matrix ids created in [0-9]{1,2}m [0-9]{1,2}s")
        flankOutputLine("https://console.developers.google.com/storage/browser/test-lab-[a-zA-Z0-9_-]*/[.a-zA-Z0-9_-]*")
    }
}

fun FlankOutput.androidShards(tests: Int, classes: Int, shards: Int, matrices: Int) =
    lines.add(shards(tests, classes, shards, matrices, "android"))

fun FlankOutput.iosShards(tests: Int, classes: Int, shards: Int, matrices: Int) =
    lines.add(shards(tests, classes, shards, matrices, "ios"))

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
    { outcome: String -> "│\\s$outcome\\s│\\s${"matrix"}-[a-zA-Z0-9]*\\s│\\s[a-zA-Z0-9-]*\\s│\\s[a-zA-Z0-9\\s,-]*\\s*│".toRegex() }

fun String.removeUnicode() = replace("\u001B\\[\\d{1,2}m".toRegex(), "").trimIndent()

fun findInCompare(name: String) = File("./src/test/resources/compare/$name-compare").readText().trimIndent()
