package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.args.yml.IosGcloudYmlParams
import ftl.cli.firebase.test.ios.IosRunCommand
import ftl.config.Device
import ftl.config.FtlConstants.defaultIosModel
import ftl.config.FtlConstants.defaultIosVersion
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.absolutePath
import ftl.test.util.TestHelper.assert
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.rules.ExpectedException
import org.junit.runner.RunWith
import picocli.CommandLine

@RunWith(FlankTestRunner::class)
class IosArgsTest {
    private val empty = emptyList<String>()
    private val testPath = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip"
    private val xctestrunFile =
        "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleSwiftTests_iphoneos12.1-arm64e.xctestrun"
    private val invalidApp = "../test_app/apks/invalid.apk"
    private val xctestrunFileAbsolutePath = xctestrunFile.absolutePath()
    private val testAbsolutePath = testPath.absolutePath()
    private val iosNonDefault = """
        gcloud:
          results-bucket: mockBucket
          record-video: false
          timeout: 70m
          async: true
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
          flaky-test-attempts: 4

        flank:
          max-test-shards: 7
          shard-time: 60
          repeat-tests: 8
          files-to-download:
            - /sdcard/screenshots
          test-targets-always-run:
            - a/testGrantPermissions
            - a/testGrantPermissions2
          test-targets:
            - b/testBasicSelection
            - b/testBasicSelection2
          disable-sharding: true
        """

    @Rule
    @JvmField
    val exceptionRule = ExpectedException.none()!!

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Test
    fun iosArgs_invalidDeviceExits() {
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
    fun iosArgs_invalidXcodeExits() {
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
    fun iosArgs() {
        val iosArgs = IosArgs.load(iosNonDefault)

        with(iosArgs) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, false)
            assert(testTimeout, "70m")
            assert(async, true)
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
        }
    }

    @Test
    fun iosArgs_toString() {
        val iosArgs = IosArgs.load(iosNonDefault)
        assert(
            iosArgs.toString(), """
IosArgs
    gcloud:
      results-bucket: mockBucket
      results-dir: null
      record-video: false
      timeout: 70m
      async: true
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
      flaky-test-attempts: 4

    flank:
      max-test-shards: 7
      shard-time: 60
      repeat-tests: 8
      smart-flank-gcs-path:${' '}
      test-targets-always-run:
        - a/testGrantPermissions
        - a/testGrantPermissions2
      files-to-download:
        - /sdcard/screenshots
      # iOS flank
      test-targets:
        - b/testBasicSelection
        - b/testBasicSelection2
      disable-sharding: true
      project: projectFoo
      local-result-dir: results
""".trimIndent()
        )
    }

    @Test
    fun iosArgsDefault() {
        val iosArgs = IosArgs.load(
            """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
        """
        )

        with(iosArgs) {
            // GcloudYml
            assert(resultsBucket, "mockBucket")
            assert(recordVideo, true)
            assert(testTimeout, "15m")
            assert(async, false)
            assert(project, "mockProjectId")

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
        }
    }

    @Test
    fun negativeOneTestShards() {
        val iosArgs = IosArgs.load(
            """
    gcloud:
      test: $testPath
      xctestrun-file: $xctestrunFile

    flank:
      max-test-shards: -1
"""
        )

        with(iosArgs) {
            assert(maxTestShards, -1)
            assert(testShardChunks.size, 17)
            testShardChunks.forEach { chunk -> assert(chunk.size, 1) }
        }
    }

    @Test
    fun iosArgs_emptyFlank() {
        val iosArgs = IosArgs.load(
            """
    gcloud:
      test: $testPath
      xctestrun-file: $xctestrunFile

    flank:
"""
        )

        with(iosArgs) {
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
    fun cli_resultsBucket() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--results-bucket=a")

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
    fun cli_recordVideo() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--record-video")

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
    fun cli_noRecordVideo() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--no-record-video")

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
    fun cli_timeout() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--timeout=1m")

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
    fun cli_async() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--async")

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
    fun cli_project() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--project=b")

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
    fun cli_resultsHistoryName() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--results-history-name=b")

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
    fun cli_maxTestShards() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--test-shards=3")

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
    fun cli_shardTime() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--shard-time=3")

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
    fun cli_disableSharding() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--disable-sharding")

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
    fun cli_repeatTests() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--repeat-tests=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile

        flank:
          repeat-tests: 2
      """
        assertThat(IosArgs.load(yaml).repeatTests).isEqualTo(2)
        assertThat(IosArgs.load(yaml, cli).repeatTests).isEqualTo(3)
    }

    @Test
    fun cli_testTargetsAlwaysRun() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--test-targets-always-run=com.A,com.B")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
      """
        assertThat(IosArgs.load(yaml, cli).testTargetsAlwaysRun).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun cli_testTargets() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--test-targets=com.A,com.B")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile
      """
        assertThat(IosArgs.load(yaml, cli).testTargets).isEqualTo(arrayListOf("com.A", "com.B"))
    }

    @Test
    fun cli_test() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--test=$testPath")

        val yaml = """
        gcloud:
          test: $xctestrunFile
          xctestrun-file: $xctestrunFile
      """

        assertThat(IosArgs.load(yaml).xctestrunZip).isEqualTo(xctestrunFileAbsolutePath)
        assertThat(IosArgs.load(yaml, cli).xctestrunZip).isEqualTo(testAbsolutePath)
    }

    @Test
    fun cli_xctestrunFile() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--xctestrun-file=$xctestrunFile")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """

        assertThat(IosArgs.load(yaml).xctestrunFile).isEqualTo(testAbsolutePath)
        assertThat(IosArgs.load(yaml, cli).xctestrunFile).isEqualTo(xctestrunFileAbsolutePath)
    }

    @Test
    fun cli_xcodeVersion() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--xcode-version=10.1")

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
    fun cli_device() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--device=model=iphone8,version=12.0,locale=zh_CN,orientation=default")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        val expectedDefaultDevice = Device(defaultIosModel, defaultIosVersion)
        val defaultDevices = IosArgs.load(yaml).devices
        assertThat(defaultDevices.first()).isEqualTo(expectedDefaultDevice)
        assertThat(defaultDevices.size).isEqualTo(1)

        val iosArgs = IosArgs.load(yaml, cli)
        val expectedDevice = Device("iphone8", "12.0", "zh_CN", "default")
        val actualDevices = iosArgs.devices
        assertThat(actualDevices.first()).isEqualTo(expectedDevice)
        assertThat(actualDevices.size).isEqualTo(1)
    }

    @Test
    fun cli_device_repeat() {
        val cli = IosRunCommand()
        val deviceCmd = "--device=model=iphone8,version=12.0,locale=zh_CN,orientation=default"
        CommandLine(cli).parse(deviceCmd, deviceCmd)

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        val iosArgs = IosArgs.load(yaml, cli)
        val expectedDevice = Device("iphone8", "12.0", "zh_CN", "default")
        val actualDevices = iosArgs.devices
        assertThat(actualDevices.size).isEqualTo(2)
        assertThat(actualDevices[0]).isEqualTo(expectedDevice)
        assertThat(actualDevices[1]).isEqualTo(expectedDevice)
    }

    @Test
    fun cli_resultsDir() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--results-dir=b")

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
    fun cli_filesToDownload() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--files-to-download=a,b")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).filesToDownload).isEmpty()

        val androidArgs = IosArgs.load(yaml, cli)
        assertThat(androidArgs.filesToDownload).isEqualTo(listOf("a", "b"))
    }

    @Test
    fun cli_flakyTestAttempts() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--flaky-test-attempts=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).flakyTestAttempts).isEqualTo(0)

        val androidArgs = IosArgs.load(yaml, cli)
        assertThat(androidArgs.flakyTestAttempts).isEqualTo(3)
    }

    @Test
    fun `cli local-results-dir`() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--local-result-dir=a")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $testPath
      """
        assertThat(IosArgs.load(yaml).localResultDir).isEqualTo("results")

        val androidArgs = IosArgs.load(yaml, cli)
        assertThat(androidArgs.localResultDir).isEqualTo("a")
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
    fun filterTests_emptyFilter() {
        val tests = getValidTestsSample()
        val actual = filterTests(tests, emptyList())

        assertThat(actual).containsExactlyElementsIn(tests)
    }

    @Test
    fun filterTests_regularFilter() {
        val tests = getValidTestsSample()
        val filter = listOf("ClassOneTest/testOne", "ClassFourTest/testFour")
        val actual = filterTests(tests, filter)

        val expected = listOf("ClassOneTest/testOne", "ClassFourTest/testFour")

        assertThat(actual).containsExactlyElementsIn(expected)
    }

    @Test
    fun filterTests_starFilter() {
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
    fun filterTests_starAndRegularFilter() {
        val tests = getValidTestsSample()
        val filter = listOf(".*?Screenshots/testTwo", "ClassOneTest/testOne")
        val actual = filterTests(tests, filter)

        val expected = listOf(
            "ClassTwoScreenshots/testTwo",
            "ClassOneTest/testOne"
        )

        assertThat(actual).containsExactlyElementsIn(expected)
    }
}
