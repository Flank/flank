package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.config.Device
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class AndroidArgsTest {
    private val empty = emptyList<String>()
    private val appApk = "../test_app/apks/app-debug.apk"
    private val testApk = "../test_app/apks/app-debug-androidTest.apk"
    private val testErrorApk = "../test_app/apks/error-androidTest.apk"

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
          - model: NexusLowRes
            version: 23
            locale: en
            orientation: portrait

        flank:
          testShards: 7
          repeatTests: 8
          test-targets-always-run:
            - class example.Test#grantPermission
      """

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()!!

    @Test
    fun androidArgs_invalidModel() {
        expectedException.expect(RuntimeException::class.java)
        expectedException.expectMessage("Unsupported model id")
        AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: no
            version: nope
      """
        )
    }

    @Test
    fun androidArgs_invalidVersion() {
        expectedException.expect(RuntimeException::class.java)
        expectedException.expectMessage("Unsupported version id")
        AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: NexusLowRes
            version: nope
      """
        )
    }

    @Test
    fun androidArgs_incompatibleModel() {
        expectedException.expect(RuntimeException::class.java)
        expectedException.expectMessage("Incompatible model")
        AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: shamu
            version: 18
      """
        )
    }

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
            assert(devices, listOf(Device("NexusLowRes", "23", "en", "portrait")))

            // FlankYml
            assert(testShards, 7)
            assert(repeatTests, 8)
            assert(testTargetsAlwaysRun, listOf("class example.Test#grantPermission"))
        }
    }

    @Test
    fun androidArgsToString() {
        val androidArgs = AndroidArgs.load(androidNonDefault)
        assert(
                androidArgs.toString(), """
AndroidArgs
    gcloud:
      resultsBucket: mockBucket
      recordVideo: false
      testTimeout: 70m
      async: true
      projectId: projectFoo
      # Android gcloud
      appApk: ../test_app/apks/app-debug.apk
      testApk: ../test_app/apks/app-debug-androidTest.apk
      autoGoogleLogin: false
      useOrchestrator: false
      environmentVariables: {clearPackageData=true}
      directoriesToPull: [/sdcard/screenshots]
      performanceMetrics: false
      testTargets: [class com.example.app.ExampleUiTest#testPasses]
      devices: [Device(model=NexusLowRes, version=23, locale=en, orientation=portrait)]

    flank:
      testShards: 7
      repeatTests: 8
      testTargetsAlwaysRun: [class example.Test#grantPermission]
""".trimIndent()
        )
    }

    @Test
    fun androidArgsDefault() {
        val androidArgs = AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk
      """
        )

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
            assert(repeatTests, 1)
            assert(testTargetsAlwaysRun, empty)
        }
    }

    @Test
    fun negativeOneTestShards() {
        val androidArgs = AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testErrorApk

        flank:
          testShards: -1
      """
        )

        with(androidArgs) {
            assert(testShards, -1)
            assert(testShardChunks.size, 2)
            testShardChunks.forEach { chunk -> assert(chunk.size, 1) }
        }
    }

    @Test
    fun androidArgs_emptyFlank() {
        val androidArgs = AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
      """
        )

        assertThat(androidArgs.appApk).isEqualTo(appApk)
        assertThat(androidArgs.testApk).isEqualTo(testApk)
    }
}
