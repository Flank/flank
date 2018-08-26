package ftl.args

import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.useMock
import ftl.ios.IosCatalog
import ftl.ios.Xctestrun
import ftl.util.Utils
import java.nio.file.Files
import java.nio.file.Path

class IosArgs(
        gcloudYml: GcloudYml,
        iosGcloudYml: IosGcloudYml,
        flankYml: FlankYml,
        iosFlankYml: IosFlankYml
) : IArgs {
    private val gcloud = gcloudYml.gcloud
    override val resultsBucket = gcloud.resultsBucket
    val recordVideo = gcloud.recordVideo
    val testTimeout = gcloud.timeout
    override val async = gcloud.async
    override val projectId = gcloud.project

    private val iosGcloud = iosGcloudYml.gcloud
    val xctestrunZip = iosGcloud.test
    val xctestrunFile = iosGcloud.xctestrunFile
    val devices = iosGcloud.device

    private val flank = flankYml.flank
    val testShards = flank.testShards
    val testRuns = flank.testRuns
    val testTargetsAlwaysRun = flank.testTargetsAlwaysRun

    private val iosFlank = iosFlankYml.flank
    val testTargets = iosFlank.testTargets

    // computed properties not specified in yaml
    val testShardChunks: List<List<String>>

    init {
        if (xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(xctestrunZip)
        } else {
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        assertFileExists(xctestrunFile, "xctestrunFile")

        if (!useMock) {
            devices.forEach { device -> assertDeviceSupported(device) }
        }

        val validTestMethods = Xctestrun.findTestNames(xctestrunFile)
        validateTestMethods(testTargets, validTestMethods, "xctest binary")

        testShardChunks = ArgsHelper.calculateShards(
                testTargets,
                validTestMethods,
                testTargetsAlwaysRun,
                testShards
        )
    }

    private fun assertDeviceSupported(device: Device) {
        if (!IosCatalog.supported(device.model, device.version)) {
            Utils.fatalError("iOS ${device.version} on ${device.model} is not a supported device")
        }
    }

    override fun toString(): String {
        return """
IosArgs
    gcloud:
      resultsBucket: $resultsBucket
      recordVideo: $recordVideo
      testTimeout: $testTimeout
      async: $async
      projectId: $projectId
      # iOS gcloud
      xctestrunZip: $xctestrunZip
      xctestrunFile: $xctestrunFile
      devices: $devices

    flank:
      testShards: $testShards
      testRuns: $testRuns
      testTargetsAlwaysRun: $testTargetsAlwaysRun
      # iOS flank
      testTargets: $testTargets
    """.trimIndent()
    }

    companion object {
        fun load(data: Path): IosArgs = IosArgs.load(String(Files.readAllBytes(data)))

        fun load(data: String): IosArgs {
            val flankYml = yamlMapper.readValue(data, FlankYml::class.java)
            val iosFlankYml = yamlMapper.readValue(data, IosFlankYml::class.java)
            val gcloudYml = yamlMapper.readValue(data, GcloudYml::class.java)
            val iosGcloudYml = yamlMapper.readValue(data, IosGcloudYml::class.java)

            return IosArgs(
                    gcloudYml,
                    iosGcloudYml,
                    flankYml,
                    iosFlankYml
            )
        }
    }


}
