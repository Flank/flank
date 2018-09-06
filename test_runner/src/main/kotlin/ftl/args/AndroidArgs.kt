package ftl.args

import com.linkedin.dex.parser.DexParser
import ftl.android.AndroidCatalog
import ftl.android.IncompatibleModelVersion
import ftl.android.SupportedDeviceConfig
import ftl.android.UnsupportedModelId
import ftl.android.UnsupportedVersionId
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.calculateShards
import ftl.args.ArgsHelper.getGcsBucket
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.validateTestMethods
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.useMock
import ftl.gc.GcStorage
import kotlinx.coroutines.experimental.runBlocking
import java.nio.file.Files
import java.nio.file.Path

// set default values, init properties, etc.
class AndroidArgs(
    gcloudYml: GcloudYml,
    androidGcloudYml: AndroidGcloudYml,
    flankYml: FlankYml
) : IArgs {
    private val gcloud = gcloudYml.gcloud
    override val resultsBucket: String
    val recordVideo = gcloud.recordVideo
    val testTimeout = gcloud.timeout
    override val async = gcloud.async
    override val projectId = gcloud.project

    private val androidGcloud = androidGcloudYml.gcloud
    val appApk = androidGcloud.app
    val testApk = androidGcloud.test
    val autoGoogleLogin = androidGcloud.autoGoogleLogin
    val useOrchestrator = androidGcloud.useOrchestrator
    val environmentVariables = androidGcloud.environmentVariables
    val directoriesToPull = androidGcloud.directoriesToPull
    val performanceMetrics = androidGcloud.performanceMetrics
    val testTargets = androidGcloud.testTargets
    val devices = androidGcloud.device

    private val flank = flankYml.flank
    val testShards = flank.testShards
    override val testRuns = flank.testRuns
    val testTargetsAlwaysRun = flank.testTargetsAlwaysRun

    // computed properties not specified in yaml
    override val testShardChunks: List<List<String>>

    init {
        resultsBucket = getGcsBucket(projectId, gcloud.resultsBucket)

        if (appApk.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(appApk)
        } else {
            assertFileExists(appApk, "appApk")
        }

        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(testApk)
        } else {
            assertFileExists(testApk, "testApk")
        }

        // Download test APK if necessary so it can be used to validate test methods
        var testLocalApk = testApk
        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            runBlocking {
                testLocalApk = GcStorage.downloadTestApk(this@AndroidArgs)
            }
        }

        if (!useMock) {
            devices.forEach { device -> assertDeviceSupported(device) }
        }

        val validTestMethods = DexParser.findTestMethods(testLocalApk).map { "class ${it.testName}" }
        validateTestMethods(testTargets, validTestMethods, "Test APK")

        testShardChunks = calculateShards(
            testTargets,
            validTestMethods,
            testTargetsAlwaysRun,
            testShards
        )
    }

    private fun assertDeviceSupported(device: Device) {
        val deviceConfigTest = AndroidCatalog.supportedDeviceConfig(device.model, device.version)
        when (deviceConfigTest) {
            SupportedDeviceConfig -> {
            }
            UnsupportedModelId -> throw RuntimeException("Unsupported model id, '${device.model}'\nSupported model ids: ${AndroidCatalog.androidModelIds}")
            UnsupportedVersionId -> throw RuntimeException("Unsupported version id, '${device.version}'\nSupported Version ids: ${AndroidCatalog.androidVersionIds}")
            is IncompatibleModelVersion -> throw RuntimeException("Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': $deviceConfigTest")
        }
    }

    override fun toString(): String {
        return """
AndroidArgs
    gcloud:
      resultsBucket: $resultsBucket
      recordVideo: $recordVideo
      testTimeout: $testTimeout
      async: $async
      projectId: $projectId
      # Android gcloud
      appApk: $appApk
      testApk: $testApk
      autoGoogleLogin: $autoGoogleLogin
      useOrchestrator: $useOrchestrator
      environmentVariables: $environmentVariables
      directoriesToPull: $directoriesToPull
      performanceMetrics: $performanceMetrics
      testTargets: $testTargets
      devices: $devices

    flank:
      testShards: $testShards
      testRuns: $testRuns
      testTargetsAlwaysRun: $testTargetsAlwaysRun
   """.trimIndent()
    }

    companion object : IArgsCompanion {
        override val validArgs by lazy {
            mergeYmlMaps(GcloudYml, AndroidGcloudYml, FlankYml)
        }

        fun load(data: Path): AndroidArgs = load(String(Files.readAllBytes(data)))

        fun load(data: String): AndroidArgs {
            val flankYml = yamlMapper.readValue(data, FlankYml::class.java)
            val gcloudYml = yamlMapper.readValue(data, GcloudYml::class.java)
            val androidGcloudYml = yamlMapper.readValue(data, AndroidGcloudYml::class.java)

            return AndroidArgs(
                gcloudYml,
                androidGcloudYml,
                flankYml
            )
        }
    }
}
