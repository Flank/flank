package ftl.args

import ftl.config.Device
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper
import ftl.test.util.TestHelper.assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AndroidArgsTest {
    private val empty = emptyList<String>()
    private val appApk = "../../test_app/apks/app-debug.apk"
    private val testApk = "../../test_app/apks/app-debug-androidTest.apk"

    private val androidNonDefault = """
        gcloud:
          results-bucket: mockBucket
          record-video: false
          timeout: 70m
          async: true
          project: projectFoo

          app: $appApk
          test: $testApk
          auto-google-login: false
          use-orchestrator: false
          environment-variables:
            clearPackageData: true
          directories-to-pull:
          - /sdcard/screenshots
          performance-metrics: false
          test-targets:
          - class com.example.app.ExampleUiTest#testPasses
          device:
          - model: a
            version: b
            locale: c
            orientation: d

        flank:
          testShards: 7
          testRuns: 8
          test-targets-always-run:
            - class example.Test#grantPermission
      """

    @Test
    fun androidArgs() {
        val androidArgs = AndroidArgs.load(androidNonDefault)

        with(androidArgs) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, false)
            assert(testTimeout, "70m")
            assert(async, true)
            assert(projectId, "projectFoo")

            // AndroidGcloudYml
            assert(appApk, appApk)
            assert(testApk, testApk)
            assert(autoGoogleLogin, false)
            assert(useOrchestrator, false)
            assert(environmentVariables, linkedMapOf("clearPackageData" to "true"))
            assert(directoriesToPull, listOf("/sdcard/screenshots"))
            assert(performanceMetrics, false)
            assert(testTargets, listOf("class com.example.app.ExampleUiTest#testPasses"))
            assert(devices, listOf(Device("a", "b", "c", "d")))

            // FlankYml
            assert(testShards, 7)
            assert(testRuns, 8)
            assert(testTargetsAlwaysRun, listOf("class example.Test#grantPermission"))
        }
    }

    @Test
    fun androidArgsToString() {
        val androidArgs = AndroidArgs.load(androidNonDefault)
        assert(androidArgs.toString(), """
AndroidArgs
    gcloud:
      resultsBucket: mockBucket
      recordVideo: false
      testTimeout: 70m
      async: true
      projectId: projectFoo
      # Android gcloud
      appApk: ../../test_app/apks/app-debug.apk
      testApk: ../../test_app/apks/app-debug-androidTest.apk
      autoGoogleLogin: false
      useOrchestrator: false
      environmentVariables: {clearPackageData=true}
      directoriesToPull: [/sdcard/screenshots]
      performanceMetrics: false
      testTargets: [class com.example.app.ExampleUiTest#testPasses]
      devices: [Device(model=a, version=b, locale=c, orientation=d)]

    flank:
      testShards: 7
      testRuns: 8
      testTargetsAlwaysRun: [class example.Test#grantPermission]
      # computed properties
      testShardChunks: [[class example.Test#grantPermission, class com.example.app.ExampleUiTest#testPasses]]
""".trimIndent())
    }

    @Test
    fun androidArgsDefault() {
        val androidArgs = AndroidArgs.load("""
        gcloud:
          app: $appApk
          test: $testApk
      """)

        with(androidArgs) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, true)
            assert(testTimeout, "15m")
            assert(async, false)
            assert(projectId, "mockProjectId")

            // AndroidGcloudYml
            assert(appApk, appApk)
            assert(testApk, testApk)
            assert(autoGoogleLogin, true)
            assert(useOrchestrator, true)
            assert(environmentVariables, emptyMap<String, String>())
            assert(directoriesToPull, empty)
            assert(performanceMetrics, true)
            assert(testTargets, empty)
            assert(devices, listOf(Device("NexusLowRes", "28")))

            // FlankYml
            assert(testShards, 1)
            assert(testRuns, 1)
            assert(testTargetsAlwaysRun, empty)
        }
    }
}
