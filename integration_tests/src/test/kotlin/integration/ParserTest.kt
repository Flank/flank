package integration

import org.junit.Test
import utils.asOutputReport

class ParserTest {

    @Test
    fun `Should parse report`() {
        s.asOutputReport().also {
            println(it)
        }
    }
}

val multi = """
    OutputReport(args={commonArgs={data=---
    gcloud:
      app: "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk"
      use-orchestrator: false
      num-flaky-test-attempts: 2
      device:
      - model: "NexusLowRes"
        version: 28
      - model: "Pixel2"
        version: 28
      - model: "Nexus6P"
        version: 27
    flank:
      disable-sharding: false
      max-test-shards: 5
      output-style: "single"
      additional-app-test-apks:
      - test: "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk"
      - test: "../test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-error-debug-androidTest.apk"
      - test: "gs://flank-open-source.appspot.com/integration/app-single-success-debug-androidTest.apk"
      disable-usage-statistics: true
      output-report: "json"
    , devices=[{model=NexusLowRes, version=28, locale=en, orientation=portrait, isVirtual=true}, {model=Pixel2, version=28, locale=en, orientation=portrait, isVirtual=true}, {model=Nexus6P, version=27, locale=en, orientation=portrait, isVirtual=true}], resultsBucket=test-lab-v9cn46bb990nx-kz69ymd4nm9aq, resultsDir=2021-03-19_16-34-33.617879_jfYb, recordVideo=false, testTimeout=15m, async=false, flakyTestAttempts=2, otherFiles={}, directoriesToPull=[], scenarioNumbers=[], failFast=false, project=flank-open-source, maxTestShards=5, shardTime=-1, repeatTests=1, smartFlankGcsPath=, smartFlankDisableUpload=false, testTargetsAlwaysRun=[], filesToDownload=[], disableSharding=false, localResultDir=results, runTimeout=-1, fullJUnitResult=false, ignoreFailedTests=false, keepFilePath=false, outputStyle=Single, disableResultsUpload=false, defaultTestTime=120.0, defaultClassTestTime=240.0, useAverageTestTimeForNewTests=false, disableUsageStatistics=true, outputReportType=JSON}, appApk=/Users/piotr/Projekty/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk, additionalApks=[], autoGoogleLogin=false, useOrchestrator=false, roboDirectives=[], environmentVariables={}, grantPermissions=all, scenarioLabels=[], obbFiles=[], obbNames=[], performanceMetrics=false, testTargets=[], additionalAppTestApks=[{test=/Users/piotr/Projekty/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-success-debug-androidTest.apk, environmentVariables={}}, {test=/Users/piotr/Projekty/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-multiple-error-debug-androidTest.apk, environmentVariables={}}, {test=gs://flank-open-source.appspot.com/integration/app-single-success-debug-androidTest.apk, environmentVariables={}}], useLegacyJUnitResult=false, obfuscateDumpShards=false, testTargetsForShard=[]}, cost=OutputReportCostNode(physical=0.00, virtual=0.85, total=0.85), weblinks=[https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/8163272579342792445, https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/7823004500830981676, https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6008992608198555068], testResults={matrix-1dykzhbjmybt4=[TextAxis(device=NexusLowRes-28-en-portrait, outcome=success, details=20 test cases passed, testSuiteOverview=TestSuiteOverview(total=20, errors=0, failures=0, flakes=0, skipped=0, elapsedTime=3.93, overheadTime=0.0)), TextAxis(device=Nexus6P-27-en-portrait, outcome=success, details=20 test cases passed, testSuiteOverview=TestSuiteOverview(total=20, errors=0, failures=0, flakes=0, skipped=0, elapsedTime=5.126, overheadTime=0.0)), TextAxis(device=Pixel2-28-en-portrait, outcome=success, details=20 test cases passed, testSuiteOverview=TestSuiteOverview(total=20, errors=0, failures=0, flakes=0, skipped=0, elapsedTime=5.609, overheadTime=0.0))], matrix-cxwb10mj5t9aa=[TextAxis(device=NexusLowRes-28-en-portrait, outcome=failure, details=5 test cases failed, 15 passed, testSuiteOverview=TestSuiteOverview(total=20, errors=0, failures=5, flakes=0, skipped=0, elapsedTime=4.246, overheadTime=0.0)), TextAxis(device=Nexus6P-27-en-portrait, outcome=failure, details=5 test cases failed, 15 passed, testSuiteOverview=TestSuiteOverview(total=20, errors=0, failures=5, flakes=0, skipped=0, elapsedTime=6.719, overheadTime=0.0)), TextAxis(device=Pixel2-28-en-portrait, outcome=failure, details=5 test cases failed, 15 passed, testSuiteOverview=TestSuiteOverview(total=20, errors=0, failures=5, flakes=0, skipped=0, elapsedTime=5.802, overheadTime=0.0))], matrix-g8g9m3j0ui2xa=[TextAxis(device=NexusLowRes-28-en-portrait, outcome=success, details=1 test cases passed, testSuiteOverview=TestSuiteOverview(total=1, errors=0, failures=0, flakes=0, skipped=0, elapsedTime=1.43, overheadTime=0.0)), TextAxis(device=Pixel2-28-en-portrait, outcome=success, details=1 test cases passed, testSuiteOverview=TestSuiteOverview(total=1, errors=0, failures=0, flakes=0, skipped=0, elapsedTime=1.696, overheadTime=0.0)), TextAxis(device=Nexus6P-27-en-portrait, outcome=success, details=1 test cases passed, testSuiteOverview=TestSuiteOverview(total=1, errors=0, failures=0, flakes=0, skipped=0, elapsedTime=1.8359999999999999, overheadTime=0.0))]}, error=)
""".trimIndent()
val sampleReport = "{\"args\":{\"commonArgs\":{\"data\":\"---\\ngcloud:\\n  app: \\\"./test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk\\\"\\n  test: \\\"./test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk\\\"\\nflank:\\n  disable-sharding: true\\n  output-report: \\\"json\\\"\\n\",\"devices\":[{\"model\":\"NexusLowRes\",\"version\":\"28\",\"locale\":\"en\",\"orientation\":\"portrait\",\"isVirtual\":true}],\"resultsBucket\":\"test-lab-v9cn46bb990nx-kz69ymd4nm9aq\",\"resultsDir\":\"2021-03-18_17-55-56.531000_WCSv\",\"recordVideo\":false,\"testTimeout\":\"15m\",\"async\":false,\"flakyTestAttempts\":0,\"otherFiles\":{},\"directoriesToPull\":[],\"scenarioNumbers\":[],\"failFast\":false,\"project\":\"flank-open-source\",\"maxTestShards\":1,\"shardTime\":-1,\"repeatTests\":1,\"smartFlankGcsPath\":\"\",\"smartFlankDisableUpload\":false,\"testTargetsAlwaysRun\":[],\"filesToDownload\":[],\"disableSharding\":true,\"localResultDir\":\"results\",\"runTimeout\":\"-1\",\"fullJUnitResult\":false,\"ignoreFailedTests\":false,\"keepFilePath\":false,\"outputStyle\":\"Single\",\"disableResultsUpload\":false,\"defaultTestTime\":120,\"defaultClassTestTime\":240,\"useAverageTestTimeForNewTests\":false,\"disableUsageStatistics\":false,\"outputReportType\":\"JSON\"},\"appApk\":\"/Users/piotr/Projekty/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-debug.apk\",\"testApk\":\"/Users/piotr/Projekty/gogo/flank/test_runner/src/test/kotlin/ftl/fixtures/tmp/apk/app-single-success-debug-androidTest.apk\",\"additionalApks\":[],\"autoGoogleLogin\":false,\"useOrchestrator\":true,\"roboDirectives\":[],\"environmentVariables\":{},\"grantPermissions\":\"all\",\"scenarioLabels\":[],\"obbFiles\":[],\"obbNames\":[],\"performanceMetrics\":false,\"testTargets\":[],\"additionalAppTestApks\":[],\"useLegacyJUnitResult\":false,\"obfuscateDumpShards\":false,\"testTargetsForShard\":[]},\"cost\":{\"physical\":0,\"virtual\":0.02,\"total\":0.02},\"test_results\":{\"matrix-3qnwchlja84yr\":{\"app\":\"app-debug.apk\",\"test-axises\":[{\"device\":\"NexusLowRes-28-en-portrait\",\"outcome\":\"success\",\"details\":\"1 test cases passed, 1 skipped\",\"testSuiteOverview\":{\"total\":2,\"errors\":0,\"failures\":0,\"flakes\":0,\"skipped\":1,\"elapsedTime\":3.36,\"overheadTime\":0}}]}},\"weblinks\":[\"https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.da0c237aaa33732/matrices/6291368458442747683/executions/bs.840902843ccb0706\"]}"
val s = """
    {
      "args": {
        "commonArgs": {
          "data": "---\ngcloud:\n  app: \"../test_projects/android/gameloop/app-debug.apk\"\n  type: \"game-loop\"\n  obb-files:\n  - \"../test_projects/android/gameloop/test.obb\"\n  obb-names:\n  - \"main.0300110.com.flank.gameloop.obb\"\nflank:\n  disable-sharding: true\n  disable-usage-statistics: true\n  output-report: \"json\"\n",
          "devices": [
            {
              "model": "NexusLowRes",
              "version": "28",
              "locale": "en",
              "orientation": "portrait",
              "isVirtual": true
            }
          ],
          "resultsBucket": "test-lab-v9cn46bb990nx-kz69ymd4nm9aq",
          "resultsDir": "2021-03-19_15-44-51.256003_lxhM",
          "recordVideo": false,
          "testTimeout": "15m",
          "async": false,
          "flakyTestAttempts": 0,
          "otherFiles": {},
          "type": "GAMELOOP",
          "directoriesToPull": [],
          "scenarioNumbers": [],
          "failFast": false,
          "project": "flank-open-source",
          "maxTestShards": 1,
          "shardTime": -1,
          "repeatTests": 1,
          "smartFlankGcsPath": "",
          "smartFlankDisableUpload": false,
          "testTargetsAlwaysRun": [],
          "filesToDownload": [],
          "disableSharding": true,
          "localResultDir": "results",
          "runTimeout": "-1",
          "fullJUnitResult": false,
          "ignoreFailedTests": false,
          "keepFilePath": false,
          "outputStyle": "Verbose",
          "disableResultsUpload": false,
          "defaultTestTime": 120.0,
          "defaultClassTestTime": 240.0,
          "useAverageTestTimeForNewTests": false,
          "disableUsageStatistics": true,
          "outputReportType": "JSON"
        },
        "appApk": "/Users/adamfilipowicz/Repos/flank/test_projects/android/gameloop/app-debug.apk",
        "additionalApks": [],
        "autoGoogleLogin": false,
        "useOrchestrator": true,
        "roboDirectives": [],
        "environmentVariables": {},
        "grantPermissions": "all",
        "scenarioLabels": [],
        "obbFiles": [
          "../test_projects/android/gameloop/test.obb"
        ],
        "obbNames": [
          "main.0300110.com.flank.gameloop.obb"
        ],
        "performanceMetrics": false,
        "testTargets": [],
        "additionalAppTestApks": [],
        "useLegacyJUnitResult": false,
        "obfuscateDumpShards": false,
        "testTargetsForShard": []
      },
      "cost": {
        "physical": 0.00,
        "virtual": 0.02,
        "total": 0.02
      },
      "test_results": {
        "matrix-2ma0nhf2cpw46": [
          {
            "device": "NexusLowRes-28-en-portrait",
            "outcome": "success",
            "details": "0 test cases passed",
            "testSuiteOverview": {
              "total": 0,
              "errors": 0,
              "failures": 0,
              "flakes": 0,
              "skipped": 0,
              "elapsedTime": 0.0,
              "overheadTime": 0.0
            }
          }
        ]
      },
      "weblinks": [
        "https://console.firebase.google.com/project/flank-open-source/testlab/histories/bh.8a1e5d4523c01177/matrices/7248921479148802747/executions/bs.1c88a167f4013f82"
      ]
    }
""".trimIndent()
