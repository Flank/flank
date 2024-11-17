package ftl.args

import com.google.common.truth.Truth.assertThat
import flank.common.isWindows
import ftl.client.google.GcStorage
import ftl.config.Device
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.config.defaultIosConfig
import ftl.ios.xctest.flattenShardChunks
import ftl.presentation.cli.firebase.test.ios.IosRunCommand
import ftl.run.exception.FlankConfigurationError
import ftl.run.platform.runIosTests
import ftl.run.status.OutputStyle
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import ftl.test.util.assertThrowsWithMessage
import io.mockk.every
import io.mockk.mockkObject
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assume
import org.junit.Assume.assumeFalse
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.contrib.java.lang.system.SystemOutRule
import org.junit.runner.RunWith
import picocli.CommandLine
import java.io.StringReader

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class IosArgsTest {
    private val empty = emptyList<String>()
    private val simpleFlankPath = getPath("src/test/kotlin/ftl/fixtures/simple-ios-flank.yml")
    private val testPath = "./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExample.zip"
    private val nonExistingTestPath = "./src/test/kotlin/ftl/fixtures/tmp/earlgrey_example_non_existing.zip"
    private val nonExistingxctestrunFile = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e_non_exis.xctestrun"
    private val xctestrunFile =
        "./src/test/kotlin/ftl/fixtures/tmp/ios/EarlGreyExample/EarlGreyExampleSwiftTests.xctestrun"
    private val invalidApp = "../test_projects/android/apks/invalid.apk"
    private val xctestrunFileAbsolutePath = xctestrunFile.absolutePath()
    private val testPlansPath = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/FlankTestPlansExample.zip"
    private val testPlansXctestrun = "./src/test/kotlin/ftl/fixtures/tmp/ios/FlankTestPlansExample/AllTests.xctestrun"
    private val testAbsolutePath = testPath.absolutePath()
    private val testIpa1 = "./src/test/kotlin/ftl/fixtures/tmp/test.ipa"
    private val testIpa2 = "./src/test/kotlin/ftl/fixtures/tmp/test2.ipa"
    private val resultDir = "test_dir"
    private val iosNonDefault = """
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
          results-history-name: ios-history
          other-files:
            com.my.app:/Documents/file.txt: local/file.txt
            /private/var/mobile/Media/file.jpg: gs://bucket/file.jpg
          additional-ipas:
            - $testIpa1
            - $testIpa2
          test: $testPath
          xctestrun-file: $xctestrunFile
          xcode-version: 9.2
          device:
          - model: iphone13pro
            version: 15.7
            locale: c
            orientation: default
          - model: iphone13pro
            version: 15.7
            locale: c
            orientation: default
          num-flaky-test-attempts: 4
          type: xctest
          test-special-entitlements: true

        flank:
          max-test-shards: 7
          shard-time: 60
          num-test-runs: 8
          default-test-time: 15.0
          use-average-test-time-for-new-tests: true
          files-to-download:
            - /sdcard/screenshots
          test-targets-always-run:
            - a/testGrantPermissions
            - a/testGrantPermissions2
          test-targets:
            - b/testBasicSelection
            - b/testBasicSelection2
          disable-sharding: true
          run-timeout: 15m
          full-junit-result: true
          ignore-failed-tests: true
          keep-file-path: true
          output-style: single
          disable-results-upload: true
          default-class-test-time: 30.0
          only-test-configuration: pl
          output-report: json
        """

    @get:Rule
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()

    @Rule
    @JvmField
    val systemOutRule: SystemOutRule = SystemOutRule().enableLog()

    @Test
    fun `empty testTargets`() {
        val emptyTestTargets = """
gcloud:
  test: "./src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip"
  xctestrun-file: "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun"
flank:
  test-targets:
  -

        """.trimIndent()

        val args = IosArgs.load(emptyTestTargets)
        assertThat(args.testTargets.size).isEqualTo(0)
    }

    @Test
    fun `args invalidDeviceExits`() {
        assertThrowsWithMessage(Throwable::class, "iOS 1.0 on iphone13pro is not supported\nSupported version ids for 'iphone13pro': 15.2, 15.7") {
            val invalidDevice = mutableListOf(Device("iphone13pro", "1.0"))
            createIosArgs(
                config = defaultIosConfig().apply {
                    common.gcloud.devices = invalidDevice
                    platform.gcloud.also {
                        it.test = testPath
                        it.xctestrunFile = xctestrunFile
                    }
                }
            ).validate()
        }
    }

    @Test
    fun `args invalidXcodeExits`() {
        assertThrowsWithMessage(Throwable::class, "Xcode 99.9 is not a supported Xcode version") {
            createIosArgs(
                config = defaultIosConfig().apply {
                    platform.gcloud.also {
                        it.test = testPath
                        it.xctestrunFile = xctestrunFile
                        it.xcodeVersion = "99.9"
                    }
                }
            ).validate()
        }
    }

    @Test
    fun args() {
        val args = IosArgs.load(iosNonDefault)

        with(args) {
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
            assert(resultsHistoryName ?: "", "ios-history")

            // IosGcloudYml
            assert(xctestrunZip, testAbsolutePath)
            assert(xctestrunFile, xctestrunFileAbsolutePath)
            val device = Device("iphone13pro", "15.7", "c", "default")
            assert(xcodeVersion ?: "", "9.2")
            assert(devices, listOf(device, device))

            // FlankYml
            assert(maxTestShards, 7)
            assert(shardTime, 60)
            assert(repeatTests, 8)
            assert(testTargetsAlwaysRun, listOf("a/testGrantPermissions", "a/testGrantPermissions2"))

            // IosFlankYml
            assert(testTargets, listOf("b/testBasicSelection", "b/testBasicSelection2"))

            assert(flakyTestAttempts, 4)
            assert(disableSharding, true)
            assert(runTimeout, "15m")
            assert(useLegacyJUnitResult, true)
            assert(fullJUnitResult, true)
            assert(outputStyle, OutputStyle.Single)
        }
    }

    @Test
    fun `args toString`() {
        val args = IosArgs.load(iosNonDefault)
        assert(
            args.toString(),
            """
IosArgs
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
      results-history-name: ios-history
      # iOS gcloud
      test: $testAbsolutePath
      xctestrun-file: $xctestrunFileAbsolutePath
      xcode-version: 9.2
      device:
        - model: iphone13pro
          version: 15.7
          locale: c
          orientation: default
        - model: iphone13pro
          version: 15.7
          locale: c
          orientation: default
      num-flaky-test-attempts: 4
      directories-to-pull:
      other-files:
        com.my.app:/Documents/file.txt: local/file.txt
        /private/var/mobile/Media/file.jpg: gs://bucket/file.jpg
      additional-ipas:
        - $testIpa1
        - $testIpa2
      scenario-numbers:
      type: xctest
      app: 
      test-special-entitlements: true
      fail-fast: false

    flank:
      max-test-shards: 7
      shard-time: 60
      num-test-runs: 8
      smart-flank-gcs-path:${' '}
      smart-flank-disable-upload: false
      default-test-time: 15.0
      use-average-test-time-for-new-tests: true
      test-targets-always-run:
        - a/testGrantPermissions
        - a/testGrantPermissions2
      files-to-download:
        - /sdcard/screenshots
      keep-file-path: true
      full-junit-result: true
      # iOS flank
      test-targets:
        - b/testBasicSelection
        - b/testBasicSelection2
      disable-sharding: true
      project: project-foo
      local-result-dir: results
      run-timeout: 15m
      ignore-failed-tests: true
      output-style: single
      disable-results-upload: true
      default-class-test-time: 30.0
      disable-usage-statistics: false
      only-test-configuration: pl
      skip-test-configuration: 
      output-report: json
      skip-config-validation: false
      custom-sharding-json: 
      ignore-global-tests: true
            """.trimIndent()
        )
    }

    @Test
    fun `verify default yml toString`() {
        val args = IosArgs.load(simpleFlankPath)
        assertEquals(
            """
IosArgs
    gcloud:
      results-bucket: mockBucket
      results-dir: $resultDir
      record-video: false
      timeout: 15m
      async: false
      client-details:
      network-profile: null
      results-history-name: null
      # iOS gcloud
      test: $testAbsolutePath
      xctestrun-file: $xctestrunFileAbsolutePath
      xcode-version: null
      device:
        - model: iphone13pro
          version: 15.7
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0
      directories-to-pull:
      other-files:
      additional-ipas:
      scenario-numbers:
      type: xctest
      app: 
      test-special-entitlements: false
      fail-fast: false

    flank:
      max-test-shards: 1
      shard-time: -1
      num-test-runs: 1
      smart-flank-gcs-path: 
      smart-flank-disable-upload: false
      default-test-time: 120.0
      use-average-test-time-for-new-tests: false
      test-targets-always-run:
      files-to-download:
      keep-file-path: false
      full-junit-result: false
      # iOS flank
      test-targets:
      disable-sharding: false
      project: mock-project-id
      local-result-dir: results
      run-timeout: -1
      ignore-failed-tests: false
      output-style: verbose
      disable-results-upload: false
      default-class-test-time: 240.0
      disable-usage-statistics: false
      only-test-configuration: 
      skip-test-configuration: 
      output-report: none
      skip-config-validation: false
      custom-sharding-json: 
      ignore-global-tests: true
            """.trimIndent(),
            args.toString()
        )
    }

    @Test
    fun argsDefault() {
        val args = IosArgs.load(
            """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
        """
        )

        with(args) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, false)
            assert(testTimeout, "15m")
            assert(async, false)
            assert(project, "mock-project-id")
            assert(clientDetails, null)
            assert(networkProfile, null)

            // IosGcloudYml
            assert(xctestrunZip, testAbsolutePath)
            assert(xctestrunFile, xctestrunFileAbsolutePath)
            assert(devices, listOf(Device("iphone13pro", "15.7")))
            assert(flakyTestAttempts, 0)

            // FlankYml
            assert(maxTestShards, 1)
            assert(shardTime, -1)
            assert(repeatTests, 1)
            assert(testTargetsAlwaysRun, emptyList<String>())
            assert(filesToDownload, emptyList<String>())
            assert(disableSharding, false)

            // IosFlankYml
            assert(testTargets, empty)
            assert(runTimeout, "-1")
            assert(onlyTestConfiguration, "")
            assert(skipTestConfiguration, "")
        }
    }

    @Test
    fun negativeOneTestShards() {
        Assume.assumeFalse(isWindows)

        val args = IosArgs.load(
            """
    gcloud:
      test: $testPath
      xctestrun-file: $xctestrunFile

    flank:
      max-test-shards: -1
"""
        )

        with(args) {
            val testShardChunks = xcTestRunData.flattenShardChunks()

            assert(maxTestShards, IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last)
            assert(testShardChunks.size, 17)
            testShardChunks.forEach { chunk -> assert(chunk.size, 1) }
        }
    }

    @Test
    fun `args emptyFlank`() {
        val args = IosArgs.load(
            """
    gcloud:
      test: $testPath
      xctestrun-file: $xctestrunFile

    flank:
"""
        )

        with(args) {
            assertThat(xctestrunZip).isEqualTo(testAbsolutePath)
            assertThat(xctestrunFile).isEqualTo(xctestrunFileAbsolutePath)
        }
    }

    @Test
    fun `disableSharding allows using invalid app`() {
        val yaml = """
        gcloud:
          test: $invalidApp
          xctestrun-file: $invalidApp
        flank:
          disable-sharding: true
      """
        IosArgs.load(yaml)
    }

    @Test(expected = RuntimeException::class)
    fun `Invalid app throws`() {
        val yaml = """
        gcloud:
          test: $invalidApp
          xctestrun-file: $invalidApp
        flank:
          disable-sharding: false
      """
        IosArgs.load(yaml).xcTestRunData
    }

    // gcloudYml

    @Test
    fun `cli resultsBucket`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--results-bucket=a")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          results-bucket: b
      """
        assertThat(IosArgs.load(yaml).resultsBucket).isEqualTo("b")
        assertThat(IosArgs.load(yaml, cli).resultsBucket).isEqualTo("a")
    }

    @Test
    fun `cli recordVideo`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--record-video")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          record-video: false
      """
        assertThat(IosArgs.load(yaml).recordVideo).isFalse()
        assertThat(IosArgs.load(yaml, cli).recordVideo).isTrue()
    }

    @Test
    fun `cli noRecordVideo`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--no-record-video")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          record-video: true
      """
        assertThat(IosArgs.load(yaml).recordVideo).isTrue()
        assertThat(IosArgs.load(yaml, cli).recordVideo).isFalse()
    }

    @Test
    fun `cli timeout`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--timeout=1m")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          timeout: 2m
      """
        assertThat(IosArgs.load(yaml).testTimeout).isEqualTo("2m")
        assertThat(IosArgs.load(yaml, cli).testTimeout).isEqualTo("1m")
    }

    @Test
    fun `cli async`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--async")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          async: false
      """
        assertThat(IosArgs.load(yaml).async).isEqualTo(false)
        assertThat(IosArgs.load(yaml, cli).async).isEqualTo(true)
    }

    @Test
    fun `cli project`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--project=b")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          project: a
      """
        assertThat(IosArgs.load(yaml).project).isEqualTo("a")
        assertThat(IosArgs.load(yaml, cli).project).isEqualTo("b")
    }

    @Test
    fun `should parse cli project as lower case string`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--project=Upper-B")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          project: uPPer-a
      """
        assertThat(IosArgs.load(yaml).project).isEqualTo("upper-a")
        assertThat(IosArgs.load(yaml, cli).project).isEqualTo("upper-b")
    }

    @Test
    fun `cli resultsHistoryName`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--results-history-name=b")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          results-history-name: a
      """
        assertThat(IosArgs.load(yaml).resultsHistoryName).isEqualTo("a")
        assertThat(IosArgs.load(yaml, cli).resultsHistoryName).isEqualTo("b")
    }

    @Test
    fun `cli maxTestShards`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--max-test-shards=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile

        flank:
          max-test-shards: 2
      """
        assertThat(IosArgs.load(yaml).maxTestShards).isEqualTo(2)
        assertThat(IosArgs.load(yaml, cli).maxTestShards).isEqualTo(3)
    }

    @Test
    fun `should not throw an error if validation is disabled (yml) -- max test shards`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
        flank:
          max-test-shards: ${Int.MAX_VALUE}
          skip-config-validation: true
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw an error if validation is disabled (command line) -- device`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
          device:
            - model: funnyDevice
              version: 28
              locale: en
              orientation: portrait
        flank:
          skip-config-validation: true
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `cli shardTime`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--shard-time=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile

        flank:
          shard-time: 2
      """
        assertThat(IosArgs.load(yaml).shardTime).isEqualTo(2)
        assertThat(IosArgs.load(yaml, cli).shardTime).isEqualTo(3)
    }

    @Test
    fun `cli disableSharding`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--disable-sharding")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile

        flank:
          disable-sharding: false
      """
        assertThat(IosArgs.load(yaml).disableSharding).isEqualTo(false)
        assertThat(IosArgs.load(yaml, cli).disableSharding).isEqualTo(true)
    }

    @Test
    fun `cli repeatTests`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--num-test-runs=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile

        flank:
          num-test-runs: 2
      """
        assertThat(IosArgs.load(yaml).repeatTests).isEqualTo(2)
        assertThat(IosArgs.load(yaml, cli).repeatTests).isEqualTo(3)
    }

    @Test
    fun `cli testTargetsAlwaysRun`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--test-targets-always-run=com.A,com.B")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
      """
        assertThat(IosArgs.load(yaml, cli).testTargetsAlwaysRun).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun `cli testTargets`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--test-targets=com.A,com.B")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
      """
        assertThat(IosArgs.load(yaml, cli).testTargets).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun `cli test`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--test=$testPath")

        val yaml = """
        gcloud:
          test: $xctestrunFile
          xctestrun-file: $xctestrunFile
      """

        assertThat(IosArgs.load(yaml).xctestrunZip).isEqualTo(xctestrunFileAbsolutePath)
        assertThat(IosArgs.load(yaml, cli).xctestrunZip).isEqualTo(testAbsolutePath)
    }

    @Test
    fun `cli xctestrunFile`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--xctestrun-file=$xctestrunFile")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """

        assertThat(IosArgs.load(yaml).xctestrunFile).isEqualTo(testAbsolutePath)
        assertThat(IosArgs.load(yaml, cli).xctestrunFile).isEqualTo(xctestrunFileAbsolutePath)
    }

    @Test
    fun `cli xcodeVersion`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--xcode-version=10.1")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          xcode-version: 10.0
      """

        assertThat(IosArgs.load(yaml).xcodeVersion).isEqualTo("10.0")
        assertThat(IosArgs.load(yaml, cli).xcodeVersion).isEqualTo("10.1")
    }

    @Test
    fun `cli device`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--device=model=iphone13pro,version=15.7,locale=zh_CN,orientation=default")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        val expectedDefaultDevice = Device(defaultIosModel, defaultIosVersion)
        val defaultDevices = IosArgs.load(yaml).devices
        assertThat(defaultDevices.first()).isEqualTo(expectedDefaultDevice)
        assertThat(defaultDevices.size).isEqualTo(1)

        val args = IosArgs.load(yaml, cli)
        val expectedDevice = Device("iphone13pro", "15.7", "zh_CN", "default")
        val actualDevices = args.devices
        assertThat(actualDevices.first()).isEqualTo(expectedDevice)
        assertThat(actualDevices.size).isEqualTo(1)
    }

    @Test
    fun `cli device repeat`() {
        val cli = IosRunCommand()
        val deviceCmd = "--device=model=iphone13pro,version=15.7,locale=zh_CN,orientation=default"
        CommandLine(cli).parseArgs(deviceCmd, deviceCmd)

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        val args = IosArgs.load(yaml, cli)
        val expectedDevice = Device("iphone13pro", "15.7", "zh_CN", "default")
        val actualDevices = args.devices
        assertThat(actualDevices.size).isEqualTo(2)
        assertThat(actualDevices[0]).isEqualTo(expectedDevice)
        assertThat(actualDevices[1]).isEqualTo(expectedDevice)
    }

    @Test
    fun `cli resultsDir`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--results-dir=b")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          results-dir: a
      """

        assertThat(IosArgs.load(yaml).resultsDir).isEqualTo("a")
        assertThat(IosArgs.load(yaml, cli).resultsDir).isEqualTo("b")
    }

    @Test
    fun `cli filesToDownload`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--files-to-download=a,b")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).filesToDownload).isEmpty()

        val args = IosArgs.load(yaml, cli)
        assertThat(args.filesToDownload).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun `cli flakyTestAttempts`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--num-flaky-test-attempts=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).flakyTestAttempts).isEqualTo(0)

        val args = IosArgs.load(yaml, cli)
        assertThat(args.flakyTestAttempts).isEqualTo(3)
    }

    @Test
    fun `cli local-results-dir`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--local-result-dir=a")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).localResultDir).isEqualTo("results")

        val args = IosArgs.load(yaml, cli)
        assertThat(args.localResultDir).isEqualTo("a")
    }

    @Test
    fun `cli smart-flank-gcs-path`() {
        val cli = IosRunCommand()
        val xml = "gs://bucket/foo.xml"
        CommandLine(cli).parseArgs("--smart-flank-gcs-path=$xml")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).smartFlankGcsPath).isEqualTo("")

        val args = IosArgs.load(yaml, cli)
        assertThat(args.smartFlankGcsPath).isEqualTo(xml)
    }

    @Test
    fun `cli smart-flank-disable-upload`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--smart-flank-disable-upload=true")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).smartFlankDisableUpload).isEqualTo(false)

        val args = IosArgs.load(yaml, cli)
        assertThat(args.smartFlankDisableUpload).isEqualTo(true)
    }

    @Test
    fun `cli keep-file-path`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--keep-file-path=true")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).keepFilePath).isEqualTo(false)

        val args = IosArgs.load(yaml, cli)
        assertThat(args.keepFilePath).isEqualTo(true)
    }

    @Test
    fun `cli run-timeout`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--run-timeout=20m")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).parsedTimeout).isEqualTo(Long.MAX_VALUE)

        val args = IosArgs.load(yaml, cli)
        assertThat(args.parsedTimeout).isEqualTo(20 * 60 * 1000L)
    }

    @Test
    fun `cli output-style`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--output-style=multi")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).outputStyle).isEqualTo(OutputStyle.Verbose)

        val args = IosArgs.load(yaml, cli)
        assertThat(args.outputStyle).isEqualTo(OutputStyle.Multi)
    }

    @Test
    fun `cli fail-fast`() {
        val cli = IosRunCommand()
        CommandLine(cli).parseArgs("--fail-fast")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).validate().failFast).isEqualTo(false)

        val args = IosArgs.load(yaml, cli).validate()
        assertThat(args.failFast).isEqualTo(true)
    }

    private fun getValidTestsSample() = listOf(
        "ClassOneTest/testOne",
        "ClassOneTest/testTwo",
        "ClassOneScreenshots/testOne",
        "ClassTwoScreenshots/testTwo",
        "ClassThreeTest/testName",
        "ClassFourTest/testFour"
    )

    @Test(expected = FlankConfigurationError::class)
    fun `invalid regex filter throws custom exception`() {
        val validTestMethods = mapOf("SampleXCTest" to listOf("test"))
        filterTests(validTestMethods, testTargets = listOf("*."))
    }

    @Test
    fun `filterTests emptyFilter`() {
        val tests = getValidTestsSample()
        val validTestMethods = mapOf("SampleXCTest" to tests)
        val actual = filterTests(validTestMethods, emptyList()).flatMap { it.value }

        assertThat(actual).containsExactlyElementsIn(tests)
    }

    @Test
    fun `filterTests regularFilter`() {
        val tests = getValidTestsSample()
        val validTestMethods = mapOf("SampleXCTest" to tests)
        val filter = listOf("ClassOneTest/testOne", "ClassFourTest/testFour")
        val actual = filterTests(validTestMethods, filter).flatMap { it.value }

        val expected = listOf("ClassOneTest/testOne", "ClassFourTest/testFour")

        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun `filterTests starFilter`() {
        val tests = getValidTestsSample()
        val validTestMethods = mapOf("SampleXCTest" to tests)
        val filter = listOf(".*?Test/testOne", ".*?/testFour")
        val actual = filterTests(validTestMethods, filter).flatMap { it.value }

        val expected = listOf(
            "ClassOneTest/testOne",
            "ClassFourTest/testFour"
        )

        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun `filterTests starAndRegularFilter`() {
        val tests = getValidTestsSample()
        val validTestMethods = mapOf("SampleXCTest" to tests)
        val filter = listOf(".*?Screenshots/testTwo", "ClassOneTest/testOne")
        val actual = filterTests(validTestMethods, filter).flatMap { it.value }

        val expected = listOf(
            "ClassTwoScreenshots/testTwo",
            "ClassOneTest/testOne"
        )

        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun `verify flank default settings for ios`() {
        val args = IosArgs.load(simpleFlankPath)
        assertFalse(args.recordVideo)
    }

    @Test
    fun `verify run timeout default value - ios`() {
        val iosArgs = IosArgs.load(simpleFlankPath)
        assertEquals(Long.MAX_VALUE, iosArgs.parsedTimeout)
    }

    @Test
    fun `verify keep file path default value - ios`() {
        val iosArgs = IosArgs.load(simpleFlankPath)
        assertFalse(iosArgs.keepFilePath)
    }

    @Test
    fun `if set max-test-shards to -1 should give maximum amount`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
        """.trimIndent()
        val args = IosArgs.load(yaml)
        assertEquals(IArgs.AVAILABLE_PHYSICAL_SHARD_COUNT_RANGE.last, args.maxTestShards)
    }

    @Test(expected = FlankConfigurationError::class)
    fun `verify appropriate error message when test and xctestrun not set`() {
        val yaml = """
        gcloud:
        flank:
          max-test-shards: -1
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    fun `verify no error message when test and xctestrun not set for refresh command`() {
        val yaml = """
        gcloud:
          test: $nonExistingTestPath
          xctestrun-file: $nonExistingTestPath
        flank:
          max-test-shards: -1
        """.trimIndent()
        IosArgs.load(yaml).validateRefresh()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `verify appropriate error message when xctestrun not set`() {
        val yaml = """
        gcloud:
          test: $testPath
        flank:
          max-test-shards: -1
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `verify appropriate error message when test not set`() {
        val yaml = """
        gcloud:
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `verify no error message when test and xctestrun-file set`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `verify no error message when test and xctestrun-file not set and validation is for refresh command`() {
        val yaml = """
        gcloud:
          test: $nonExistingTestPath
          xctestrun-file: $nonExistingxctestrunFile
        flank:
          max-test-shards: -1
        """.trimIndent()
        IosArgs.load(yaml).validateRefresh()
    }

    @Test
    fun `should set defaultTestTime`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
          default-test-time: 15
        """.trimIndent()
        val args = IosArgs.load(yaml)
        assertEquals(args.defaultTestTime, 15.0, 0.01)
    }

    @Test
    fun `should set defaultTestTime to default value if not specified`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
          use-average-test-time-for-new-tests: true
        """.trimIndent()
        val args = IosArgs.load(yaml)
        assertEquals(args.defaultTestTime, 120.0, 0.01)
    }

    @Test
    fun `should useAverageTestTimeForNewTests set to true`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
          use-average-test-time-for-new-tests: true
        """.trimIndent()
        val args = IosArgs.load(yaml)
        assertTrue(args.useAverageTestTimeForNewTests)
    }

    @Test
    fun `should useAverageTestTimeForNewTests set to false by default`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
        flank:
          max-test-shards: -1
        """.trimIndent()
        val args = IosArgs.load(yaml)
        assertFalse(args.useAverageTestTimeForNewTests)
    }

    @Test
    fun `should show warning message when results directory exist`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          results-dir: test
        flank:
          max-test-shards: -1
        """.trimIndent()
        mockkObject(GcStorage) {
            every { GcStorage.exist(any(), any()) } returns true

            // when
            IosArgs.load(yaml).validate()

            // then
            assertTrue(systemOutRule.log.contains("WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled"))
        }
    }

    @Test
    fun `should not print result-dir warning when same directory does not exist`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          results-dir: test
        flank:
          max-test-shards: -1
        """.trimIndent()
        mockkObject(GcStorage) {
            every { GcStorage.exist(any(), any()) } returns false

            // when
            IosArgs.load(yaml).validate()

            // then
            assertFalse(systemOutRule.log.contains("WARNING: Google cloud storage result directory should be unique, otherwise results from multiple test matrices will be overwritten or intermingled"))
        }
    }

    @Test
    fun `should not throw exception if game-loop is provided and nothing else`() {
        val yaml = """
        gcloud:
          app: $testPath
          results-dir: test
          type: game-loop
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop is set but app not`() {
        val yaml = """
        gcloud:
          results-dir: test
          type: game-loop
          scenario-numbers:
             - 1
             - 2
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if game-loop is not provided and scenario numbers are`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          results-dir: test
          scenario-numbers:
             - 1
             - 2
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception if game-loop is provided and scenario numbers are`() {
        val yaml = """
        gcloud:
          app: $testPath
          results-dir: test
          type: game-loop
          scenario-numbers:
             - 1
             - 2
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if invalid scenario numbers are provided`() {
        val yaml = """
        gcloud:
          app: $testPath
          results-dir: test
          type: game-loop
          scenario-numbers:
             - error1
             - error2
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception if app provided but not type equals gameloop`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          results-dir: test
          app: $testPath
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception if app provided and type equals gameloop`() {
        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
          results-dir: test
          type: game-loop
          app: $testPath
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception when only-test-configuration is specified for xctestrun v1`() {
        assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
        flank:
          only-test-configuration: pl
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test(expected = FlankConfigurationError::class)
    fun `should throw exception when skip-test-configuration is specified for xctestrun v1`() {
        assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
        flank:
          skip-test-configuration: pl
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception when only-test-configuration is specified for xctestrun v2`() {
        assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPlansPath
          xctestrun-file: $testPlansXctestrun
        flank:
          only-test-configuration: pl
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `should not throw exception when skip-test-configuration is specified for xctestrun v2`() {
        assumeFalse(isWindows)

        val yaml = """
        gcloud:
          test: $testPlansPath
          xctestrun-file: $testPlansXctestrun
        flank:
          skip-test-configuration: pl
        """.trimIndent()
        IosArgs.load(yaml).validate()
    }

    @Test
    fun `should add flank version to client-details even if client-details is empty`() {
        val yaml = """
         gcloud:
          test: $testPlansPath
          xctestrun-file: $testPlansXctestrun
        flank:
          skip-test-configuration: pl
        """.trimIndent()

        val parsedYml = IosArgs.load(yaml).validate()

        val (matrixMap) = runBlocking { parsedYml.runIosTests() }
        assertTrue(
            "Not all matrices have the `Flank Version` client-detail",
            matrixMap.map.all { it.value.clientDetails!!.containsKey("Flank Version") && it.value.clientDetails!!.containsKey("Flank Revision") }
        )
    }
}

private fun IosArgs.Companion.load(yamlData: String, cli: IosRunCommand? = null): IosArgs =
    load(StringReader(yamlData), cli)
