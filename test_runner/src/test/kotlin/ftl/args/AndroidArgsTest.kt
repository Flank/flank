package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.AppTestPair
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.run.platform.runAndroidTests
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.getPath
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import ftl.util.FlankFatalError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import picocli.CommandLine

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class AndroidArgsTest {
    private val empty = emptyList<String>()
    private val appApk = "../test_app/apks/app-debug.apk"
    private val invalidApk = "../test_app/apks/invalid.apk"
    private val testApk = "../test_app/apks/app-debug-androidTest.apk"
    private val testErrorApk = "../test_app/apks/error-androidTest.apk"
    private val testFlakyApk = "../test_app/apks/flaky-androidTest.apk"
    private val appApkAbsolutePath = appApk.absolutePath()
    private val testApkAbsolutePath = testApk.absolutePath()
    private val testErrorApkAbsolutePath = testErrorApk.absolutePath()
    private val testFlakyApkAbsolutePath = testFlakyApk.absolutePath()
    private val simpleFlankPath = getPath("src/test/kotlin/ftl/fixtures/simple-android-flank.yml")

    private val androidNonDefault = """
        gcloud:
          results-bucket: mockBucket
          record-video: false
          timeout: 70m
          async: true
          client-details:
            key1: value1
            key2: value2
          network-profile: LTE
          project: projectFoo
          results-history-name: android-history

          app: $appApk
          test: $testApk
          additional-apks:
            - $testErrorApk
            - $testFlakyApk
          auto-google-login: false
          use-orchestrator: false
          environment-variables:
            clearPackageData: true
            randomEnvVar: false
          directories-to-pull:
          - /sdcard/screenshots
          - /sdcard/screenshots2
          other-files:
            /sdcard/dir1/file1.txt: $appApk
            /sdcard/dir2/file2.jpg: $testApk
          performance-metrics: false
          test-runner-class: com.foo.TestRunner
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
          num-flaky-test-attempts: 3

        flank:
          max-test-shards: 7
          shard-time: 60
          num-test-runs: 8
          files-to-download:
            - /sdcard/screenshots
            - /sdcard/screenshots2
          test-targets-always-run:
            - class example.Test#grantPermission
            - class example.Test#grantPermission2
          disable-sharding: true
          keep-file-path: true
          additional-app-test-apks:
            - app: $appApk
              test: $testErrorApk
          run-timeout: 20m
      """

    @Rule
    @JvmField
    var expectedException = ExpectedException.none()!!

    @Test
    fun `empty testTargets`() {
        val emptyTestTargets = """
        gcloud:
          app: $appApk
          test: $testApk
          test-targets:
          - 

        """.trimIndent()

        val args = AndroidArgs.load(emptyTestTargets)
        assertThat(args.testTargets.size).isEqualTo(0)
    }

    @Test
    fun `androidArgs invalidModel`() {
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
    fun `androidArgs invalidVersion`() {
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
    fun `androidArgs incompatibleModel`() {
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
            assert(
                clientDetails,
                mapOf(
                    "key1" to "value1",
                    "key2" to "value2"
                )
            )
            assert(networkProfile, "LTE")
            assert(project, "projectFoo")
            assert(resultsHistoryName ?: "", "android-history")

            // AndroidGcloudYml
            assert(appApk, appApkAbsolutePath)
            assert(testApk, testApkAbsolutePath)
            assert(additionalApks, listOf(testErrorApkAbsolutePath, testFlakyApkAbsolutePath))
            assert(autoGoogleLogin, false)
            assert(useOrchestrator, false)
            assert(environmentVariables, linkedMapOf("clearPackageData" to "true", "randomEnvVar" to "false"))
            assert(directoriesToPull, listOf("/sdcard/screenshots", "/sdcard/screenshots2"))
            assert(
                otherFiles, mapOf(
                    "/sdcard/dir1/file1.txt" to appApkAbsolutePath,
                    "/sdcard/dir2/file2.jpg" to testApkAbsolutePath
                )
            )
            assert(performanceMetrics, false)
            assert(testRunnerClass, "com.foo.TestRunner")
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
            assert(flakyTestAttempts, 3)

            // FlankYml
            assert(maxTestShards, 7)
            assert(shardTime, 60)
            assert(repeatTests, 8)
            assert(filesToDownload, listOf("/sdcard/screenshots", "/sdcard/screenshots2"))
            assert(
                testTargetsAlwaysRun, listOf(
                    "class example.Test#grantPermission",
                    "class example.Test#grantPermission2"
                )
            )
            assert(disableSharding, true)
            assert(runTimeout, "20m")
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
      client-details: 
        key1: value1
        key2: value2
      network-profile: LTE
      results-history-name: android-history
      # Android gcloud
      app: $appApkAbsolutePath
      test: $testApkAbsolutePath
      additional-apks: 
        - $testErrorApkAbsolutePath
        - $testFlakyApkAbsolutePath
      auto-google-login: false
      use-orchestrator: false
      directories-to-pull:
        - /sdcard/screenshots
        - /sdcard/screenshots2
      other-files:
        /sdcard/dir1/file1.txt: $appApkAbsolutePath
        /sdcard/dir2/file2.jpg: $testApkAbsolutePath
      performance-metrics: false
      test-runner-class: com.foo.TestRunner
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
      num-flaky-test-attempts: 3

    flank:
      max-test-shards: 7
      shard-time: 60
      num-test-runs: 8
      smart-flank-gcs-path:${' '}
      smart-flank-disable-upload: false
      files-to-download:
        - /sdcard/screenshots
        - /sdcard/screenshots2
      test-targets-always-run:
        - class example.Test#grantPermission
        - class example.Test#grantPermission2
      disable-sharding: true
      project: projectFoo
      local-result-dir: results
      # Android Flank Yml
      keep-file-path: true
      additional-app-test-apks:
        - app: $appApkAbsolutePath
          test: $testErrorApkAbsolutePath
      run-timeout: 20m
      legacy-junit-result: false
""".trimIndent()
        )
    }

    @Test
    fun `verify default yml toString`() {
        val args = AndroidArgs.load(simpleFlankPath)
        assertEquals("""
AndroidArgs
    gcloud:
      results-bucket: mockBucket
      results-dir: null
      record-video: false
      timeout: 15m
      async: false
      client-details: 
      network-profile: null
      results-history-name: null
      # Android gcloud
      app: $appApkAbsolutePath
      test: $testApkAbsolutePath
      additional-apks: 
      auto-google-login: false
      use-orchestrator: true
      directories-to-pull:
      other-files:
      performance-metrics: false
      test-runner-class: null
      test-targets:
      device:
        - model: NexusLowRes
          version: 28
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0

    flank:
      max-test-shards: 1
      shard-time: -1
      num-test-runs: 1
      smart-flank-gcs-path: 
      smart-flank-disable-upload: false
      files-to-download:
      test-targets-always-run:
      disable-sharding: false
      project: mockProjectId
      local-result-dir: results
      # Android Flank Yml
      keep-file-path: false
      additional-app-test-apks:
      run-timeout: -1
      legacy-junit-result: true
        """.trimIndent(), args.toString())
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
            assert(recordVideo, false)
            assert(testTimeout, "15m")
            assert(async, false)
            assert(project, "mockProjectId")
            assert(clientDetails, null)
            assert(networkProfile, null)

            // AndroidGcloudYml
            assert(appApk, appApkAbsolutePath)
            assert(testApk, testApkAbsolutePath)
            assert(autoGoogleLogin, false)
            assert(useOrchestrator, true)
            assert(environmentVariables, emptyMap<String, String>())
            assert(directoriesToPull, empty)
            assert(performanceMetrics, false)
            assert(testRunnerClass, null)
            assert(testTargets, empty)
            assert(devices, listOf(Device("NexusLowRes", "28")))
            assert(flakyTestAttempts, 0)

            // FlankYml
            assert(maxTestShards, 1)
            assert(repeatTests, 1)
            assert(filesToDownload, empty)
            assert(testTargetsAlwaysRun, empty)
            assert(disableSharding, false)
            assert(runTimeout, "-1")
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
          max-test-shards: -1
      """
        )

        val testShardChunks = AndroidTestShard.getTestShardChunks(androidArgs, androidArgs.testApk)
        with(androidArgs) {
            assert(maxTestShards, -1)
            assert(testShardChunks.size, 2)
            testShardChunks.forEach { chunk -> assert(chunk.size, 1) }
        }
    }

    @Test
    fun `androidArgs emptyFlank`() {
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
    fun `disableSharding allows using invalid apk`() {
        val yaml = """
        gcloud:
          app: $invalidApk
          test: $invalidApk
        flank:
          disable-sharding: true
      """
        val androidArgs = AndroidArgs.load(yaml)
        val testShardChunks = AndroidTestShard.getTestShardChunks(androidArgs, androidArgs.testApk)
        assertThat(testShardChunks).hasSize(1)
    }

    @Test(expected = FlankFatalError::class)
    fun `Invalid apk throws`() {
        val yaml = """
        gcloud:
          app: $invalidApk
          test: $invalidApk
      """
        val androidArgs = AndroidArgs.load(yaml)
        AndroidTestShard.getTestShardChunks(androidArgs, androidArgs.testApk)
    }

    @Test
    fun `cli app`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--app", testApk)

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
    fun `cli test`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--test", appApk)

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
    fun `cli testTargets`() {
        val cli = AndroidRunCommand()
        val testTarget = "class com.foo.ClassName"

        CommandLine(cli).parseArgs("--test-targets=$testTarget,$testTarget")

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
    fun `cli useOrchestrator`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--use-orchestrator")

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
    fun `cli noUseOrchestrator`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--no-use-orchestrator")

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
    fun `cli autoGoogleLogin`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--auto-google-login")

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
    fun `cli noAutoGoogleLogin`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--no-auto-google-login")

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
    fun `cli performanceMetrics`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--performance-metrics")

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
    fun `cli noPerformanceMetrics`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--no-performance-metrics")

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
    fun `cli testRunnerClass`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--test-runner-class=com.foo.bar.TestRunner")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).testRunnerClass).isNull()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.testRunnerClass).isEqualTo("com.foo.bar.TestRunner")
    }

    @Test
    fun `cli environmentVariables`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--environment-variables=a=1,b=2")

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
    fun `cli directoriesToPull`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--directories-to-pull=a,b")

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
    fun `cli filesToDownload`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--files-to-download=a,b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).filesToDownload).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.filesToDownload).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun `cli device`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--device=model=shamu,version=22,locale=zh_CN,orientation=default")

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
    fun `cli device repeat`() {
        val cli = AndroidRunCommand()
        val deviceCmd = "--device=model=shamu,version=22,locale=zh_CN,orientation=default"
        CommandLine(cli).parseArgs(deviceCmd, deviceCmd)

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
    fun `cli resultsBucket`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--results-bucket=a")

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
    fun `cli recordVideo`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--record-video")

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
    fun `cli noRecordVideo`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--no-record-video")

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
    fun `cli timeout`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--timeout=1m")

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
    fun `cli async`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--async")

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
    fun `cli project`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--project=b")

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
    fun `cli resultsHistoryName`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--results-history-name=b")

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
    fun `cli maxTestShards`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--max-test-shards=3")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
          max-test-shards: 2
      """
        assertThat(AndroidArgs.load(yaml).maxTestShards).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).maxTestShards).isEqualTo(3)
    }

    @Test
    fun `cli shardTime`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--shard-time=3")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
          shard-time: 2
      """
        assertThat(AndroidArgs.load(yaml).shardTime).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).shardTime).isEqualTo(3)
    }

    @Test
    fun `cli disableSharding`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--disable-sharding")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
          disable-sharding: false
      """
        assertThat(AndroidArgs.load(yaml).disableSharding).isEqualTo(false)
        assertThat(AndroidArgs.load(yaml, cli).disableSharding).isEqualTo(true)
    }

    @Test
    fun `cli repeatTests`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--num-test-runs=3")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk

        flank:
          num-test-runs: 2
      """
        assertThat(AndroidArgs.load(yaml).repeatTests).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).repeatTests).isEqualTo(3)
    }

    @Test
    fun `cli testTargetsAlwaysRun`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--test-targets-always-run=com.A,com.B")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml, cli).testTargetsAlwaysRun).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun `cli resultsDir`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--results-dir=b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: a
      """

        assertThat(AndroidArgs.load(yaml).resultsDir).isEqualTo("a")
        assertThat(AndroidArgs.load(yaml, cli).resultsDir).isEqualTo("b")
    }

    @Test
    fun `cli flakyTestAttempts`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--num-flaky-test-attempts=3")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).flakyTestAttempts).isEqualTo(0)

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.flakyTestAttempts).isEqualTo(3)
    }

    @Test
    fun `cli smart-flank-gcs-path`() {
        val cli = AndroidRunCommand()
        val xml = "gs://bucket/foo.xml"
        CommandLine(cli).parseArgs("--smart-flank-gcs-path=$xml")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).smartFlankGcsPath).isEqualTo("")

        val args = AndroidArgs.load(yaml, cli)
        assertThat(args.smartFlankGcsPath).isEqualTo(xml)
    }

    @Test
    fun `cli local-result-dir`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--local-result-dir=b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          local-result-dir: a
      """
        assertThat(AndroidArgs.load(yaml).localResultDir).isEqualTo("a")

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.localResultDir).isEqualTo("b")
    }

    @Test
    fun `cli smart-flank-disable-upload`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--smart-flank-disable-upload=true")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          smart-flank-disable-upload: false
      """
        assertThat(AndroidArgs.load(yaml).smartFlankDisableUpload).isEqualTo(false)

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.smartFlankDisableUpload).isEqualTo(true)
    }

    @Test
    fun `cli additional-app-test-apks`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--additional-app-test-apks=app=$appApk,test=$testFlakyApk")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          additional-app-test-apks:
          - app: $appApk
            test: $testErrorApk
      """
        assertEquals(
            listOf(AppTestPair(appApkAbsolutePath, testErrorApkAbsolutePath)),
            AndroidArgs.load(yaml).additionalAppTestApks
        )

        assertEquals(
            listOf(AppTestPair(appApkAbsolutePath, testFlakyApkAbsolutePath)),
            AndroidArgs.load(yaml, cli).additionalAppTestApks
        )
    }

    @Test
    fun `cli keep-file-path`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--keep-file-path=true")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          keep-file-path: false
      """
        assertThat(AndroidArgs.load(yaml).keepFilePath).isEqualTo(false)

        val androidArgs = AndroidArgs.load(yaml, cli)
        assertThat(androidArgs.keepFilePath).isEqualTo(true)
    }

    @Test
    fun `additional test apks without app specified should have top level app provided -- yml file`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          additional-app-test-apks:
          - test: $testErrorApk
          - app: null
            test: $testErrorApk
          - app: $invalidApk
            test: $testApk
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml)
        val (matrixMap, chunks) = runBlocking { runAndroidTests(parsedYml) }
        assertEquals(4, matrixMap.map.size)
        assertEquals(4, chunks.size)
    }

    @Test
    fun `verify run timeout default value - android`() {
        val args = AndroidArgs.load(simpleFlankPath)
        assertEquals(Long.MAX_VALUE, args.parsedTimeout)
    }
}
