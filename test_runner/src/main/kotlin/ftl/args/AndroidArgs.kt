package ftl.args

import com.linkedin.dex.parser.DexParser
import com.linkedin.dex.parser.TestMethod
import ftl.android.AndroidCatalog
import ftl.android.IncompatibleModelVersion
import ftl.android.SupportedDeviceConfig
import ftl.android.UnsupportedModelId
import ftl.android.UnsupportedVersionId
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.convertShards
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.createJunitBucket
import ftl.args.ArgsHelper.evaluateFilePath
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.ArgsToString.devicesToString
import ftl.args.ArgsToString.listToString
import ftl.args.ArgsToString.mapToString
import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.useMock
import ftl.filter.TestFilters
import ftl.gc.GcStorage
import ftl.reports.xml.model.JUnitTestResult
import ftl.shard.Shard
import ftl.util.Utils
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path

// set default values, init properties, etc.
class AndroidArgs(
    gcloudYml: GcloudYml,
    androidGcloudYml: AndroidGcloudYml,
    flankYml: FlankYml,
    override val data: String,
    val cli: AndroidRunCommand = AndroidRunCommand()
) : IArgs {
    private val gcloud = gcloudYml.gcloud
    override val resultsBucket: String
    override val recordVideo = gcloud.recordVideo
    override val testTimeout = gcloud.timeout
    override val async = gcloud.async
    override val projectId = gcloud.project
    override val resultsHistoryName = gcloud.resultsHistoryName

    private val androidGcloud = androidGcloudYml.gcloud
    var appApk = cli.app ?: androidGcloud.app
    var testApk = cli.test ?: androidGcloud.test
    val autoGoogleLogin = androidGcloud.autoGoogleLogin

    // We use not() on noUseOrchestrator because if the flag is on, useOrchestrator needs to be false
    val useOrchestrator = cli.useOrchestrator ?: cli.noUseOrchestrator?.not() ?: androidGcloud.useOrchestrator
    val environmentVariables = androidGcloud.environmentVariables
    val directoriesToPull = androidGcloud.directoriesToPull
    val performanceMetrics = androidGcloud.performanceMetrics
    val testTargets = cli.testTargets ?: androidGcloud.testTargets
    val devices = androidGcloud.device

    private val flank = flankYml.flank
    override val testShards = flank.testShards
    override val repeatTests = flank.repeatTests
    override val junitGcsPath = flank.junitGcsPath
    override val testTargetsAlwaysRun = flank.testTargetsAlwaysRun

    // computed properties not specified in yaml
    override val testShardChunks: List<List<String>> by lazy {
        // Download test APK if necessary so it can be used to validate test methods
        var testLocalApk = testApk
        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            runBlocking {
                testLocalApk = GcStorage.downloadTestApk(this@AndroidArgs)
            }
        }

        val filteredTests = getTestMethods(testLocalApk)
        val oldTestResult = GcStorage.downloadJunitXml(this) ?: JUnitTestResult(mutableListOf())
        convertShards(Shard.calculateShardsByTime(filteredTests, oldTestResult, testShards)).toMutableList()
    }

    init {
        resultsBucket = createGcsBucket(projectId, gcloud.resultsBucket)
        createJunitBucket(projectId, flank.junitGcsPath)

        if (appApk.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(appApk)
        } else {
            appApk = evaluateFilePath(appApk)
            assertFileExists(appApk, "appApk")
        }

        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            assertGcsFileExists(testApk)
        } else {
            testApk = evaluateFilePath(testApk)
            assertFileExists(testApk, "testApk")
        }

        devices.forEach { device -> assertDeviceSupported(device) }
    }

    private fun getTestMethods(testLocalApk: String): List<String> {
        val allTestMethods = DexParser.findTestMethods(testLocalApk)
        require(allTestMethods.isNotEmpty()) { Utils.fatalError("Test APK has no tests") }
        val testFilter = TestFilters.fromTestTargets(testTargets)
        val filteredTests = allTestMethods
            .asSequence()
            .filter(testFilter.shouldRun)
            .map(TestMethod::testName)
            .map { "class $it" }
            .toList()
        require(useMock || filteredTests.isNotEmpty()) { Utils.fatalError("All tests filtered out") }
        return filteredTests
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
      results-bucket: $resultsBucket
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
      project: $projectId
      results-history-name: $resultsHistoryName
      # Android gcloud
      app: $appApk
      test: $testApk
      auto-google-login: $autoGoogleLogin
      use-orchestrator: $useOrchestrator
      environment-variables:
${mapToString(environmentVariables)}
      directories-to-pull:
${listToString(directoriesToPull)}
      performance-metrics: $performanceMetrics
      test-targets:
${listToString(testTargets)}
      device:
${devicesToString(devices)}

    flank:
      testShards: $testShards
      repeatTests: $repeatTests
      junitGcsPath: $junitGcsPath
      test-targets-always-run:
${listToString(testTargetsAlwaysRun)}
   """.trimIndent()
    }

    companion object : IArgsCompanion {
        override val validArgs by lazy {
            mergeYmlMaps(GcloudYml, AndroidGcloudYml, FlankYml)
        }

        fun load(data: Path, cli: AndroidRunCommand = AndroidRunCommand()): AndroidArgs = load(String(Files.readAllBytes(data)), cli)

        fun load(data: String, cli: AndroidRunCommand = AndroidRunCommand()): AndroidArgs {
            val flankYml = yamlMapper.readValue(data, FlankYml::class.java)
            val gcloudYml = yamlMapper.readValue(data, GcloudYml::class.java)
            val androidGcloudYml = yamlMapper.readValue(data, AndroidGcloudYml::class.java)

            return AndroidArgs(
                gcloudYml,
                androidGcloudYml,
                flankYml,
                data,
                cli
            )
        }
    }
}
