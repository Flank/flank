package integration

import com.google.common.truth.Truth.assertThat
import org.junit.Assert
import org.junit.Assert.assertEquals
import utils.ProcessResult
import java.lang.StringBuilder
import kotlin.text.Regex.Companion.escape

val androidRunCommands = listOf("firebase", "test", "android", "run")

private val commonGcloud = """
    gcloud:
      results-bucket: test-lab-[.a-zA-Z0-9_-]*
      results-dir: [.a-zA-Z0-9_-]*
      record-video: false
      timeout: 15m
      async: false
      client-details: 
      network-profile: null
      results-history-name: null
""".trimIndent()

val gcloudDefault = """
    $commonGcloud
      # Android gcloud
      app: [0-9a-zA-Z\/_]*-debug.apk
      test: null
      additional-apks: 
      auto-google-login: false
      use-orchestrator: true
      directories-to-pull: 
      grant-permissions: all
      type: null
      other-files: 
      scenario-numbers: 
      scenario-labels: 
      obb-files: 
      obb-names: 
      performance-metrics: false
      num-uniform-shards: null
      test-runner-class: null
      test-targets:
      robo-directives:
      robo-script: null
      device:
        - model: NexusLowRes
          version: 28
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0
""".trimIndent()

private val flankCommon = """
    flank:
      max-test-shards: 1
      shard-time: -1
      num-test-runs: 1
      smart-flank-gcs-path: 
      smart-flank-disable-upload: false
      default-test-time: 120.0
      use-average-test-time-for-new-tests: false
      files-to-download:
      test-targets-always-run:
      disable-sharding: false
      project: flank-open-source
      local-result-dir: results
      full-junit-result: false
""".trimIndent()

val flankDefault = """
    $flankCommon
      # Android Flank Yml
      keep-file-path: false
      additional-app-test-apks:
      run-timeout: -1
      legacy-junit-result: false
      ignore-failed-tests: false
      output-style: single
      disable-results-upload: false
      default-class-test-time: 240.0
""".trimIndent()

private fun StringBuilder.flankOutputLine(line: String) = append(line).append("\\s*")

fun FlankOutput.matricesWebLink(linksNumber: Int = 1) = lines.add(buildString {
    flankOutputLine("Matrices webLink")
    repeat(linksNumber) {
        flankOutputLine("matrix-[a-zA-Z0-9]+ https://console.firebase.google.com/project/flank-open-source/testlab/histories/[.a-zA-Z0-9_-]*/matrices/[.a-zA-Z0-9_-]*/executions/[.a-zA-Z0-9_-]*")
    }
})

fun FlankOutput.runTests(tests: Int = 1, shards: Int = 1, matrices: Int = 1) = lines.add(buildString {
    flankOutputLine("RunTests")
    flankOutputLine("Uploading app-debug.apk \\.*")
    flankOutputLine("$tests test / $shards shard")
    flankOutputLine("$matrices matrix ids created in \\dm \\ds")
    flankOutputLine("https://console.developers.google.com/storage/browser/test-lab-[a-zA-Z0-9_-]*/[.a-zA-Z0-9_-]*")
})

fun FlankOutput.fetchArtifacts() = lines.add(buildString {
    flankOutputLine("FetchArtifacts")
    flankOutputLine("Updating matrix file")
})

fun FlankOutput.costReport() = lines.add(buildString {
    flankOutputLine("CostReport")
    flankOutputLine("Virtual devices")
    flankOutputLine("\\$(\\d*.\\d*) for \\d*m")
})

fun FlankOutput.resultsReport(left: Int = 1, right: Int = 1) = lines.add(buildString {
    flankOutputLine("MatrixResultsReport")
    flankOutputLine("$left / $right ${escape("(100.00%)")}")
})

private const val outputSummary_2 = "┌[\\s\\S]*┘"

data class FlankOutput(val lines: MutableList<String> = mutableListOf())

infix fun String.containsFlankOutput(block: FlankOutput.() -> Unit) = assertThat(this).run {
    FlankOutput().apply(block).lines.forEach { containsMatch(it) }
}

fun assertExitCode(result: ProcessResult, expectedExitCode: Int) = assertEquals("""
    Exit code:
      expected $expectedExitCode
      actual   ${result.exitCode}
    Output:
      ${result.output}
""".trimIndent(),
    0,
    result.exitCode
)

object Assert {
    infix fun that(input: String) = input
}

data class OutcomeSummary(val lines: MutableList<String> = mutableListOf())

fun outcomeSummary() = buildString {
    flankOutputLine("┌[\\s\\S")
}


infix fun String.containsOutcomeSummary(block: OutcomeSummary.() -> Unit) = assertThat(this).run {
    OutcomeSummary().apply(block).lines.forEach { containsMatch((it)) }
}

enum class TestOutcome {
    SUCCESS, FLAKY, FAILURE
}
