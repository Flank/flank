package ftl.args

import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.createJunitBucket
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.ArgsToString.devicesToString
import ftl.args.ArgsToString.listToString
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.ios.IosCatalog
import ftl.ios.Xctestrun
import ftl.util.Utils
import java.nio.file.Files
import java.nio.file.Path

class IosArgs(
    gcloudYml: GcloudYml,
    iosGcloudYml: IosGcloudYml,
    flankYml: FlankYml,
    iosFlankYml: IosFlankYml,
    override val data: String
) : IArgs {

    private val gcloud = gcloudYml.gcloud
    override val resultsBucket: String
    override val recordVideo = gcloud.recordVideo
    override val testTimeout = gcloud.timeout
    override val async = gcloud.async
    override val projectId = gcloud.project
    override val resultsHistoryName = gcloud.resultsHistoryName

    private val iosGcloud = iosGcloudYml.gcloud
    val xctestrunZip = iosGcloud.test
    val xctestrunFile = iosGcloud.xctestrunFile
    val xcodeVersion = iosGcloud.xcodeVersion
    val devices = iosGcloud.device

    private val flank = flankYml.flank
    override val testShards = flank.testShards
    override val repeatTests = flank.repeatTests
    override val junitGcsPath = flank.junitGcsPath
    override val testTargetsAlwaysRun = flank.testTargetsAlwaysRun

    private val iosFlank = iosFlankYml.flank
    val testTargets = iosFlank.testTargets

    // computed properties not specified in yaml
    override val testShardChunks: List<List<String>> by lazy {
        val validTestMethods = Xctestrun.findTestNames(xctestrunFile)
        validateTestMethods(testTargets, validTestMethods, "xctest binary")
        val testsToShard = if (testTargets.isEmpty()) {
            validTestMethods
        } else {
            testTargets
        }

        ArgsHelper.calculateShards(
            testMethodsToShard = testsToShard,
            testMethodsAlwaysRun = testTargetsAlwaysRun,
            testShards = testShards
            )
    }

    init {
        resultsBucket = createGcsBucket(projectId, gcloud.resultsBucket)
        createJunitBucket(projectId, flank.junitGcsPath)

        if (xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(xctestrunZip)
        } else {
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        assertFileExists(xctestrunFile, "xctestrunFile")

        devices.forEach { device -> assertDeviceSupported(device) }
        assertXcodeSupported(xcodeVersion)
    }

    private fun assertXcodeSupported(xcodeVersion: String?) {
        if (xcodeVersion == null) return
        if (!IosCatalog.supportedXcode(xcodeVersion)) {
            Utils.fatalError(("Xcode $xcodeVersion is not a supported Xcode version"))
        }
    }

    private fun assertDeviceSupported(device: Device) {
        if (!IosCatalog.supportedDevice(device.model, device.version)) {
            Utils.fatalError("iOS ${device.version} on ${device.model} is not a supported device")
        }
    }

    override fun toString(): String {
        return """
IosArgs
    gcloud:
      results-bucket: $resultsBucket
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
      project: $projectId
      results-history-name: $resultsHistoryName
      # iOS gcloud
      test: $xctestrunZip
      xctestrun-file: $xctestrunFile
      xcode-version: $xcodeVersion
      device:
${devicesToString(devices)}

    flank:
      testShards: $testShards
      repeatTests: $repeatTests
      junitGcsPath: $junitGcsPath
      test-targets-always-run:
${listToString(testTargetsAlwaysRun)}
      # iOS flank
      test-targets:
${listToString(testTargets)}
    """.trimIndent()
    }

    companion object : IArgsCompanion {
        override val validArgs by lazy {
            mergeYmlMaps(GcloudYml, IosGcloudYml, FlankYml, IosFlankYml)
        }

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
                iosFlankYml,
                data
            )
        }
    }
}
