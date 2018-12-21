package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
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
    private val appApkAbsolutePath = appApk.absolutePath()
    private val testApkAbsolutePath = testApk.absolutePath()

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
          directories-to-download:
            - /sdcard/screenshots
            - /sdcard/screenshots2
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

        with(androidArgs) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, false)
            assert(testTimeout, "70m")
            assert(async, true)
            assert(project, "projectFoo")
            assert(resultsHistoryName ?: "", "android-history")

            // AndroidGcloudYml
            assert(appApk, appApkAbsolutePath)
            assert(testApk, testApkAbsolutePath)
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
            assert(directoriesToDownload, listOf("/sdcard/screenshots", "/sdcard/screenshots2"))
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
      results-dir: null
      record-video: false
      timeout: 70m
      async: true
      project: projectFoo
      results-history-name: android-history
      # Android gcloud
      app: $appApkAbsolutePath
      test: $testApkAbsolutePath
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
      smartFlankGcsPath:${' '}
      directories-to-download:
        - /sdcard/screenshots
        - /sdcard/screenshots2
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
            assert(project, "mockProjectId")

            // AndroidGcloudYml
            assert(appApk, appApkAbsolutePath)
            assert(testApk, testApkAbsolutePath)
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
            assert(directoriesToDownload, empty)
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

        assertThat(androidArgs.appApk).isEqualTo(appApkAbsolutePath)
        assertThat(androidArgs.testApk).isEqualTo(testApkAbsolutePath)
    }

    @Test
    fun cli_app() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--app", testApk)

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).appApk).isEqualTo(appApkAbsolutePath)

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.appApk).isEqualTo(testApkAbsolutePath)
    }

    @Test
    fun cli_test() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--test", appApk)

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).testApk).isEqualTo(testApkAbsolutePath)

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.testApk).isEqualTo(appApkAbsolutePath)
        assertThat(androidArgs.cli).isEqualTo(cli)
    }

    @Test
    fun cli_testTargets() {
        val cli = AndroidRunCommand()
        val testTarget = "class com.foo.ClassName"

        CommandLine(cli).parse("--test-targets=$testTarget,$testTarget")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          test-targets:
            - class com.example.app.ExampleUiTest#testPasses
            - class com.example.app.ExampleUiTest#testPasses
            - class com.example.app.ExampleUiTest#testFails
      """
        assertThat(AndroidArgs.load(yaml).testTargets.size).isEqualTo(3)

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.testTargets.size).isEqualTo(2)
        assertThat(androidArgs.testTargets).isEqualTo(listOf(testTarget, testTarget))
    }

    @Test
    fun cli_useOrchestrator() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--use-orchestrator")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          use-orchestrator: false
      """
        assertThat(AndroidArgs.load(yaml).useOrchestrator).isFalse()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.useOrchestrator).isTrue()
    }

    @Test
    fun cli_noUseOrchestrator() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--no-use-orchestrator")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          use-orchestrator: true
      """
        assertThat(AndroidArgs.load(yaml).useOrchestrator).isTrue()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.useOrchestrator).isFalse()
    }

    @Test
    fun cli_autoGoogleLogin() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--auto-google-login")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          auto-google-login: false
      """
        assertThat(AndroidArgs.load(yaml).autoGoogleLogin).isFalse()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.autoGoogleLogin).isTrue()
    }

    @Test
    fun cli_noAutoGoogleLogin() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--no-auto-google-login")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          auto-google-login: true
      """
        assertThat(AndroidArgs.load(yaml).autoGoogleLogin).isTrue()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.autoGoogleLogin).isFalse()
    }

    @Test
    fun cli_performanceMetrics() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--performance-metrics")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          performance-metrics: false
      """
        assertThat(AndroidArgs.load(yaml).performanceMetrics).isFalse()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.performanceMetrics).isTrue()
    }

    @Test
    fun cli_noPerformanceMetrics() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--no-performance-metrics")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          performance-metrics: true
      """
        assertThat(AndroidArgs.load(yaml).performanceMetrics).isTrue()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.performanceMetrics).isFalse()
    }

    @Test
    fun cli_environmentVariables() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--environment-variables=a=1,b=2")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).environmentVariables).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli)
        val expectedMap = mapOf("a" to "1", "b" to "2")
        assertThat(androidArgs.environmentVariables).isEqualTo(expectedMap)
    }

    @Test
    fun cli_directoriesToPull() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--directories-to-pull=a,b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).directoriesToPull).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.directoriesToPull).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun cli_directoriesToDownload() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--directories-to-download=a,b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).directoriesToDownload).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.directoriesToDownload).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun cli_device() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--device=model=shamu,version=22,locale=zh_CN,orientation=default")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        val expectedDefaultDevice = Device(defaultAndroidModel, defaultAndroidVersion)
        val defaultDevices = AndroidArgs.load(yaml).devices
        assertThat(defaultDevices.first()).isEqualTo(expectedDefaultDevice)
        assertThat(defaultDevices.size).isEqualTo(1)

        val androidArgs = AndroidArgs.load(yaml, cli)
        val expectedDevice = Device("shamu", "22", "zh_CN", "default")
        val actualDevices = androidArgs.devices
        assertThat(actualDevices.first()).isEqualTo(expectedDevice)
        assertThat(actualDevices.size).isEqualTo(1)
    }

    @Test
    fun cli_device_repeat() {
        val cli = AndroidRunCommand()
        val deviceCmd = "--device=model=shamu,version=22,locale=zh_CN,orientation=default"
        CommandLine(cli).parse(deviceCmd, deviceCmd)

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        val androidArgs = AndroidArgs.load(yaml, cli)
        val expectedDevice = Device("shamu", "22", "zh_CN", "default")
        val actualDevices = androidArgs.devices
        assertThat(actualDevices.size).isEqualTo(2)
        assertThat(actualDevices[0]).isEqualTo(expectedDevice)
        assertThat(actualDevices[1]).isEqualTo(expectedDevice)
    }

    // gcloudYml

    @Test
    fun cli_resultsBucket() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--results-bucket=a")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-bucket: b
      """
        assertThat(AndroidArgs.load(yaml).resultsBucket).isEqualTo("b")
        assertThat(AndroidArgs.load(yaml, cli).resultsBucket).isEqualTo("a")
    }

    @Test
    fun cli_recordVideo() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--record-video")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          record-video: false
      """
        assertThat(AndroidArgs.load(yaml).recordVideo).isFalse()
        assertThat(AndroidArgs.load(yaml, cli).recordVideo).isTrue()
    }

    @Test
    fun cli_noRecordVideo() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--no-record-video")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          record-video: true
      """
        assertThat(AndroidArgs.load(yaml).recordVideo).isTrue()
        assertThat(AndroidArgs.load(yaml, cli).recordVideo).isFalse()
    }

    @Test
    fun cli_timeout() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--timeout=1m")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          timeout: 2m
      """
        assertThat(AndroidArgs.load(yaml).testTimeout).isEqualTo("2m")
        assertThat(AndroidArgs.load(yaml, cli).testTimeout).isEqualTo("1m")
    }

    @Test
    fun cli_async() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--async")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          async: false
      """
        assertThat(AndroidArgs.load(yaml).async).isEqualTo(false)
        assertThat(AndroidArgs.load(yaml, cli).async).isEqualTo(true)
    }

    @Test
    fun cli_project() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--project=b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          project: a
      """
        assertThat(AndroidArgs.load(yaml).project).isEqualTo("a")
        assertThat(AndroidArgs.load(yaml, cli).project).isEqualTo("b")
    }

    @Test
    fun cli_resultsHistoryName() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--results-history-name=b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-history-name: a
      """
        assertThat(AndroidArgs.load(yaml).resultsHistoryName).isEqualTo("a")
        assertThat(AndroidArgs.load(yaml, cli).resultsHistoryName).isEqualTo("b")
    }

    @Test
    fun cli_testShards() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--test-shards=3")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
          testShards: 2
      """
        assertThat(AndroidArgs.load(yaml).testShards).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).testShards).isEqualTo(3)
    }

    @Test
    fun cli_repeatTests() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--repeat-tests=3")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
          repeatTests: 2
      """
        assertThat(AndroidArgs.load(yaml).repeatTests).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).repeatTests).isEqualTo(3)
    }

    @Test
    fun cli_testTargetsAlwaysRun() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--test-targets-always-run=com.A,com.B")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml, cli).testTargetsAlwaysRun).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun cli_resultsDir() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parse("--results-dir=b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: a
      """

        assertThat(AndroidArgs.load(yaml).resultsDir).isEqualTo("a")
        assertThat(AndroidArgs.load(yaml, cli).resultsDir).isEqualTo("b")
    }
}
