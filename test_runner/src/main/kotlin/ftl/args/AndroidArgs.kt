package ftl.args

import com.google.common.annotations.VisibleForTesting
import ftl.android.AndroidCatalog
import ftl.android.IncompatibleModelVersion
import ftl.android.SupportedDeviceConfig
import ftl.android.UnsupportedModelId
import ftl.android.UnsupportedVersionId
import ftl.args.ArgsHelper.assertCommonProps
import ftl.args.ArgsHelper.assertFileExists
import ftl.args.ArgsHelper.assertGcsFileExists
import ftl.args.ArgsHelper.createGcsBucket
import ftl.args.ArgsHelper.createJunitBucket
import ftl.args.ArgsHelper.evaluateFilePath
import ftl.args.ArgsHelper.mergeYmlMaps
import ftl.args.ArgsHelper.yamlMapper
import ftl.args.ArgsToString.apksToString
import ftl.args.ArgsToString.listToString
import ftl.args.ArgsToString.mapToString
import ftl.args.ArgsToString.objectsToString
import ftl.args.yml.AndroidFlankYml
import ftl.args.yml.AndroidGcloudYml
import ftl.args.yml.FlankYml
import ftl.args.yml.GcloudYml
import ftl.args.yml.AppTestPair
import ftl.args.yml.AndroidGcloudYmlParams
import ftl.args.yml.YamlDeprecated
import ftl.cli.firebase.test.android.AndroidRunCommand
import ftl.config.Device
import ftl.config.FtlConstants
import ftl.config.parseRoboDirectives
import ftl.util.FlankFatalError
import java.io.Reader
import java.nio.file.Files
import java.nio.file.Path

// set default values, init properties, etc.
class AndroidArgs(
    gcloudYml: GcloudYml,
    androidGcloudYml: AndroidGcloudYml,
    flankYml: FlankYml,
    androidFlankYml: AndroidFlankYml,
    override val data: String,
    val cli: AndroidRunCommand? = null
) : IArgs {

    private val gcloud = gcloudYml.gcloud
    override val resultsBucket: String
    override val resultsDir = (cli?.resultsDir ?: gcloud.resultsDir)?.also { assertFileExists(it, "from results-dir") }
    override val recordVideo = cli?.recordVideo ?: cli?.noRecordVideo?.not() ?: gcloud.recordVideo
    override val testTimeout = cli?.timeout ?: gcloud.timeout
    override val async = cli?.async ?: gcloud.async
    override val resultsHistoryName = cli?.resultsHistoryName ?: gcloud.resultsHistoryName
    override val flakyTestAttempts = cli?.flakyTestAttempts ?: gcloud.flakyTestAttempts

    private val androidGcloud = androidGcloudYml.gcloud
    var appApk = (cli?.app ?: androidGcloud.app ?: throw FlankFatalError("app is not set")).processFilePath("from app")
    var testApk = (cli?.test ?: androidGcloud.test)?.processFilePath("from test")
    val additionalApks =
        (cli?.additionalApks ?: androidGcloud.additionalApks).map { it.processFilePath("from additional-apks") }
    val autoGoogleLogin = cli?.autoGoogleLogin ?: cli?.noAutoGoogleLogin?.not() ?: androidGcloud.autoGoogleLogin

    // We use not() on noUseOrchestrator because if the flag is on, useOrchestrator needs to be false
    val useOrchestrator = cli?.useOrchestrator ?: cli?.noUseOrchestrator?.not() ?: androidGcloud.useOrchestrator
    val roboDirectives =
        cli?.roboDirectives?.parseRoboDirectives() ?: androidGcloud.roboDirectives.parseRoboDirectives()
    val roboScript = (cli?.roboScript ?: androidGcloud.roboScript)?.processFilePath("from roboScript")
    val environmentVariables = cli?.environmentVariables ?: androidGcloud.environmentVariables
    val directoriesToPull = cli?.directoriesToPull ?: androidGcloud.directoriesToPull
    val otherFiles = (cli?.otherFiles ?: androidGcloud.otherFiles).map { (devicePath, filePath) ->
        devicePath to filePath.processFilePath("from otherFiles")
    }.toMap()
    val performanceMetrics =
        cli?.performanceMetrics ?: cli?.noPerformanceMetrics?.not() ?: androidGcloud.performanceMetrics
    val numUniformShards = cli?.numUniformShards ?: androidGcloud.numUniformShards
    val testRunnerClass = cli?.testRunnerClass ?: androidGcloud.testRunnerClass
    val testTargets = cli?.testTargets ?: androidGcloud.testTargets.filterNotNull()
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
    override val localResultDir = cli?.localResultsDir ?: flank.localResultsDir
    override val runTimeout = cli?.runTimeout ?: flank.runTimeout
    override val useLegacyJUnitResult = cli?.useLegacyJUnitResult ?: flank.useLegacyJUnitResult
    override val clientDetails = cli?.clientDetails ?: gcloud.clientDetails
    override val networkProfile = cli?.networkProfile ?: gcloud.networkProfile
    override val ignoreFailedTests = cli?.ignoreFailedTests ?: flank.ignoreFailedTests
    override val keepFilePath = cli?.keepFilePath ?: flank.keepFilePath

    private val androidFlank = androidFlankYml.flank
    val additionalAppTestApks = (cli?.additionalAppTestApks ?: androidFlank.additionalAppTestApks).map { (app, test) ->
        AppTestPair(
            app = app?.processFilePath("from additional-app-test-apks.app"),
            test = test.processFilePath("from additional-app-test-apks.test")
        )
    }

    init {
        resultsBucket = createGcsBucket(project, cli?.resultsBucket ?: gcloud.resultsBucket)
        createJunitBucket(project, flank.smartFlankGcsPath)

        devices.forEach { device -> assertDeviceSupported(device) }

        if (numUniformShards != null && maxTestShards > 1) throw FlankFatalError(
            "Option num-uniform-shards cannot be specified along with max-test-shards. Use only one of them"
        )

        if (!(isRoboTest xor isInstrumentationTest)) throw FlankFatalError(
            "One of following options must be specified [test, robo-directives, robo-script]."
        )

        // Using both roboDirectives and roboScript may hang test execution on FTL
        if (roboDirectives.isNotEmpty() && roboScript != null) throw FlankFatalError(
            "Options robo-directives and robo-script are mutually exclusive, use only one of them."
        )

        assertCommonProps(this)
    }

    val isInstrumentationTest get() = testApk != null
    val isRoboTest get() = roboDirectives.isNotEmpty() || roboScript != null

    private fun assertDeviceSupported(device: Device) {
        when (val deviceConfigTest = AndroidCatalog.supportedDeviceConfig(device.model, device.version, this.project)) {
            SupportedDeviceConfig -> {
            }
            UnsupportedModelId -> throw RuntimeException(
                "Unsupported model id, '${device.model}'\nSupported model ids: ${AndroidCatalog.androidModelIds(
                    this.project
                )}"
            )
            UnsupportedVersionId -> throw RuntimeException(
                "Unsupported version id, '${device.version}'\nSupported Version ids: ${AndroidCatalog.androidVersionIds(
                    this.project
                )}"
            )
            is IncompatibleModelVersion -> throw RuntimeException("Incompatible model, '${device.model}', and version, '${device.version}'\nSupported version ids for '${device.model}': $deviceConfigTest")
        }
    }

    // Note: environmentVariables may contain secrets and are not printed for security reasons.
    override fun toString(): String {
        return """
AndroidArgs
    gcloud:
      results-bucket: $resultsBucket
      results-dir: $resultsDir
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
      client-details: ${mapToString(clientDetails)}
      network-profile: $networkProfile
      results-history-name: $resultsHistoryName
      # Android gcloud
      app: $appApk
      test: $testApk
      additional-apks: ${listToString(additionalApks)}
      auto-google-login: $autoGoogleLogin
      use-orchestrator: $useOrchestrator
      directories-to-pull:${listToString(directoriesToPull)}
      other-files:${mapToString(otherFiles)}
      performance-metrics: $performanceMetrics
      num-uniform-shards: $numUniformShards
      test-runner-class: $testRunnerClass
      test-targets:${listToString(testTargets)}
      robo-directives:${objectsToString(roboDirectives)}
      robo-script: $roboScript
      device:${objectsToString(devices)}
      num-flaky-test-attempts: $flakyTestAttempts

    flank:
      max-test-shards: $maxTestShards
      shard-time: $shardTime
      num-test-runs: $repeatTests
      smart-flank-gcs-path: $smartFlankGcsPath
      smart-flank-disable-upload: $smartFlankDisableUpload
      files-to-download:${listToString(filesToDownload)}
      test-targets-always-run:${listToString(testTargetsAlwaysRun)}
      disable-sharding: $disableSharding
      project: $project
      local-result-dir: $localResultDir
      # Android Flank Yml
      keep-file-path: $keepFilePath
      additional-app-test-apks:${apksToString(additionalAppTestApks)}
      run-timeout: $runTimeout
      legacy-junit-result: $useLegacyJUnitResult
      ignore-failed-tests: $ignoreFailedTests
   """.trimIndent()
    }

    companion object : IArgsCompanion {
        override val validArgs by lazy {
            mergeYmlMaps(GcloudYml, AndroidGcloudYml, FlankYml, AndroidFlankYml)
        }

        fun load(yamlPath: Path, cli: AndroidRunCommand? = null): AndroidArgs =
            load(Files.newBufferedReader(yamlPath), cli)

        @VisibleForTesting
        internal fun load(yamlReader: Reader, cli: AndroidRunCommand? = null): AndroidArgs {

            val data = YamlDeprecated.modifyAndThrow(yamlReader, android = true)
            val flankYml = yamlMapper.readValue(data, FlankYml::class.java)
            val gcloudYml = yamlMapper.readValue(data, GcloudYml::class.java)
            val androidGcloudYml = yamlMapper.readValue(data, AndroidGcloudYml::class.java)
            val androidFlankYml = yamlMapper.readValue(data, AndroidFlankYml::class.java)

            return AndroidArgs(
                gcloudYml,
                androidGcloudYml,
                flankYml,
                androidFlankYml,
                data,
                cli
            )
        }

        fun default(): AndroidArgs {
            return AndroidArgs(
                GcloudYml(),
                AndroidGcloudYml(AndroidGcloudYmlParams(app = ".", test = ".")),
                FlankYml(),
                AndroidFlankYml(),
                "",
                AndroidRunCommand()
            )
        }
    }
}

private fun String.processFilePath(name: String): String =
    if (startsWith(FtlConstants.GCS_PREFIX))
        this.also { assertGcsFileExists(it) } else
        evaluateFilePath(this).also { assertFileExists(it, name) }
