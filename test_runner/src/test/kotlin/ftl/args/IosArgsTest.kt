package ftl.args

import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.args.yml.IosGcloudYmlParams
import ftl.config.Device
import ftl.test.util.FlankTestRunner
import ftl.test.util.TestHelper.assert
import org.junit.Rule
import org.junit.Test
import org.junit.contrib.java.lang.system.ExpectedSystemExit
import org.junit.contrib.java.lang.system.SystemErrRule
import org.junit.runner.RunWith

@RunWith(FlankTestRunner::class)
class IosArgsTest {
    private val empty = emptyList<String>()
    private val testPath = "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExample.zip"
    private val xctestrunFile =
        "./src/test/kotlin/ftl/fixtures/tmp/EarlGreyExampleMixedTests_iphoneos11.2-arm64.xctestrun"
    private val iosNonDefault = """
        gcloud:
          results-bucket: mockBucket
          record-video: false
          timeout: 70m
          async: true
          project: projectFoo

          test: $testPath
          xctestrun-file: $xctestrunFile
          device:
          - model: iphone8
            version: 11.2
            locale: c
            orientation: d

        flank:
          testShards: 7
          repeatTests: 8
          test-targets-always-run:
            - a/testGrantPermissions
          test-targets:
            - b/testBasicSelection
        """

    @Rule
    @JvmField
    val expectedExitRule: ExpectedSystemExit = ExpectedSystemExit.none()

    @Rule
    @JvmField
    val systemErrRule = SystemErrRule().muteForSuccessfulTests()!!

    @Test
    fun iosArgs_invalidDeviceExits() {
        expectedExitRule.expectSystemExitWithStatus(-1)
        val invalidDevice = listOf(Device("iphoneZ", "99.9"))
        IosArgs(
            GcloudYml(),
            IosGcloudYml(IosGcloudYmlParams(test = testPath, xctestrunFile = xctestrunFile, device = invalidDevice)),
            FlankYml(),
            IosFlankYml()
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
            assert(projectId, "projectFoo")

            // IosGcloudYml
            assert(xctestrunZip, testPath)
            assert(xctestrunFile, xctestrunFile)
            assert(devices, listOf(Device("iphone8", "11.2", "c", "d")))

            // FlankYml
            assert(testShards, 7)
            assert(repeatTests, 8)
            assert(testTargetsAlwaysRun, listOf("a/testGrantPermissions"))

            // IosFlankYml
            assert(testTargets, listOf("b/testBasicSelection"))
        }
    }

    @Test
    fun iosArgsToString() {
        val iosArgs = IosArgs.load(iosNonDefault)
        assert(
            iosArgs.toString(), """
IosArgs
    gcloud:
      resultsBucket: mockBucket
      recordVideo: false
      testTimeout: 70m
      async: true
      projectId: projectFoo
      # iOS gcloud
      xctestrunZip: $testPath
      xctestrunFile: $xctestrunFile
      devices: [Device(model=iphone8, version=11.2, locale=c, orientation=d)]

    flank:
      testShards: 7
      repeatTests: 8
      testTargetsAlwaysRun: [a/testGrantPermissions]
      # iOS flank
      testTargets: [b/testBasicSelection]
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
            assert(projectId, "mockProjectId")

            // IosGcloudYml
            assert(xctestrunZip, testPath)
            assert(xctestrunFile, xctestrunFile)
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
            assert(testShardChunks.size, 6)
            testShardChunks.forEach { chunk -> assert(chunk.size, 1) }
        }
    }
}
