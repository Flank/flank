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
import ftl.args.ArgsHelper.calculateShards
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.createJunitBucket
import ftl.args.ArgsHelper.evaluateFilePath
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.ArgsToString.devicesToString
import ftl.args.ArgsToString.listToString
import ftl.args.ArgsToString.mapToString
import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.AndroidGcloudYmlParams
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.YamlDeprecated
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.FtlConstants.useMock
import ftl.filter.TestFilters
import ftl.gc.GcStorage
import ftl.util.Utils
import java.nio.file.Files
import java.nio.file.Path
import kotlinx.coroutines.runBlocking

// set default values, init properties, etc.
class AndroidArgs(
    gcloudYml: GcloudYml,
    androidGcloudYml: AndroidGcloudYml,
    flankYml: FlankYml,
    override val data: String,
    val cli: AndroidRunCommand? = null
) : IArgs {
    private val gcloud = gcloudYml.gcloud
    override val resultsBucket: String
    override val resultsDir = cli?.resultsDir ?: gcloud.resultsDir
    override val recordVideo = cli?.recordVideo ?: cli?.noRecordVideo?.not() ?: gcloud.recordVideo
    override val testTimeout = cli?.timeout ?: gcloud.timeout
    override val async = cli?.async ?: gcloud.async
    override val resultsHistoryName = cli?.resultsHistoryName ?: gcloud.resultsHistoryName
    override val flakyTestAttempts = cli?.flakyTestAttempts ?: gcloud.flakyTestAttempts

    private val androidGcloud = androidGcloudYml.gcloud
    var appApk = cli?.app ?: androidGcloud.app
    var testApk = cli?.test ?: androidGcloud.test
    val autoGoogleLogin = cli?.autoGoogleLogin ?: cli?.noAutoGoogleLogin?.not() ?: androidGcloud.autoGoogleLogin

    // We use not() on noUseOrchestrator because if the flag is on, useOrchestrator needs to be false
    val useOrchestrator = cli?.useOrchestrator ?: cli?.noUseOrchestrator?.not() ?: androidGcloud.useOrchestrator
    val environmentVariables = cli?.environmentVariables ?: androidGcloud.environmentVariables
    val directoriesToPull = cli?.directoriesToPull ?: androidGcloud.directoriesToPull
    val performanceMetrics = cli?.performanceMetrics ?: cli?.noPerformanceMetrics?.not() ?: androidGcloud.performanceMetrics
    val testTargets = cli?.testTargets ?: androidGcloud.testTargets
    val devices = cli?.device ?: androidGcloud.device

    private val flank = flankYml.flank
    override val maxTestShards = cli?.maxTestShards ?: flank.maxTestShards
    override val shardTime = cli?.shardTime ?: flank.shardTime
    override val repeatTests = cli?.repeatTests ?: flank.repeatTests
    override val smartFlankGcsPath = cli?.smartFlankGcsPath ?: flank.smartFlankGcsPath
    override val smartFlankDisableUpload = cli?.smartFlankDisableUpload ?: flank.smartFlankDisableUpload
    override val testTargetsAlwaysRun = cli?.testTargetsAlwaysRun ?: flank.testTargetsAlwaysRun
    override val filesToDownload = cli?.filesToDownload ?: flank.filesToDownload
    override val disableSharding = cli?.disableSharding ?: flank.disableSharding
    override val project = cli?.project ?: flank.project
    override val localResultDir = cli?.localResultDir ?: flank.localResultDir

    // computed properties not specified in yaml
    override val testShardChunks: List<List<String>> by lazy {
        if (disableSharding) return@lazy listOf(emptyList<String>())

        // Download test APK if necessary so it can be used to validate test methods
        var testLocalApk = testApk
        if (testApk.startsWith(FtlConstants.GCS_PREFIX)) {
            runBlocking {
                testLocalApk = GcStorage.downloadTestApk(this@AndroidArgs)
            }
        }

        val filteredTests = getTestMethods(testLocalApk)
        calculateShards(filteredTests, this)
    }

    init {
        resultsBucket = createGcsBucket(project, cli?.resultsBucket ?: gcloud.resultsBucket)
        createJunitBucket(project, flank.smartFlankGcsPath)

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
            .distinct()
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
      results-dir: $resultsDir
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
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
      flaky-test-attempts: $flakyTestAttempts

    flank:
      max-test-shards: $maxTestShards
      shard-time: $shardTime
      repeat-tests: $repeatTests
      smart-flank-gcs-path: $smartFlankGcsPath
      smart-flank-disable-upload: $smartFlankDisableUpload
      files-to-download:
${listToString(filesToDownload)}
      test-targets-always-run:
${listToString(testTargetsAlwaysRun)}
      disable-sharding: $disableSharding
      project: $project
      local-result-dir: $localResultDir
   """.trimIndent()
    }

    companion object : IArgsCompanion {
        override val validArgs by lazy {
            mergeYmlMaps(GcloudYml, AndroidGcloudYml, FlankYml)
        }

        fun load(data: Path, cli: AndroidRunCommand? = null): AndroidArgs =
            load(String(Files.readAllBytes(data)), cli)

        fun load(yamlData: String, cli: AndroidRunCommand? = null): AndroidArgs {
            val data = YamlDeprecated.modifyAndThrow(yamlData, android = true)

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

        fun default(): AndroidArgs {
            return AndroidArgs(GcloudYml(), AndroidGcloudYml(AndroidGcloudYmlParams(app = ".", test = ".")), FlankYml(), "", AndroidRunCommand())
        }
    }
}
