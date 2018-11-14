package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import picocli.CommandLine

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
          results-history-name: android-history

          app: $appApk
          test: $testApk
          auto-google-login: false
          use-orchestrator: false
          environment-variables:
            clearPackageData: true
            randomEnvVar: false
          directories-to-pull:
          - /sdcard/screenshots
          - /sdcard/screenshots2
          performance-metrics: false
          test-targets:
          - class com.example.app.ExampleUiTest#testPasses
          - class com.example.app.ExampleUiTest#testFails
          device:
          - model: NexusLowRes
            version: 23
            locale: en
            orientation: portrait
          - model: NexusLowRes
            version: 24
            locale: en
            orientation: portrait

        flank:
          testShards: 7
          repeatTests: 8
          test-targets-always-run:
            - class example.Test#grantPermission
            - class example.Test#grantPermission2
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
        val expectedAppApk = appApk
        val expectedTestApk = testApk

        with(androidArgs) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, false)
            assert(testTimeout, "70m")
            assert(async, true)
            assert(projectId, "projectFoo")
            assert(resultsHistoryName ?: "", "android-history")

            // AndroidGcloudYml
            assert(appApk, expectedAppApk)
            assert(testApk, expectedTestApk)
            assert(autoGoogleLogin, false)
            assert(useOrchestrator, false)
            assert(environmentVariables, linkedMapOf("clearPackageData" to "true", "randomEnvVar" to "false"))
            assert(directoriesToPull, listOf("/sdcard/screenshots", "/sdcard/screenshots2"))
            assert(performanceMetrics, false)
            assert(
                testTargets,
                listOf(
                    "class com.example.app.ExampleUiTest#testPasses",
                    "class com.example.app.ExampleUiTest#testFails"
                )
            )
            assert(
                devices, listOf(
                    Device("NexusLowRes", "23", "en", "portrait"),
                    Device("NexusLowRes", "24", "en", "portrait")
                )
            )

            // FlankYml
            assert(testShards, 7)
            assert(repeatTests, 8)
            assert(
                testTargetsAlwaysRun, listOf(
                    "class example.Test#grantPermission",
                    "class example.Test#grantPermission2"
                )
            )
        }
    }

    @Test
    fun androidArgsToString() {
        val androidArgs = AndroidArgs.load(androidNonDefault)
        assert(
            androidArgs.toString(), """
AndroidArgs
    gcloud:
      results-bucket: mockBucket
      record-video: false
      timeout: 70m
      async: true
      project: projectFoo
      results-history-name: android-history
      # Android gcloud
      app: ../test_app/apks/app-debug.apk
      test: ../test_app/apks/app-debug-androidTest.apk
      auto-google-login: false
      use-orchestrator: false
      environment-variables:
        clearPackageData: true
        randomEnvVar: false
      directories-to-pull:
        - /sdcard/screenshots
        - /sdcard/screenshots2
      performance-metrics: false
      test-targets:
        - class com.example.app.ExampleUiTest#testPasses
        - class com.example.app.ExampleUiTest#testFails
      device:
        - model: NexusLowRes
          version: 23
          locale: en
          orientation: portrait
        - model: NexusLowRes
          version: 24
          locale: en
          orientation: portrait

    flank:
      testShards: 7
      repeatTests: 8
      test-targets-always-run:
        - class example.Test#grantPermission
        - class example.Test#grantPermission2
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

    @Test
    fun androidArgs_overrideAppFromCmdLine() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--app", testApk)

        val androidArgs = AndroidArgs.load(
            """
        gcloud:
          app: $appApk
          test: $testApk
      """,
            cli
        )

        assertThat(androidArgs.appApk).isEqualTo(testApk)
        assertThat(androidArgs.cli).isEqualTo(cli)
    }

    @Test
    fun androidArgs_overrideTestFromCmdLine() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--test", appApk)

        val androidArgs = AndroidArgs.load(
            """
        gcloud:
          app: $appApk
          test: $testApk
      """,
            cli
        )

        assertThat(androidArgs.testApk).isEqualTo(appApk)
        assertThat(androidArgs.cli).isEqualTo(cli)
    }

    @Test
    fun androidArgs_overrideTestTargetsFromCmdLine() {
        val cli = AndroidRunCommand()
        val testTarget = "class com.foo.ClassName"

        CommandLine(cli).parse("--test-targets", testTarget)

        val androidArgs = AndroidArgs.load(
            """
        gcloud:
          app: $appApk
          test: $testApk
          test-targets:
            - class com.example.app.ExampleUiTest#testPasses
            - class com.example.app.ExampleUiTest#testFails
      """,
            cli
        )

        assertThat(androidArgs.testTargets.size).isEqualTo(1)
        assertThat(androidArgs.testTargets).isEqualTo(listOf(testTarget))
    }
}
