package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.args.yml.IosGcloudYmlParams
import ftl.cli.firebase.test.ios.IosRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.run.status.OutputStyle
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import ftl.test.util.TestHelper.getPath
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assume
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import picocli.CommandLine
import java.io.StringReader

@Suppress("TooManyFunctions")
@RunWith(FlankTestRunner::class)
class IosArgsTest {
    private val empty = emptyList<String>()
    private val simpleFlankPath = getPath("src/test/kotlin/ftl/fixtures/simple-ios-flank.yml")
    private val testPath = "./src/test/kotlin/ftl/fixtures/tmp/earlgrey_example.zip"
    private val xctestrunFile =
        "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos13.4-arm64e.xctestrun"
    private val invalidApp = "../test_app/apks/invalid.apk"
    private val xctestrunFileAbsolutePath = xctestrunFile.absolutePath()
    private val testAbsolutePath = testPath.absolutePath()
    private val iosNonDefault = """
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
          results-history-name: ios-history

          test: $testPath
          xctestrun-file: $xctestrunFile
          xcode-version: 9.2
          device:
          - model: iphone8
            version: 11.2
            locale: c
            orientation: d
          - model: iphone8
            version: 11.2
            locale: c
            orientation: d
          num-flaky-test-attempts: 4

        flank:
          max-test-shards: 7
          shard-time: 60
          num-test-runs: 8
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
          ignore-failed-tests: true
          keep-file-path: true
          output-style: single
        """

    @Rule
    @JvmField
    val exceptionRule = ExpectedException.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

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
        exceptionRule.expectMessage("iOS 99.9 on iphoneZ is not a supported device")
        val invalidDevice = listOf(Device("iphoneZ", "99.9"))
        IosArgs(
            GcloudYml(),
            IosGcloudYml(IosGcloudYmlParams(test = testPath, xctestrunFile = xctestrunFile, device = invalidDevice)),
            FlankYml(),
            IosFlankYml(),
            ""
        )
    }

    @Test
    fun `args invalidXcodeExits`() {
        exceptionRule.expectMessage("Xcode 99.9 is not a supported Xcode version")
        IosArgs(
            GcloudYml(),
            IosGcloudYml(IosGcloudYmlParams(test = testPath, xctestrunFile = xctestrunFile, xcodeVersion = "99.9")),
            FlankYml(),
            IosFlankYml(),
            ""
        )
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
            assert(project, "projectFoo")
            assert(resultsHistoryName ?: "", "ios-history")

            // IosGcloudYml
            assert(xctestrunZip, testAbsolutePath)
            assert(xctestrunFile, xctestrunFileAbsolutePath)
            val device = Device("iphone8", "11.2", "c", "d")
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
            assert(outputStyle, OutputStyle.Single)
        }
    }

    @Test
    fun `args toString`() {
        val args = IosArgs.load(iosNonDefault)
        assert(
            args.toString(), """
IosArgs
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
      results-history-name: ios-history
      # iOS gcloud
      test: $testAbsolutePath
      xctestrun-file: $xctestrunFileAbsolutePath
      xcode-version: 9.2
      device:
        - model: iphone8
          version: 11.2
          locale: c
          orientation: d
        - model: iphone8
          version: 11.2
          locale: c
          orientation: d
      num-flaky-test-attempts: 4

    flank:
      max-test-shards: 7
      shard-time: 60
      num-test-runs: 8
      smart-flank-gcs-path:${' '}
      smart-flank-disable-upload: false
      test-targets-always-run:
        - a/testGrantPermissions
        - a/testGrantPermissions2
      files-to-download:
        - /sdcard/screenshots
      keep-file-path: true
      # iOS flank
      test-targets:
        - b/testBasicSelection
        - b/testBasicSelection2
      disable-sharding: true
      project: projectFoo
      local-result-dir: results
      run-timeout: 15m
      ignore-failed-tests: true
      output-style: single
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
      results-dir: null
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
        - model: iphone8
          version: 12.0
          locale: en
          orientation: portrait
      num-flaky-test-attempts: 0

    flank:
      max-test-shards: 1
      shard-time: -1
      num-test-runs: 1
      smart-flank-gcs-path: 
      smart-flank-disable-upload: false
      test-targets-always-run:
      files-to-download:
      keep-file-path: false
      # iOS flank
      test-targets:
      disable-sharding: false
      project: mockProjectId
      local-result-dir: results
      run-timeout: -1
      ignore-failed-tests: false
      output-style: multi
        """.trimIndent(), args.toString()
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
            assert(project, "mockProjectId")
            assert(clientDetails, null)
            assert(networkProfile, null)

            // IosGcloudYml
            assert(xctestrunZip, testAbsolutePath)
            assert(xctestrunFile, xctestrunFileAbsolutePath)
            assert(devices, listOf(Device("iphone8", "12.0")))
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
        }
    }

    @Test
    fun negativeOneTestShards() {
        Assume.assumeFalse(FtlConstants.isWindows)

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
            assert(maxTestShards, -1)
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
        IosArgs.load(yaml).testShardChunks
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
        IosArgs.load(yaml).testShardChunks
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
        CommandLine(cli).parseArgs("--device=model=iphone8,version=12.0,locale=zh_CN,orientation=default")

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
        val expectedDevice = Device("iphone8", "12.0", "zh_CN", "default")
        val actualDevices = args.devices
        assertThat(actualDevices.first()).isEqualTo(expectedDevice)
        assertThat(actualDevices.size).isEqualTo(1)
    }

    @Test
    fun `cli device repeat`() {
        val cli = IosRunCommand()
        val deviceCmd = "--device=model=iphone8,version=12.0,locale=zh_CN,orientation=default"
        CommandLine(cli).parseArgs(deviceCmd, deviceCmd)

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        val args = IosArgs.load(yaml, cli)
        val expectedDevice = Device("iphone8", "12.0", "zh_CN", "default")
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
        CommandLine(cli).parseArgs("--output-style=verbose")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).outputStyle).isEqualTo(OutputStyle.Multi)

        val args = IosArgs.load(yaml, cli)
        assertThat(args.outputStyle).isEqualTo(OutputStyle.Verbose)
    }

    private fun getValidTestsSample() = listOf(
        "ClassOneTest/testOne",
        "ClassOneTest/testTwo",
        "ClassOneScreenshots/testOne",
        "ClassTwoScreenshots/testTwo",
        "ClassThreeTest/testName",
        "ClassFourTest/testFour"
    )

    @Test(expected = IllegalArgumentException::class)
    fun `invalid regex filter throws custom exception`() {
        filterTests(listOf("test"), testTargetsRgx = listOf("*."))
    }

    @Test
    fun `filterTests emptyFilter`() {
        val tests = getValidTestsSample()
        val actual = filterTests(tests, emptyList())

        assertThat(actual).containsExactlyElementsIn(tests)
    }

    @Test
    fun `filterTests regularFilter`() {
        val tests = getValidTestsSample()
        val filter = listOf("ClassOneTest/testOne", "ClassFourTest/testFour")
        val actual = filterTests(tests, filter)

        val expected = listOf("ClassOneTest/testOne", "ClassFourTest/testFour")

        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun `filterTests starFilter`() {
        val tests = getValidTestsSample()
        val filter = listOf(".*?Test/testOne", ".*?/testFour")
        val actual = filterTests(tests, filter)

        val expected = listOf(
            "ClassOneTest/testOne",
            "ClassFourTest/testFour"
        )

        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun `filterTests starAndRegularFilter`() {
        val tests = getValidTestsSample()
        val filter = listOf(".*?Screenshots/testTwo", "ClassOneTest/testOne")
        val actual = filterTests(tests, filter)

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
}

private fun IosArgs.Companion.load(yamlData: String, cli: IosRunCommand? = null): IosArgs = load(StringReader(yamlData), cli)
