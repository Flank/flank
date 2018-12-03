package ftl.args

import com.google.common.truth.Truth.assertThat
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.args.yml.IosGcloudYmlParams
import ftl.cli.firebase.test.ios.IosRunCommand
import ftl.config.Device
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

        flank:
          testShards: 7
          repeatTests: 8
          test-targets-always-run:
            - a/testGrantPermissions
            - a/testGrantPermissions2
          test-targets:
            - b/testBasicSelection
            - b/testBasicSelection2
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
            assert(testShards, 7)
            assert(repeatTests, 8)
            assert(testTargetsAlwaysRun, listOf("a/testGrantPermissions", "a/testGrantPermissions2"))

            // IosFlankYml
            assert(testTargets, listOf("b/testBasicSelection", "b/testBasicSelection2"))
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
      record-video: false
      timeout: 70m
      async: true
      project: projectFoo
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

    flank:
      testShards: 7
      repeatTests: 8
      smartFlankGcsPath:${' '}
      test-targets-always-run:
        - a/testGrantPermissions
        - a/testGrantPermissions2
      # iOS flank
      test-targets:
        - b/testBasicSelection
        - b/testBasicSelection2
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
            assert(devices, listOf(Device("iphone8", "11.2")))

            // FlankYml
            assert(testShards, 1)
            assert(repeatTests, 1)
            assert(testTargetsAlwaysRun, emptyList<String>())

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
      testShards: -1
"""
        )

        with(iosArgs) {
            assert(testShards, -1)
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
    fun cli_testShards() {
        val cli = IosRunCommand()
        CommandLine(cli).parse("--test-shards=3")

        val yaml = """
        gcloud:
          test: $testPath
          xctestrun-file: $xctestrunFile

        flank:
          testShards: 2
      """
        assertThat(IosArgs.load(yaml).testShards).isEqualTo(2)
        assertThat(IosArgs.load(yaml, cli).testShards).isEqualTo(3)
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
          repeatTests: 2
      """
        assertThat(IosArgs.load(yaml).repeatTests).isEqualTo(2)
        assertThat(IosArgs.load(yaml, cli).repeatTests).isEqualTo(3)
    }
}
