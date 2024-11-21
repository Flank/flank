package ftl.args

import com.google.common.truth.Truth.assertThat
import com.google.api.services.testing.model.TestSpecification
import ftl.args.IArgs.Companion.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE
import ftl.args.IArgs.Companion.AVAILABLE_VIRTUAL_ARM_SHARD_COUNT_RANGE
import ftl.args.IArgs.Companion.AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE
import ftl.args.yml.AppTestPair
import ftl.args.yml.Type
import ftl.client.google.GcStorage
import ftl.client.google.GcStorage.exist
import ftl.client.google.run.android.setupAndroidTest
import ftl.config.Device
import ftl.config.FtlConstants.defaultAndroidModel
import ftl.config.FtlConstants.defaultAndroidVersion
import ftl.presentation.cli.firebase.test.android.AndroidRunCommand
import ftl.run.exception.FlankConfigurationError
import ftl.run.exception.FlankGeneralError
import ftl.run.exception.FlankNoTestsError
import ftl.run.exception.IncompatibleTestDimensionError
import ftl.run.model.GameLoopContext
import ftl.run.model.InstrumentationTestContext
import ftl.run.platform.android.createAndroidTestContexts
import ftl.run.platform.android.createAndroidTestMatrixType
import ftl.run.platform.runAndroidTests
import ftl.run.status.OutputStyle
import ftl.shard.Chunk
import ftl.shard.TestMethod
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import ftl.test.util.TestHelper.getThrowable
import ftl.test.util.assertThrowsWithMessage
import ftl.util.asFileReference
import ftl.util.getMockedTestMatrix
import ftl.util.mockTestMatrices
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine
import java.io.StringReader
import java.nio.file.Files
import java.nio.file.Paths
import java.util.UUID

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class AndroidArgsTest {

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog()

    private val empty = emptyList<String>()
    private val appApk = "../test_projects/android/apks/app-debug.apk"
    private val appAab = "../test_projects/android/bundle/app-debug.aab"
    private val invalidApk = "../test_projects/android/apks/invalid.apk"
    private val nonExistingApk = "../test_projects/android/apks/app-debug_non_existing.apk"
    private val testApk = "../test_projects/android/apks/app-debug-androidTest.apk"
    private val testLargeParameterizedApk = "../test_projects/android/apks/app-Large-Parameterized.apk"
    private val testExtremeParameterizedApk = "../test_projects/android/apks/app-Extreme-ParameterizedTests.apk"
    private val testExtremeParameterizedOtherApk =
        "../test_projects/android/apks/app-Extreme-Other-ParameterizedTests.apk"
    private val testErrorApk = "../test_projects/android/apks/error-androidTest.apk"
    private val testFlakyApk = "../test_projects/android/apks/flaky-androidTest.apk"
    private val obbFile = "../test_projects/android/gameloop/test.obb"
    private val appApkAbsolutePath = appApk.absolutePath()
    private val testApkAbsolutePath = testApk.absolutePath()
    private val testErrorApkAbsolutePath = testErrorApk.absolutePath()
    private val testFlakyApkAbsolutePath = testFlakyApk.absolutePath()
    private val simpleFlankPath = getPath("src/test/kotlin/ftl/fixtures/simple-android-flank.yml")
    private val flankLocal = getPath("src/test/kotlin/ftl/fixtures/flank.local.yml")
    private val resultDir = "test_dir"
    private val androidNonDefault = """
        gcloud:
          results-bucket: mockBucket
          results-dir: $resultDir
          record-video: false
          timeout: 70m
          async: true
          client-details:
            key1: value1
            key2: value2
          network-profile: LTE
          project: project-foo
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
          grant-permissions: all
          type: instrumentation
          other-files:
            /sdcard/dir1/file1.txt: $appApk
            /sdcard/dir2/file2.jpg: $testApk
          performance-metrics: false
          num-uniform-shards: null
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
          default-test-time: 15.0
          use-average-test-time-for-new-tests: true
          files-to-download:
            - /sdcard/screenshots
            - /sdcard/screenshots2
          test-targets-always-run:
            - class example.Test#grantPermission
            - class example.Test#grantPermission2
          disable-sharding: true
          keep-file-path: true
          full-junit-result: true
          additional-app-test-apks:
            - app: $appApk
              test: $testErrorApk
              max-test-shards: 123
              test-targets:
              - class any.additional.TestClass#test1
              device:
              - model: Nexus9
                version: 23
          run-timeout: 20m
          ignore-failed-tests: true
          output-style: single
          disable-results-upload: true
          default-class-test-time: 30.0
          output-report: json
      """

    @After
    fun tearDown() = unmockkAll()

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
        assertThrowsWithMessage(IncompatibleTestDimensionError::class, "Unsupported model id") {
            AndroidArgs.load(
                """
            gcloud:
              app: $appApk
              test: $testApk
              device:
              - model: no
                version: nope
          """
            ).validate()
        }
    }

    @Test
    fun `androidArgs invalidVersion`() {
        assertThrowsWithMessage(IncompatibleTestDimensionError::class, "Unsupported version id") {
            AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: NexusLowRes
            version: nope
      """
            ).validate()
        }
    }

    @Test
    fun `androidArgs incompatibleModel`() {
        assertThrowsWithMessage(IncompatibleTestDimensionError::class, "Incompatible model") {
            AndroidArgs.load(
                """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: redfin
            version: 21
      """
            ).validate()
        }
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
            assert(project, "project-foo")
            assert(resultsHistoryName ?: "", "android-history")

            // AndroidGcloudYml
            assert(appApk, appApkAbsolutePath)
            assert(testApk, testApkAbsolutePath)
            assert(additionalApks, listOf(testErrorApkAbsolutePath, testFlakyApkAbsolutePath))
            assert(autoGoogleLogin, false)
            assert(useOrchestrator, false)
            assert(environmentVariables, linkedMapOf("clearPackageData" to "true", "randomEnvVar" to "false"))
            assert(directoriesToPull, listOf("/sdcard/screenshots", "/sdcard/screenshots2"))
            assert(grantPermissions, "all")
            assert(
                otherFiles,
                mapOf(
                    "/sdcard/dir1/file1.txt" to appApkAbsolutePath,
                    "/sdcard/dir2/file2.jpg" to testApkAbsolutePath
                )
            )
            assert(type, Type.INSTRUMENTATION)
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
                devices,
                listOf(
                    Device("NexusLowRes", "23", "en", "portrait", isVirtual = true),
                    Device("NexusLowRes", "24", "en", "portrait", isVirtual = true)
                )
            )
            assert(flakyTestAttempts, 3)

            // FlankYml
            assert(maxTestShards, 7)
            assert(shardTime, 60)
            assert(repeatTests, 8)
            assert(filesToDownload, listOf("/sdcard/screenshots", "/sdcard/screenshots2"))
            assert(
                testTargetsAlwaysRun,
                listOf(
                    "class example.Test#grantPermission",
                    "class example.Test#grantPermission2"
                )
            )
            assert(fullJUnitResult, true)
            assert(disableSharding, true)
            assert(runTimeout, "20m")
            assert(outputStyle, OutputStyle.Single)
        }
    }

    @Test
    fun androidArgsToString() {
        val androidArgs = AndroidArgs.load(androidNonDefault)
        assert(
            androidArgs.toString(),
            """
AndroidArgs
    gcloud:
      results-bucket: mockBucket
      results-dir: $resultDir
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
      grant-permissions: all
      type: instrumentation
      other-files:
        /sdcard/dir1/file1.txt: $appApkAbsolutePath
        /sdcard/dir2/file2.jpg: $testApkAbsolutePath
      scenario-numbers:
      scenario-labels:
      obb-files:
      obb-names:
      performance-metrics: false
      num-uniform-shards: null
      test-runner-class: com.foo.TestRunner
      test-targets:
        - class com.example.app.ExampleUiTest#testPasses
        - class com.example.app.ExampleUiTest#testFails
      robo-directives:
      robo-script: null
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
      test-targets-for-shard:
      fail-fast: false
      parameterized-tests: default

    flank:
      max-test-shards: 7
      shard-time: 60
      num-test-runs: 8
      smart-flank-gcs-path:${' '}
      smart-flank-disable-upload: false
      default-test-time: 15.0
      use-average-test-time-for-new-tests: true
      files-to-download:
        - /sdcard/screenshots
        - /sdcard/screenshots2
      test-targets-always-run:
        - class example.Test#grantPermission
        - class example.Test#grantPermission2
      disable-sharding: true
      project: project-foo
      local-result-dir: results
      full-junit-result: true
      # Android Flank Yml
      keep-file-path: true
      additional-app-test-apks:
        - app: $appApkAbsolutePath
          test: $testErrorApkAbsolutePath
          max-test-shards: 123
          test-targets:
          - class any.additional.TestClass#test1
          device:
          - model: Nexus9
            version: 23
            locale: en
            orientation: portrait
      run-timeout: 20m
      legacy-junit-result: false
      ignore-failed-tests: true
      output-style: single
      disable-results-upload: true
      default-class-test-time: 30.0
      disable-usage-statistics: false
      output-report: json
      skip-config-validation: false
      custom-sharding-json: 
            """.trimIndent()
        )
    }

    @Test
    fun `verify default yml toString`() {
        val args = AndroidArgs.load(simpleFlankPath)
        assertEquals(
            """
AndroidArgs
    gcloud:
      results-bucket: mockBucket
      results-dir: $resultDir
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
      test-targets-for-shard:
      fail-fast: false
      parameterized-tests: default

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
      project: mock-project-id
      local-result-dir: results
      full-junit-result: false
      # Android Flank Yml
      keep-file-path: false
      additional-app-test-apks:
      run-timeout: -1
      legacy-junit-result: true
      ignore-failed-tests: false
      output-style: verbose
      disable-results-upload: false
      default-class-test-time: 240.0
      disable-usage-statistics: false
      output-report: json
      skip-config-validation: false
      custom-sharding-json: 
            """.trimIndent(),
            args.toString()
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
            assert(recordVideo, false)
            assert(testTimeout, "15m")
            assert(async, false)
            assert(project, "mock-project-id")
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
            assert(devices, listOf(Device("NexusLowRes", "28", isVirtual = true)))
            assert(flakyTestAttempts, 0)

            // FlankYml
            assert(maxTestShards, 1)
            assert(repeatTests, 1)
            assert(filesToDownload, empty)
            assert(testTargetsAlwaysRun, empty)
            assert(disableSharding, false)
            assert(runTimeout, "-1")
            assert(outputStyle, OutputStyle.Verbose)
            assert(fullJUnitResult, false)
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

        val testShardChunks = getAndroidShardChunks(androidArgs)
        with(androidArgs) {
            assert(maxTestShards, AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last)
            assert(testShardChunks.size, 2)
            testShardChunks.forEach { chunk -> assert(chunk.size, 1) }
        }
    }

    @Test
    fun negativeOneTestShardsWithArmDevice() {
        val androidArgs = AndroidArgs.load(
            """
        gcloud:
          app: $appApk
          test: $testErrorApk
          device:
          - model: NexusLowRes
            version: 23
            locale: en
            orientation: portrait
          - model: SmallPhone.arm
            version: 30
            locale: en
            orientation: portrait

        flank:
          max-test-shards: -1
      """
        )

        val testShardChunks = getAndroidShardChunks(androidArgs)
        with(androidArgs) {
            assert(maxTestShards, AVAILABLE_VIRTUAL_ARM_SHARD_COUNT_RANGE.last)
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
        val androidArgs = AndroidArgs.load(yaml).validate()
        val testShardChunks = runBlocking { androidArgs.createAndroidTestContexts() }
        assertThat(testShardChunks).hasSize(0)
    }

    @Test
    fun `enable sharding allows using invalid apk`() {
        val yaml = """
        gcloud:
          app: $invalidApk
          test: $invalidApk
      """
        val androidArgs = AndroidArgs.load(yaml).validate()
        val testShardChunks = runBlocking { androidArgs.createAndroidTestContexts() }
        assertThat(testShardChunks).hasSize(0)
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
        assertThat(AndroidArgs.load(yaml).validate().appApk).isEqualTo(appApkAbsolutePath)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().testApk).isEqualTo(testApkAbsolutePath)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        assertThat(androidArgs.testApk).isEqualTo(appApkAbsolutePath)
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
        assertThat(AndroidArgs.load(yaml).validate().testTargets.size).isEqualTo(3)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().useOrchestrator).isFalse()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().useOrchestrator).isTrue()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().autoGoogleLogin).isFalse()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().autoGoogleLogin).isTrue()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().performanceMetrics).isFalse()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().performanceMetrics).isTrue()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        assertThat(androidArgs.performanceMetrics).isFalse()
    }

    @Test
    fun `cli numUniformShards`() {
        val expected = AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--num-uniform-shards=$expected")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).validate().numUniformShards).isNull()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        assertThat(androidArgs.numUniformShards).isEqualTo(expected)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if numUniformShards is specified along with maxTestShards`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          num-uniform-shards: ${AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}
        flank:
          max-test-shards: ${AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}
      """
        AndroidArgs.load(yaml).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().testRunnerClass).isNull()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().environmentVariables).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        val expectedMap = mapOf("a" to "1", "b" to "2")
        assertThat(androidArgs.environmentVariables).isEqualTo(expectedMap)
    }

    @Test
    fun `cli directoriesToPull`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--directories-to-pull=/sdcard/test,/data/local/tmp/test")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).validate().directoriesToPull).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        assertThat(androidArgs.directoriesToPull).isEqualTo(listOf("/sdcard/test", "/data/local/tmp/test"))
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw FlankFatalError when invalid cli directoriesToPull`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--directories-to-pull=a,b")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).validate().directoriesToPull).isEmpty()

        AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().filesToDownload).isEmpty()

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        assertThat(androidArgs.filesToDownload).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun `cli device`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--device=model=redfin,version=30,locale=zh_CN,orientation=default")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        val expectedDefaultDevice = Device(defaultAndroidModel, defaultAndroidVersion, isVirtual = true)
        val defaultDevices = AndroidArgs.load(yaml).validate().devices
        assertThat(defaultDevices.first()).isEqualTo(expectedDefaultDevice)
        assertThat(defaultDevices.size).isEqualTo(1)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        val expectedDevice = Device("redfin", "30", "zh_CN", "default", isVirtual = false)
        val actualDevices = androidArgs.devices
        assertThat(actualDevices.first()).isEqualTo(expectedDevice)
        assertThat(actualDevices.size).isEqualTo(1)
    }

    @Test
    fun `cli device repeat`() {
        val cli = AndroidRunCommand()
        val deviceCmd = "--device=model=redfin,version=30,locale=zh_CN,orientation=default"
        CommandLine(cli).parseArgs(deviceCmd, deviceCmd)

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        val expectedDevice = Device("redfin", "30", "zh_CN", "default", false)
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
        assertThat(AndroidArgs.load(yaml).validate().resultsBucket).isEqualTo("b")
        assertThat(AndroidArgs.load(yaml, cli).validate().resultsBucket).isEqualTo("a")
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
        assertThat(AndroidArgs.load(yaml).validate().recordVideo).isFalse()
        assertThat(AndroidArgs.load(yaml, cli).validate().recordVideo).isTrue()
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
        assertThat(AndroidArgs.load(yaml).validate().recordVideo).isTrue()
        assertThat(AndroidArgs.load(yaml, cli).validate().recordVideo).isFalse()
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
        assertThat(AndroidArgs.load(yaml).validate().testTimeout).isEqualTo("2m")
        assertThat(AndroidArgs.load(yaml, cli).validate().testTimeout).isEqualTo("1m")
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
        assertThat(AndroidArgs.load(yaml).validate().async).isEqualTo(false)
        assertThat(AndroidArgs.load(yaml, cli).validate().async).isEqualTo(true)
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
    fun `should parse cli project as lower case string`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--project=Upper-B")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          project: uPPer-a
      """
        assertThat(AndroidArgs.load(yaml).project).isEqualTo("upper-a")
        assertThat(AndroidArgs.load(yaml, cli).project).isEqualTo("upper-b")
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
        assertThat(AndroidArgs.load(yaml).validate().resultsHistoryName).isEqualTo("a")
        assertThat(AndroidArgs.load(yaml, cli).validate().resultsHistoryName).isEqualTo("b")
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
        assertThat(AndroidArgs.load(yaml).validate().maxTestShards).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).validate().maxTestShards).isEqualTo(3)
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
        assertThat(AndroidArgs.load(yaml).validate().shardTime).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).validate().shardTime).isEqualTo(3)
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
        assertThat(AndroidArgs.load(yaml).validate().disableSharding).isEqualTo(false)
        assertThat(AndroidArgs.load(yaml, cli).validate().disableSharding).isEqualTo(true)
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
        assertThat(AndroidArgs.load(yaml).validate().repeatTests).isEqualTo(2)
        assertThat(AndroidArgs.load(yaml, cli).validate().repeatTests).isEqualTo(3)
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
        assertThat(AndroidArgs.load(yaml, cli).validate().testTargetsAlwaysRun).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun `cli resultsDir`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--results-dir=build")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: results
      """

        assertThat(AndroidArgs.load(yaml).validate().resultsDir).isEqualTo("results")
        assertThat(AndroidArgs.load(yaml, cli).validate().resultsDir).isEqualTo("build")
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
        assertThat(AndroidArgs.load(yaml).validate().flakyTestAttempts).isEqualTo(0)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().smartFlankGcsPath).isEqualTo("")

        val args = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().localResultDir).isEqualTo("a")

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
        assertThat(AndroidArgs.load(yaml).validate().smartFlankDisableUpload).isEqualTo(false)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
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
            AndroidArgs.load(yaml).validate().additionalAppTestApks
        )

        assertEquals(
            listOf(AppTestPair(appApkAbsolutePath, testFlakyApkAbsolutePath)),
            AndroidArgs.load(yaml, cli).validate().additionalAppTestApks
        )
    }

    @Test
    fun `cli additional-app-test-apks with max-test-shards override`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--additional-app-test-apks=app=$appApk,test=$testFlakyApk,max-test-shards=4")

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
            AndroidArgs.load(yaml).validate().additionalAppTestApks
        )

        assertEquals(
            listOf(AppTestPair(appApkAbsolutePath, testFlakyApkAbsolutePath, maxTestShards = 4)),
            AndroidArgs.load(yaml, cli).validate().additionalAppTestApks
        )
    }

    @Test
    fun `cli additional-app-test-apks with parameterized-tests override`() {
        val cli = AndroidRunCommand()
        val text = "ignore-all"
        CommandLine(cli).parseArgs("--additional-app-test-apks=app=$appApk,test=$testFlakyApk,parameterized-tests=$text")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          additional-app-test-apks:
          - app: $appApk
            test: $testErrorApk
            parameterized-tests: default
      """

        assertEquals(
            listOf(AppTestPair(appApkAbsolutePath, testErrorApkAbsolutePath, parameterizedTests = "default")),
            AndroidArgs.load(yaml).validate().additionalAppTestApks
        )

        assertEquals(
            listOf(AppTestPair(appApkAbsolutePath, testFlakyApkAbsolutePath, parameterizedTests = "ignore-all")),
            AndroidArgs.load(yaml, cli).validate().additionalAppTestApks
        )
    }

    @Test
    fun `cli additional-app-test-apks with test-targets override`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--additional-app-test-apks=app=$appApk,test=$testFlakyApk,test-targets=class any.class.TestClass")

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
            AndroidArgs.load(yaml).validate().additionalAppTestApks
        )

        assertEquals(
            listOf(
                AppTestPair(
                    appApkAbsolutePath,
                    testFlakyApkAbsolutePath,
                    testTargets = listOf("class any.class.TestClass")
                )
            ),
            AndroidArgs.load(yaml, cli).validate().additionalAppTestApks
        )
    }

    @Test
    fun `additional-app-test-apks inherit top level client-details`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          client-details:
            top-key1: top-val1
        flank:
          additional-app-test-apks:
          - test: $testErrorApk
          - app: null
            test: $testErrorApk
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()

        val (matrixMap, chunks) = runBlocking { parsedYml.runAndroidTests() }
        assertTrue(
            "Not all matrices have the `top-key1` client-detail",
            matrixMap.map.all { it.value.clientDetails!!["top-key1"] != null }
        )
        assertEquals(3, matrixMap.map.size)
        assertEquals(3, chunks.size)
    }

    @Test
    fun `additional-app-test-apks can override top-level client-details`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          client-details:
            top-key1: top-val1
        flank:
          additional-app-test-apks:
          - test: $testErrorApk
            client-details:
                top-key1: overridden-top-val1
                key22: val22
          - app: null
            test: $testErrorApk
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()

        val (matrixMap, chunks) = runBlocking { parsedYml.runAndroidTests() }
        assertTrue(
            "Not all matrices have the `top-key1` client-detail",
            matrixMap.map.all { it.value.clientDetails!!.containsKey("top-key1") }
        )

        assertNotNull(
            "Matrix did not override `top-key1` client-detail",
            matrixMap.map.toList().firstOrNull {
                it.second.clientDetails!!["top-key1"] == "overridden-top-val1"
            }
        )

        assertEquals(3, matrixMap.map.size)
        assertEquals(3, chunks.size)
    }

    @Test
    fun `additional-app-test-apks should pick up client-details`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          client-details:
            top-key1: top-val1
        flank:
          additional-app-test-apks:
          - test: $testErrorApk
            client-details:
                key1: val1
                key2: val2
                top-key1: overwritten-top-val1
          - app: null
            test: $testErrorApk
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()

        val (matrixMap, chunks) = runBlocking { parsedYml.runAndroidTests() }
        assertTrue(
            "Not all matrices have the `top-key1` client-detail",
            matrixMap.map.all { it.value.clientDetails!!.containsKey("top-key1") }
        )
        matrixMap.map
            .toList().apply {
                // test the module which overrides and adds client details
                first { it.second.clientDetails!!.size == 5 }
                    .apply {
                        assertEquals("val1", second.clientDetails!!["key1"])
                        assertEquals("val2", second.clientDetails!!["key2"])
                        assertEquals("overwritten-top-val1", second.clientDetails!!["top-key1"])
                    }
                // test all other modules got top level client details
                first { it.second.clientDetails!!.size == 3 }
                    .apply {
                        assertEquals("top-val1", second.clientDetails!!["top-key1"])
                    }
                last { it.second.clientDetails!!.size == 3 }
                    .apply {
                        assertEquals("top-val1", second.clientDetails!!["top-key1"])
                    }
            }
        assertEquals(3, matrixMap.map.size)
        assertEquals(3, chunks.size)
    }

    @Test
    fun `should add flank version to client-details even if client-details is empty`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          additional-app-test-apks:
          - test: $testErrorApk
          - app: null
            test: $testErrorApk
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()

        val (matrixMap) = runBlocking { parsedYml.runAndroidTests() }
        assertTrue(
            "Not all matrices have the `Flank Version` client-detail",
            matrixMap.map.all { it.value.clientDetails!!.containsKey("Flank Version") && it.value.clientDetails!!.containsKey("Flank Revision") }
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
        assertThat(AndroidArgs.load(yaml).validate().keepFilePath).isEqualTo(false)

        val androidArgs = AndroidArgs.load(yaml, cli).validate()
        assertThat(androidArgs.keepFilePath).isEqualTo(true)
    }

    @Test
    fun `cli run-timeout`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--run-timeout=20m")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).validate().parsedTimeout).isEqualTo(Long.MAX_VALUE)

        val args = AndroidArgs.load(yaml, cli).validate()
        assertThat(args.parsedTimeout).isEqualTo(20 * 60 * 1000L)
    }

    @Test
    fun `cli output-style`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--output-style=multi")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).validate().outputStyle).isEqualTo(OutputStyle.Verbose)

        val args = AndroidArgs.load(yaml, cli).validate()
        assertThat(args.outputStyle).isEqualTo(OutputStyle.Multi)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `cli output-style fail on parse`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          output-style: unknown
      """
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `cli fail-fast`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--fail-fast")

        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
      """
        assertThat(AndroidArgs.load(yaml).validate().failFast).isEqualTo(false)

        val args = AndroidArgs.load(yaml, cli).validate()
        assertThat(args.failFast).isEqualTo(true)
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

        val parsedYml = AndroidArgs.load(yaml).validate()
        val (matrixMap, chunks) = runBlocking { parsedYml.runAndroidTests() }
        assertEquals(4, matrixMap.map.size)
        assertEquals(4, chunks.size)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should fail on missing app apk -- yml file`() {
        val yaml = """
        flank:
          additional-app-test-apks:
          - app: $appApk
            test: $testApk
          - app: null
            test: $testErrorApk
        """.trimIndent()

        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should omit global app apk if any additional-app-test-apks specified -- yml file`() {
        val yaml = """
        gcloud:
        flank:
          additional-app-test-apks:
          - app: $appApk
            test: $testApk
        """.trimIndent()

        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `verify run timeout default value - android`() {
        val args = AndroidArgs.load(simpleFlankPath)
        assertEquals(Long.MAX_VALUE, args.parsedTimeout)
    }

    @Test
    fun `verify legacy junit result default value - android`() {
        val args = AndroidArgs.load(flankLocal)
        assertFalse(args.useLegacyJUnitResult)
    }

    @Test
    fun `verify ignore failed tests default value - android`() {
        val args = AndroidArgs.load(flankLocal)
        assertFalse(args.ignoreFailedTests)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw if both instrumentation and robo tests are specified`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          robo-directives:
            text:resource_name_1: some_text
            click:resource_name_2: ""
          robo-script: $appApk
        """.trimIndent()

        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should load robo-script from yaml`() {
        val yaml = """
        gcloud:
          app: $appApk
          robo-script: $appApk
        """.trimIndent()

        val args = AndroidArgs.load(yaml).validate()

        assertEquals(
            appApkAbsolutePath,
            args.roboScript
        )
    }

    @Test
    fun `should load robo-directives from yaml`() {
        val yaml = """
        gcloud:
          app: $appApk
          robo-directives:
            text:resource_name_1: some_text
            click:resource_name_2: ""
        """.trimIndent()

        val args = AndroidArgs.load(yaml).validate()

        assertEquals(
            args.roboDirectives,
            listOf(
                FlankRoboDirective(type = "text", name = "resource_name_1", input = "some_text"),
                FlankRoboDirective(type = "click", name = "resource_name_2")
            )
        )
    }

    fun `should load robo-script & robo directives from cli`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--robo-script=$appApk, --robo-directives=text:a=b,click=c")

        val yaml = """
        gcloud:
          app: $appApk
        """.trimIndent()

        val args = AndroidArgs.load(yaml, cli).validate()

        assertEquals(
            args.roboScript,
            appApkAbsolutePath
        )
        assertEquals(
            args.roboDirectives,
            listOf(
                FlankRoboDirective(type = "text", name = "a", input = "b"),
                FlankRoboDirective(type = "click", name = "c")
            )
        )
    }

    @Test(expected = FlankNoTestsError::class)
    fun `should throw FlankNoTestsError if not tests to be run overall`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        """.trimIndent()

        mockkStatic("ftl.run.platform.android.CreateAndroidTestContextKt")
        every { runBlocking { any<AndroidArgs>().createAndroidTestContexts() } } returns listOf()

        val parsedYml = AndroidArgs.load(yaml).validate()
        runBlocking { parsedYml.runAndroidTests() }
    }

    @Test
    fun `should only keep @LargeTest`() {
        val expectedTests = setOf(
            "LargeParameterizedTests",
            "ExampleInstrumentedTest#useAppContextLarge",
            "LargeTestClass#testLargeClass"
        )

        val yaml = """
        gcloud:
          app: $appApk
          test: $testLargeParameterizedApk
          test-targets:
            - annotation androidx.test.filters.LargeTest
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()
        val chunks = runBlocking { parsedYml.runAndroidTests() }.shardChunks
        chunks[0]
            .apply { assertTrue(isNotEmpty()) }
            .map { it.substringAfterLast(".") }
            .forEach {
                assertTrue(it in expectedTests)
            }
    }

    @Test
    fun `should keep no @LargeTest`() {
        val expectedTests = setOf(
            "LargeParameterizedTests",
            "ExampleInstrumentedTest#useAppContextLarge",
            "LargeTestClass#testLargeClass"
        )

        val yaml = """
        gcloud:
          app: $appApk
          test: $testLargeParameterizedApk
          test-targets:
            - notAnnotation androidx.test.filters.LargeTest
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()
        val chunks = runBlocking { parsedYml.runAndroidTests() }.shardChunks
        chunks[0]
            .apply { assertTrue(isNotEmpty()) }
            .map { it.substringAfterLast(".") }
            .forEach {
                assertTrue(it !in expectedTests)
            }
    }

    @Test
    fun `results-dir (cloud directory) should not throw if it doesn't exist locally`() {
        val resultsDir = UUID.randomUUID().toString()
        val directoryPath = Paths.get(resultsDir)
        if (Files.exists(directoryPath)) {
            fail("Test directory ($resultsDir) shouldn't exists! It's a remote directory.")
        }
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: $resultsDir
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
        if (Files.exists(directoryPath)) {
            fail("Test directory ($resultsDir) shouldn't be created! It's a remote directory on the cloud!")
        }
    }

    @Test
    fun `default output should be multi`() {
        listOf(
            "  num-flaky-test-attempts: 3",
            """
            flank:
              max-test-shards: ${AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last}
            """.trimIndent(),
            """
            flank:
              additional-app-test-apks:
                - app: ../test_projects/android/apks/app-debug.apk
                  test: ../test_projects/android/apks/app1-debug-androidTest.apk
            """.trimIndent()
        ).map { inject ->
            """
            gcloud:
              app: $appApk
              test: $testApk
            $inject
            """.trimIndent()
        }.forEach { yaml ->
            assertEquals(
                OutputStyle.Multi,
                AndroidArgs.load(yaml).validate().defaultOutputStyle
            )
        }
    }

    @Test
    fun `should return AndroidTestConfig without testTargets`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          use-orchestrator: false
        flank:
          disable-sharding: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        val androidTestConfig = args.createAndroidTestMatrixType(
            InstrumentationTestContext(
                app = "app".asFileReference(),
                test = "test".asFileReference(),
                shards = listOf(
                    Chunk(listOf(TestMethod(name = "test", time = 0.0))),
                    Chunk(listOf(TestMethod(name = "test", time = 0.0)))
                ),
                args = args
            )
        )
        val testSpecification = TestSpecification().setupAndroidTest(androidTestConfig)
        assertTrue(testSpecification.androidInstrumentationTest.testTargets.isEmpty())
    }

    @Test
    fun `should return AndroidTestConfig with testTargets`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          use-orchestrator: false
          test-targets:
          - EarlGreyExampleSwiftTests/testWith.*${'$'}
        flank:
          disable-sharding: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        val androidTestConfig = args.createAndroidTestMatrixType(
            InstrumentationTestContext(
                app = "app".asFileReference(),
                test = "test".asFileReference(),
                shards = listOf(
                    Chunk(listOf(TestMethod(name = "test", time = 0.0))),
                    Chunk(listOf(TestMethod(name = "test", time = 0.0)))
                ),
                args = args
            )
        )
        val testSpecification = TestSpecification().setupAndroidTest(androidTestConfig)
        assertTrue(testSpecification.androidInstrumentationTest.testTargets.isNotEmpty())
    }

    // This and the "should limit shards to virtual limit if only virtual device configured"
    // test seem to basically be testing the same behavior as well as the
    // negativeOneTestShards test?
    @Test
    fun `if set max-test-shards to -1 should give maximum amount`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last, args.maxTestShards)
    }

    @Test
    fun `should set disableResultsUpload to true`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
          disable-results-upload: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertTrue(args.disableResultsUpload)
    }

    @Test
    fun `should disableResultsUpload set to false`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertFalse(args.disableResultsUpload)
    }

    @Test
    fun `should limit shards to virtual limit if only virtual device configured`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
          disable-results-upload: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last, args.maxTestShards)
    }

    @Test
    fun `should limit shards to Arm limit if any Arm device and no physical device configured `() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: SmallPhone.arm
            version: 30
            locale: en
            orientation: portrait
        flank:
          max-test-shards: -1
          disable-results-upload: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(AVAILABLE_VIRTUAL_ARM_SHARD_COUNT_RANGE.last, args.maxTestShards)
    }

    @Test
    fun `should not set shards count when maxTestShards != -1`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: 10
          disable-results-upload: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(10, args.maxTestShards)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw when maximum test shards for virtual devices limit exceeded`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: ${AVAILABLE_VIRTUAL_SHARD_COUNT_RANGE.last + 1}
          disable-results-upload: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw when maximum test shards for Arm devices limit exceeded`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: SmallPhone.arm
            version: 30
            locale: en
            orientation: portrait
        flank:
          max-test-shards: ${AVAILABLE_VIRTUAL_ARM_SHARD_COUNT_RANGE.last + 1}
          disable-results-upload: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw when maximum test shards for Arm devices limit exceeded and non-Arm devices configured`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
          - model: SmallPhone.arm
            version: 30
            locale: en
            orientation: portrait
          device:
          - model: NexusLowRes
            version: 23
            locale: en
            orientation: portrait
        flank:
          max-test-shards: ${AVAILABLE_VIRTUAL_ARM_SHARD_COUNT_RANGE.last + 1}
          disable-results-upload: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw an error if validation is disabled (yml) -- max test shards`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: ${Int.MAX_VALUE}
          skip-config-validation: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw an error if validation is disabled (command line) -- device`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: funnyDevice
              version: 28
              locale: en
              orientation: portrait
        flank:
          skip-config-validation: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw when maximum test shards for physical devices limit exceeded`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait
        flank:
          max-test-shards: ${AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last + 1}
          disable-results-upload: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should limit shards to physical if only physical device configured`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait
        flank:
          max-test-shards: -1
          disable-results-upload: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last, args.maxTestShards)
    }

    @Test
    fun `should limit shards to physical if physical and virtual device configured`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
        flank:
          max-test-shards: -1
          disable-results-upload: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last, args.maxTestShards)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw when --legacy-junit-result and --full-junit-result set`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
        flank:
          max-test-shards: -1
          full-junit-result: true
          legacy-junit-result: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw when --legacy-junit-result set and --full-junit-result not set`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
        flank:
          max-test-shards: -1
          full-junit-result: false
          legacy-junit-result: true
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw when --legacy-junit-result not set and --full-junit-result set`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
        flank:
          max-test-shards: -1
          full-junit-result: true
          legacy-junit-result: false
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `proceed on correct orientation`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
            - model: Nexus6
              version: 25
              locale: en
              orientation: default
            - model: Nexus6
              version: 25
              locale: en
              orientation: landscape
        flank:
          max-test-shards: -1
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankGeneralError::class)
    fun `fail fast on orientation misspell`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: blueline
              version: 28
              locale: en
              orientation: portrait_
        flank:
          max-test-shards: -1
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should set defaultTestTime`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
          default-test-time: 15
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(args.defaultTestTime, 15.0, 0.01)
    }

    @Test
    fun `should set defaultTestTime to default value if not specified`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
          use-average-test-time-for-new-tests: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertEquals(args.defaultTestTime, 120.0, 0.01)
    }

    @Test
    fun `should useAverageTestTimeForNewTests set to true`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
          use-average-test-time-for-new-tests: true
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertTrue(args.useAverageTestTimeForNewTests)
    }

    @Test
    fun `should useAverageTestTimeForNewTests set to false by default`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
        flank:
          max-test-shards: -1
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        assertFalse(args.useAverageTestTimeForNewTests)
    }

    @Test
    fun `should not throw when other files exists`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          other-files:
            /sdcard/dir1/file1.txt: $appApk
            /sdcard/dir2/file2.jpg: $testApk
        flank:
          max-test-shards: -1
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankGeneralError::class)
    fun `should throw when other files don't exist`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          other-files:
            /sdcard/dir1/file1.txt: $nonExistingApk
            /sdcard/dir2/file2.jpg: $nonExistingApk
        flank:
          max-test-shards: -1
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should show warning message when bucket exist and legacy junit result used`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: test
        flank:
          max-test-shards: -1
          legacy-junit-result: true
        """.trimIndent()
        mockkObject(GcStorage) {
            every { exist(any(), any()) } returns true

            // when
            AndroidArgs.load(yaml).validate()

            // then
            assertTrue(systemOutRule.log.contains("WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled"))
        }
    }

    @Test
    fun `should not print Google cloud storage warning when legacy junit results is false`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: test
        flank:
          max-test-shards: -1
          legacy-junit-result: false
        """.trimIndent()

        // when
        AndroidArgs.load(yaml).validate()

        // then
        assertFalse(systemOutRule.log.contains("WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled"))
    }

    @Test
    fun `should not print result-dir warning when same directory does not exist`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          results-dir: test
        flank:
          max-test-shards: -1
          legacy-junit-result: true
        """.trimIndent()
        mockkObject(GcStorage) {
            every { exist(any(), any()) } returns false

            // when
            AndroidArgs.load(yaml).validate()

            // then
            assertFalse(systemOutRule.log.contains("WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled"))
        }
    }

    @Test(expected = FlankGeneralError::class)
    fun `should throw exception if incorrect permission requested`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          grant-permissions: error
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if correct permission requested All`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          grant-permissions: all
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if correct permission requested None`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          grant-permissions: none
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankGeneralError::class)
    fun `should throw exception if incorrect type requested`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          grant-permissions: error
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if correct type requested game-loop`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if correct type requested instrumental`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: instrumentation
          test-runner-class: com.foo.TestRunner
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if correct type requested robo`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: robo
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if incorrect type requested and scenario labels provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: robo
          scenario-labels:
            - test1
            - test2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception if game-loop and scenario labels provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          scenario-labels:
            - test1
            - test2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception if game-loop and scenario numbers provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          scenario-numbers:
            - 1
            - 2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop not provided and scenario numbers provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: robo
          scenario-numbers:
            - 1
            - 2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if invalid scenario numbers provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          scenario-numbers:
            - error
            - 2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if valid scenario numbers provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          scenario-numbers:
            - 1
            - 2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should Not throw exception if valid scenario numbers provided and scenario labels`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          scenario-labels:
            - label1
            - label2
          scenario-numbers:
            - 1
            - 2
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should return Gameloop test with correct scenario numbers and labels`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          scenario-labels:
            - label1
            - label2
          scenario-numbers:
            - 1
            - 2
        """.trimIndent()
        val args = AndroidArgs.load(yaml).validate()
        val androidTestConfig = args.createAndroidTestMatrixType(
            GameLoopContext(
                app = "app".asFileReference(),
                scenarioNumbers = args.scenarioNumbers,
                scenarioLabels = args.scenarioLabels,
                args = args
            )
        )
        val testSpecification = TestSpecification().setupAndroidTest(androidTestConfig)
        assertTrue(testSpecification.androidTestLoop.scenarioLabels == args.scenarioLabels)
        assertTrue(testSpecification.androidTestLoop.scenarios == args.scenarioNumbers.map { it.toInt() })
    }

    @Test
    fun `should not throw exception if game-loop is provided and nothing else`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop is provided and valid obb files provided but no obb file names`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          obb-files:
            - $obbFile
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception if game-loop is provided and valid obb files provided and obb file names`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          obb-files:
            - $obbFile
          obb-names:
            - com.test.obb
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop is provided but no obb files provided but valid obb file names`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          obb-names:
            - com.test.obb
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankGeneralError::class)
    fun `should throw exception if game-loop is provided and invalid obb file provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          obb-files:
            - error
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop is not provided and valid obb file provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: robo
          obb-files:
            - $obbFile
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop is provided and more than 2 obb files provided`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: game-loop
          obb-files:
            - $obbFile
            - $obbFile
            - $obbFile
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should throw exception if incorrect (empty) value used in yml config file`() {
        assertThrowsWithMessage(
            clazz = FlankGeneralError::class,
            message = "Invalid value for [test-targets]: no argument value found"
        ) {
            AndroidArgs.load(
                """
            gcloud:
              app: any/path.apk
              test-targets:
                """.trimIndent()
            )
        }
    }

    @Test
    fun `should not throw exception if incorrect (empty) value used in yml config file is overwritten with command line`() {
        val cli = AndroidRunCommand()
        CommandLine(cli).parseArgs("--legacy-junit-result")

        AndroidArgs.load(
            """
            gcloud:
              app: any/path.apk
            flank:
              legacy-junit-result:
            """.trimIndent(),
            cli
        )
    }

    @Test
    fun `should not throw exception if test targets for shard & type is defined correctly (instrumentation)`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus6
              version: 25
              locale: en
              orientation: portrait
          type: instrumentation
          test-runner-class: com.foo.TestRunner
          test-targets-for-shard:
            - com.example.test
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should return correct message if version is not supported for device`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testApk
          device:
            - model: Nexus7
              version: 28
        """.trimIndent()
        val errorMessage = getThrowable { AndroidArgs.load(yaml).validate() }.message ?: ""
        val expectedMessage = """
            Incompatible model, 'Nexus7', and version, '28'
            Supported version ids for 'Nexus7': 21, 22
        """.trimIndent()
        assertEquals(expectedMessage, errorMessage)
    }

    @Test
    fun `should throw exception if parameterized test is incorrect`() {
        val yaml = """
        gcloud:
            # Android gcloud
            app: $appApk
            test: $testApk
            parameterized-tests: error
        """.trimIndent()
        val errorMessage = getThrowable { AndroidArgs.load(yaml).validate() }.message ?: ""
        val expectedMessage = """
            Parameterized test flag must be one of the following: `default`, `ignore-all`, `shard-into-single`, leaving it blank will result in `default` sharding.
        """.trimIndent()
        assertEquals(expectedMessage, errorMessage)
    }

    @Test
    fun `should throw NOT throw an exception if parameterized test is left to be blank`() {
        val yaml = """
        gcloud:
            # Android gcloud
            app: $appApk
            test: $testApk
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should throw NOT throw an exception if parameterized test is ignore-all`() {
        val yaml = """
        gcloud:
            # Android gcloud
            app: $appApk
            test: $testApk
            parameterized-tests: ignore-all
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test
    fun `should throw NOT throw an exception if parameterized test is shard-into-single`() {
        val yaml = """
        gcloud:
            # Android gcloud
            app: $appApk
            test: $testApk
            parameterized-tests: shard-into-single
        """.trimIndent()
        AndroidArgs.load(yaml).validate()
    }

    @Test(expected = FlankNoTestsError::class)
    fun `should throw exception as there are no tests to run - ignore-all parameterized tests`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testExtremeParameterizedApk
          parameterized-tests: ignore-all
        """.trimIndent()
        val parsedYml = AndroidArgs.load(yaml).validate()
        runBlocking { parsedYml.runAndroidTests() }
    }

    @Test
    fun `should NOT throw exception as there are no tests to run - default parameterized tests`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testExtremeParameterizedApk
          parameterized-tests: default
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()
        val chunks = runBlocking { parsedYml.runAndroidTests() }.shardChunks
        assertTrue(chunks.size == 1)
    }

    @Test
    fun `should shard tests correctly into a single shard with shard-into-single used`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testExtremeParameterizedOtherApk
          parameterized-tests: shard-into-single
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()
        val chunks = runBlocking { parsedYml.runAndroidTests() }.shardChunks
        assertTrue(chunks.size == 2)
    }

    @Test
    fun `should shard tests normally when default used`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testExtremeParameterizedOtherApk
          parameterized-tests: default
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()
        val chunks = runBlocking { parsedYml.runAndroidTests() }.shardChunks
        assertTrue(chunks.size == 1)
    }

    @Test
    fun `should shard tests into multiple new shards with shard-into-multiple`() {
        val yaml = """
        gcloud:
          app: $appApk
          test: $testExtremeParameterizedOtherApk
          parameterized-tests: shard-into-multiple
        """.trimIndent()

        val parsedYml = AndroidArgs.load(yaml).validate()
        val chunks = runBlocking { parsedYml.runAndroidTests() }.shardChunks
        assertTrue(chunks.size == 5)
    }
}

private fun AndroidArgs.Companion.load(
    yamlData: String,
    cli: AndroidRunCommand? = null
): AndroidArgs =
    load(StringReader(yamlData), cli)

fun getAndroidShardChunks(args: AndroidArgs): List<Chunk> =
    runBlocking { (args.createAndroidTestContexts().first() as InstrumentationTestContext).shards }
