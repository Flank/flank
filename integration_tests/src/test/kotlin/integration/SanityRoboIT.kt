package integration

import com.google.common.truth.Truth.assertThat
import FlankCommand
import TestParameters
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import run
import toAndroidTestParameters
import utils.toStringMap
import kotlin.text.Regex.Companion.escape


const val outputSummary = "┌[\\s\\S]*┘"
const val emptyLines = "\\s*"

class SanityRoboIT {
    private lateinit var testParams: TestParameters

    @Before
    fun setUp() {
        testParams = System.getProperties().toStringMap().toAndroidTestParameters()
    }

    @Test
    fun `should run sanity robo`() {
        val result = FlankCommand(
            flankPath = "../test_runner/build/libs/flank.jar",
            ymlPath = "./src/test/resources/sanity_robo.yml",
            params = androidRunCommands
        ).run("./")


        assertExitCode(result, 0)

        Assert that result.output containsFlankOutput {
            matricesWebLink(1)
            runTests(0, 0, 1)
            fetchArtifacts()
            costReport()
            resultsReport()
        }
    }

    @Test
    fun testa() {
        listOf<Int>()
        println()
    }
}

val bbreg = """
    AndroidArgs
        gcloud:
          results-bucket: test-lab-[.a-zA-Z0-9_-]*
          results-dir: [.a-zA-Z0-9_-]*
          record-video: false
          timeout: 15m
          async: false
          client-details: 
          network-profile: null
          results-history-name: null
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

        flank:
          max-test-shards: 50
          shard-time: -1
          num-test-runs: 1
          smart-flank-gcs-path: 
          smart-flank-disable-upload: false
          default-test-time: 120.0
          use-average-test-time-for-new-tests: false
          files-to-download:
          test-targets-always-run:
            - class actually.any.test.Clazz
          disable-sharding: false
          project: flank-open-source
          local-result-dir: results
          full-junit-result: false
          # Android Flank Yml
          keep-file-path: false
          additional-app-test-apks:
          run-timeout: -1
          legacy-junit-result: false
          ignore-failed-tests: false
          output-style: single
          disable-results-upload: true
          default-class-test-time: 240.0
    $emptyLines
    RunTests
      Uploading app-debug.apk \.*
      0 test / 0 shard
    $emptyLines
      1 matrix ids created in \dm \ds
      https://console.developers.google.com/storage/browser/test-lab-[a-zA-Z0-9_-]*/[.a-zA-Z0-9_-]*
    $emptyLines
    Matrices webLink
      matrix-[a-zA-Z0-9]+ https://console.firebase.google.com/project/flank-open-source/testlab/histories/[.a-zA-Z0-9_-]*/matrices/[.a-zA-Z0-9_-]*/executions/[.a-zA-Z0-9_-]*
    [\s]*0[\s\S]*FINISHED[\s]*
    $emptyLines
    FetchArtifacts
    $emptyLines
      Updating matrix file
    $emptyLines
    CostReport
      Virtual devices
        ${escape("$")}(\d*.\d*) for \d*m
    $emptyLines
    MatrixResultsReport
      1 / 1 ${escape("(100.00%)")}
    $emptyLines
    $outputSummary
    $emptyLines
    Matrices webLink
      matrix-[a-zA-Z0-9]+ https://console.firebase.google.com/project/flank-open-source/testlab/histories/[.a-zA-Z0-9_-]*/matrices/[.a-zA-Z0-9_-]*/executions/[.a-zA-Z0-9_-]*
""".trimIndent()

val test = """
    version: local_snapshot
    revision: 55476259c1d391bf779affaa0ed9dc30692279de

    AndroidArgs
        gcloud:
          results-bucket: test-lab-v9cn46bb990nx-kz69ymd4nm9aq
          results-dir: 2020-11-05_14-57-23.204686_dGGA
          record-video: false
          timeout: 15m
          async: false
          client-details: 
          network-profile: null
          results-history-name: null
          # Android gcloud
          app: /Users/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk
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

        flank:
          max-test-shards: 50
          shard-time: -1
          num-test-runs: 1
          smart-flank-gcs-path: 
          smart-flank-disable-upload: false
          default-test-time: 120.0
          use-average-test-time-for-new-tests: false
          files-to-download:
          test-targets-always-run:
            - class actually.any.test.Clazz
          disable-sharding: false
          project: flank-open-source
          local-result-dir: results
          full-junit-result: false
          # Android Flank Yml
          keep-file-path: false
          additional-app-test-apks:
          run-timeout: -1
          legacy-junit-result: false
          ignore-failed-tests: false
          output-style: multi
          disable-results-upload: true
          default-class-test-time: 240.0

    RunTests
      Uploading app-debug.apk .
      0 test / 0 shard

      1 matrix ids created in 0m 4s
      https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-11-05_14-57-23.204686_dGGA

    Matrices webLink
      matrix-2leuyoubnp892 https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/8504866218843143096/executions/bs.b2bb28a35d2f2766

      0m  0s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 PENDING
      0m  0s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 PENDING
      0m  0s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 PENDING
      0m  0s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 PENDING
      0m  0s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 PENDING
      0m  0s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 PENDING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      0m 38s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 RUNNING
      1m 40s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Preparing device.
      1m 40s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Preparing device.
      1m 40s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Preparing device.
      1m 40s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Preparing device.
      1m 40s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Preparing device.
      1m 40s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Preparing device.
      2m 16s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Installing apps.
      2m 16s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Installing apps.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      2m 28s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting remote robo test.
      3m 34s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Retrieving Post-test Package Stats information from the device.
      3m 34s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Retrieving Post-test Package Stats information from the device.
      3m 34s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Retrieving Post-test Package Stats information from the device.
      3m 52s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Stopped logcat recording.
      3m 58s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 Starting results processing. Attempt: 1
      4m  4s matrix-2leuyoubnp892 NexusLowRes-28 shard-0 FINISHED
      4m  4s matrix-2leuyoubnp892 FINISHED

    FetchArtifacts
      
      Updating matrix file

    CostReport
      Virtual devices
        ${'$'}0.03 for 2m

    MatrixResultsReport
      1 / 1 (100.00%)
    ┌─────────┬──────────────────────┬────────────────────────────┬──────────────┐
    │ OUTCOME │      MATRIX ID       │      TEST AXIS VALUE       │ TEST DETAILS │
    ├─────────┼──────────────────────┼────────────────────────────┼──────────────┤
    │ success │ matrix-2leuyoubnp892 │ NexusLowRes-28-en-portrait │ ---          │
    └─────────┴──────────────────────┴────────────────────────────┴──────────────┘

    Matrices webLink
      matrix-2leuyoubnp892 https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/8504866218843143096/executions/bs.b2bb28a35d2f2766
""".trimIndent()
