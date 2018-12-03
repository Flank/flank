package ftl.args

import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.createJunitBucket
import ftl.args.ArgsHelper.evaluateFilePath
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.ArgsToString.devicesToString
import ftl.args.ArgsToString.listToString
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.IosFlankYml
import ftl.args.yml.IosGcloudYml
import ftl.cli.firebase.test.ios.IosRunCommand
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
    override val data: String,
    val cli: IosRunCommand? = null
) : IArgs {

    private val gcloud = gcloudYml.gcloud
    override val resultsBucket: String
    override val recordVideo = cli?.recordVideo ?: cli?.noRecordVideo?.not() ?: gcloud.recordVideo
    override val testTimeout = cli?.timeout ?: gcloud.timeout
    override val async = cli?.async ?: gcloud.async
    override val project = cli?.project ?: gcloud.project
    override val resultsHistoryName = cli?.resultsHistoryName ?: gcloud.resultsHistoryName

    private val iosGcloud = iosGcloudYml.gcloud
    var xctestrunZip = cli?.test ?: iosGcloud.test
    var xctestrunFile = cli?.xctestrunFile ?: iosGcloud.xctestrunFile
    val xcodeVersion = iosGcloud.xcodeVersion
    val devices = iosGcloud.device

    private val flank = flankYml.flank
    override val testShards = cli?.testShards ?: flank.testShards
    override val repeatTests = cli?.repeatTests ?: flank.repeatTests
    override val smartFlankGcsPath = flank.smartFlankGcsPath
    override val testTargetsAlwaysRun = cli?.testTargetsAlwaysRun ?: flank.testTargetsAlwaysRun

    private val iosFlank = iosFlankYml.flank
    val testTargets = cli?.testTargets ?: iosFlank.testTargets

    // computed properties not specified in yaml
    override val testShardChunks: List<List<String>> by lazy {
        val validTestMethods = Xctestrun.findTestNames(xctestrunFile)
        validateTestMethods(testTargets, validTestMethods, "xctest binary")
        val testsToShard = if (testTargets.isEmpty()) {
            validTestMethods
        } else {
            testTargets
        }.distinct()

        ArgsHelper.calculateShards(testsToShard, this)
    }

    init {
        resultsBucket = createGcsBucket(project, cli?.resultsBucket ?: gcloud.resultsBucket)
        createJunitBucket(project, flank.smartFlankGcsPath)

        if (xctestrunZip.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(xctestrunZip)
        } else {
            xctestrunZip = evaluateFilePath(xctestrunZip)
            assertFileExists(xctestrunZip, "xctestrunZip")
        }
        xctestrunFile = evaluateFilePath(xctestrunFile)
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
      project: $project
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
      smartFlankGcsPath: $smartFlankGcsPath
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

        fun load(data: Path, cli: IosRunCommand? = null): IosArgs = IosArgs.load(String(Files.readAllBytes(data)), cli)

        fun load(data: String, cli: IosRunCommand? = null): IosArgs {
            val flankYml = yamlMapper.readValue(data, FlankYml::class.java)
            val iosFlankYml = yamlMapper.readValue(data, IosFlankYml::class.java)
            val gcloudYml = yamlMapper.readValue(data, GcloudYml::class.java)
            val iosGcloudYml = yamlMapper.readValue(data, IosGcloudYml::class.java)

            return IosArgs(
                gcloudYml,
                iosGcloudYml,
                flankYml,
                iosFlankYml,
                data,
                cli
            )
        }
    }
}
