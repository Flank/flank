package integration

import FlankCommand
import TestParameters
import org.junit.Before
import org.junit.Test
import run
import toAndroidTestParameters
import utils.toStringMap


class AndroidFullIT {

    @Test
    fun `flank full option run`() {
//        val result = FlankCommand(
//            flankPath = "../test_runner/build/libs/flank.jar",
//            ymlPath = "./src/test/resources/flank_android_full.yml",
//            params = androidRunCommands
//        ).run("./", this::class.java.simpleName)
//
//
//        assertExitCode(result, 10)
//
//        Assert that result.output.removeUnicode() contains {
        Assert that testa contains {
            customConfig(
                "disable-sharding" with false,
                "max-test-shards" with 50,
                "additional-app-test-apks" with listOf(
                    "- app: null",
                    "  test: [0-9a-zA-Z\\/_.-]*/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk",
                    "- app: null",
                    "  test: [0-9a-zA-Z\\/_.-]*/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-error-debug-androidTest.apk",
                    "- app: null",
                    "  test: gs://flank-open-source.appspot.com/test/app-single-success-debug-androidTest.apk",
                ),
                "robo-script" with "[0-9a-zA-Z\\/_.-]*/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/MainActivity_robo_script.json",
                "use-orchestrator" with false,
                "output-style" with "single"
            )
            output {
                matricesWebLink(linksNumber = 4)
                androidShards(
                    tests = 11,
                    classes = 8,
                    shards = 19,
                    matrices = 4
                )
                fetchArtifacts()
                costReport(
                    virtual = true,
                    physical = true
                )
                resultsReport()
            }
            outcomeSummary {
                success(number = 3)
                failure(number = 1)
                flaky(number = 0)
            }
        }
    }
}


val testa = """
    version: local_snapshot
revision: c56cdec8b3586a64f3ff644a74372524613ee301

AndroidArgs
    gcloud:
      results-bucket: test-lab-v9cn46bb990nx-kz69ymd4nm9aq
      results-dir: 2020-11-10_11-26-27.200686_GnSv
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
      use-orchestrator: false
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
      robo-script: /Users/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/MainActivity_robo_script.json
      device: 
        - model: NexusLowRes
          version: 28
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0
      test-targets-for-shard: 

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
      disable-sharding: false
      project: flank-open-source
      local-result-dir: results
      full-junit-result: false
      # Android Flank Yml
      keep-file-path: false
      additional-app-test-apks:
        - app: null
          test: /Users/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk
        - app: null
          test: /Users/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-error-debug-androidTest.apk
        - app: null
          test: gs://flank-open-source.appspot.com/test/app-single-success-debug-androidTest.apk
      run-timeout: -1
      legacy-junit-result: false
      ignore-failed-tests: false
      output-style: single
      disable-results-upload: false
      default-class-test-time: 240.0

RunTests

 Smart Flank cache hit: 0% (0 / 9)
  Shard times: 120s, 120s, 120s, 120s, 120s, 240s, 240s, 240s, 240s


 Smart Flank cache hit: 0% (0 / 9)
  Shard times: 120s, 120s, 120s, 120s, 120s, 240s, 240s, 240s, 240s


 Smart Flank cache hit: 0% (0 / 1)
  Shard times: 120s

Saved 3 shards to android_shards.json
  Uploading android_shards.json .
  Uploading app-debug.apk .
  Uploading app-multiple-error-debug-androidTest.apk .  Uploading app-multiple-success-debug-androidTest.apk .  Uploading MainActivity_robo_script.json .


  11 tests + 8 parameterized classes / 19 shards

  4 matrix ids created in 0m 12s
  https://console.developers.google.com/storage/browser/test-lab-v9cn46bb990nx-kz69ymd4nm9aq/2020-11-10_11-26-27.200686_GnSv

Matrices webLink
  matrix-3rggyncbmbsu2 https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6674251027591133234/executions/bs.8a98b8a47771a4c2
  matrix-2if6zvcy09fym https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/5087540697861399228
  matrix-uv04kgwx4efqa https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6407905251640120579/executions/bs.752e21ad35ba9970
  matrix-r9ejzytvt6jaa https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/9178244087966892822

FetchArtifacts
  ...................
  Updating matrix file

CostReport
  Virtual devices
    ${'$'}0.35 for 21m

  Uploading CostReport.txt .
MatrixResultsReport
  2 / 4 (50.00%)
  2 matrices failed

┌─────────┬──────────────────────┬────────────────────────────┬────────────────────────────────┐
│ OUTCOME │      MATRIX ID       │      TEST AXIS VALUE       │          TEST DETAILS          │
├─────────┼──────────────────────┼────────────────────────────┼────────────────────────────────┤
│ success │ matrix-3rggyncbmbsu2 │ NexusLowRes-28-en-portrait │ ---                            │
│ success │ matrix-2if6zvcy09fym │ NexusLowRes-28-en-portrait │ 20 test cases passed           │
│ failure │ matrix-r9ejzytvt6jaa │ NexusLowRes-28-en-portrait │ 5 test cases failed, 15 passed │
│ failure │ matrix-uv04kgwx4efqa │ NexusLowRes-28-en-portrait │ 0 test cases failed            │
└─────────┴──────────────────────┴────────────────────────────┴────────────────────────────────┘
More details are available at:
https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/9178244087966892822
https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6407905251640120579

  Uploading MatrixResultsReport.txt .
  Uploading HtmlErrorReport.html .
  Uploading JUnitReport.xml .

Matrices webLink
  matrix-3rggyncbmbsu2 https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6674251027591133234/executions/bs.8a98b8a47771a4c2
  matrix-r9ejzytvt6jaa https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/9178244087966892822
  matrix-2if6zvcy09fym https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/5087540697861399228
  matrix-uv04kgwx4efqa https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6407905251640120579/executions/bs.752e21ad35ba9970
""".trimIndent()
