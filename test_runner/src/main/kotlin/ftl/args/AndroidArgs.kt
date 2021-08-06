package ftl.args

import com.fasterxml.jackson.annotation.JsonIgnore
import flank.tool.analytics.AnonymizeInStatistics
import flank.tool.analytics.IgnoreInStatistics
import ftl.api.ShardChunks
import ftl.args.yml.AppTestPair
import ftl.args.yml.Type
import ftl.run.ANDROID_SHARD_FILE
import ftl.run.model.AndroidTestShards
import java.nio.file.Paths

data class AndroidArgs(
    @get:JsonIgnore
    val commonArgs: CommonArgs,

    @property:AnonymizeInStatistics
    val appApk: String?,

    @property:AnonymizeInStatistics
    val testApk: String?,

    @property:AnonymizeInStatistics
    val additionalApks: List<String>,

    val autoGoogleLogin: Boolean,
    val useOrchestrator: Boolean,

    @property:AnonymizeInStatistics
    val roboDirectives: List<FlankRoboDirective>,

    @property:AnonymizeInStatistics
    val roboScript: String?,

    @property:AnonymizeInStatistics
    val environmentVariables: Map<String, String>, // should not be printed, because could contain sensitive information
    val grantPermissions: String?,

    @property:AnonymizeInStatistics
    val scenarioLabels: List<String>,

    @property:AnonymizeInStatistics
    val obbFiles: List<String>,

    @property:AnonymizeInStatistics
    val obbNames: List<String>,
    val performanceMetrics: Boolean,
    val numUniformShards: Int?,

    @property:AnonymizeInStatistics
    val testRunnerClass: String?,

    @property:AnonymizeInStatistics
    val testTargets: List<String>,

    @property:AnonymizeInStatistics
    val additionalAppTestApks: List<AppTestPair>,
    override val useLegacyJUnitResult: Boolean,
    val obfuscateDumpShards: Boolean,

    @property:AnonymizeInStatistics
    val testTargetsForShard: ShardChunks,

    @property:IgnoreInStatistics
    val parameterizedTests: String,

    @property:IgnoreInStatistics
    val customSharding: Map<String, AndroidTestShards>
) : IArgs by commonArgs {
    companion object : AndroidArgsCompanion()

    override fun toString(): String {
        return """
AndroidArgs
    gcloud:
      results-bucket: $resultsBucket
      results-dir: $resultsDir
      record-video: $recordVideo
      timeout: $testTimeout
      async: $async
      client-details:${ArgsToString.mapToString(clientDetails)}
      network-profile: $networkProfile
      results-history-name: $resultsHistoryName
      # Android gcloud
      app: $appApk
      test: $testApk
      additional-apks:${ArgsToString.listToString(additionalApks)}
      auto-google-login: $autoGoogleLogin
      use-orchestrator: $useOrchestrator
      directories-to-pull:${ArgsToString.listToString(directoriesToPull)}
      grant-permissions: $grantPermissions
      type: ${type?.ymlName}
      other-files:${ArgsToString.mapToString(otherFiles)}
      scenario-numbers:${ArgsToString.listToString(scenarioNumbers)}
      scenario-labels:${ArgsToString.listToString(scenarioLabels)}
      obb-files:${ArgsToString.listToString(obbFiles)}
      obb-names:${ArgsToString.listToString(obbNames)}
      performance-metrics: $performanceMetrics
      num-uniform-shards: $numUniformShards
      test-runner-class: $testRunnerClass
      test-targets:${ArgsToString.listToString(testTargets)}
      robo-directives:${ArgsToString.objectsToString(roboDirectives)}
      robo-script: $roboScript
      device:${ArgsToString.objectsToString(devices)}
      num-flaky-test-attempts: $flakyTestAttempts
      test-targets-for-shard:${ArgsToString.listOfListToString(testTargetsForShard)}
      fail-fast: $failFast
      parameterized-tests: $parameterizedTests

    flank:
      max-test-shards: $maxTestShards
      shard-time: $shardTime
      num-test-runs: $repeatTests
      smart-flank-gcs-path: $smartFlankGcsPath
      smart-flank-disable-upload: $smartFlankDisableUpload
      default-test-time: $defaultTestTime
      use-average-test-time-for-new-tests: $useAverageTestTimeForNewTests
      files-to-download:${ArgsToString.listToString(filesToDownload)}
      test-targets-always-run:${ArgsToString.listToString(testTargetsAlwaysRun)}
      disable-sharding: $disableSharding
      project: $project
      local-result-dir: $localResultDir
      full-junit-result: $fullJUnitResult
      # Android Flank Yml
      keep-file-path: $keepFilePath
      additional-app-test-apks:${ArgsToString.apksToString(additionalAppTestApks)}
      run-timeout: $runTimeout
      legacy-junit-result: $useLegacyJUnitResult
      ignore-failed-tests: $ignoreFailedTests
      output-style: ${outputStyle.name.lowercase()}
      disable-results-upload: $disableResultsUpload
      default-class-test-time: $defaultClassTestTime
      disable-usage-statistics: $disableUsageStatistics
      output-report: $outputReportType
      skip-config-validation: $skipConfigValidation
      custom-sharding-json: $customShardingJson
        """.trimIndent()
    }
}

// Changes these based on type
val AndroidArgs.isDontAutograntPermissions
    get() = !(grantPermissions.isNotNull() && (grantPermissions.equals("all")))

val AndroidArgs.isInstrumentationTest
    get() = if (type != null) (type == Type.INSTRUMENTATION) else validateInstrumentation()

fun AndroidArgs.validateInstrumentation() = (
    appApk.isNotNull() && testApk.isNotNull() ||
        additionalAppTestApks.isNotEmpty() &&
        (appApk.isNotNull() || additionalAppTestApks.all { (app, _) -> app.isNotNull() })
    )

val AndroidArgs.isRoboTest
    get() = if (type != null) type == Type.ROBO else validateRobo()

fun AndroidArgs.validateRobo() = (appApk.isNotNull() && (roboDirectives.isNotEmpty() || roboScript.isNotNull()))

private val AndroidArgs.checkForSanityRobo
    get() = appApk.isNotNull() &&
        testApk.isNull() &&
        roboScript.isNull() &&
        additionalAppTestApks.isEmpty()

val AndroidArgs.isSanityRobo
    get() = if (type != null) (type == Type.ROBO) && checkForSanityRobo else checkForSanityRobo

val AndroidArgs.isGameLoop
    get() = if (type == null) false else (type == Type.GAMELOOP)

val AndroidArgs.shardsFilePath
    get() = Paths.get(localStorageDirectory, ANDROID_SHARD_FILE).toAbsolutePath().toString()

private fun String?.isNull() = this == null
private fun String?.isNotNull() = isNull().not()
